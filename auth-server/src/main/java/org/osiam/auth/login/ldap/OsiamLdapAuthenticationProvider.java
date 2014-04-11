package org.osiam.auth.login.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;
import org.osiam.security.authentication.AuthenticationBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;

public class OsiamLdapAuthenticationProvider extends LdapAuthenticationProvider {

    @Inject
    private AuthenticationBean userDetailsService;
    
    @Value("${org.osiam.ldap.sync.user.data:true}")
    private boolean syncUserData;

    public OsiamLdapAuthenticationProvider(LdapAuthenticator authenticator,
            LdapAuthoritiesPopulator authoritiesPopulator) {
        super(authenticator, authoritiesPopulator);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(OsiamLdapAuthentication.class, authentication,
                messages.getMessage("LdapAuthenticationProvider.onlySupports",
                        "Only OsiamLdapAuthentication is supported"));

        final OsiamLdapAuthentication userToken = (OsiamLdapAuthentication) authentication;

        String username = userToken.getName();
        String password = (String) authentication.getCredentials();

        if (logger.isDebugEnabled()) {
            logger.debug("Processing authentication request for user: " + username);
        }

        if (!StringUtils.hasLength(username)) {
            throw new BadCredentialsException(messages.getMessage("LdapAuthenticationProvider.emptyUsername",
                    "Empty Username"));
        }

        if (!StringUtils.hasLength(password)) {
            throw new BadCredentialsException(messages.getMessage("AbstractLdapAuthenticationProvider.emptyPassword",
                    "Empty Password"));
        }

        Assert.notNull(password, "Null password was supplied in authentication token");

        DirContextOperations userData = doAuthentication(userToken);

        UserDetails user = userDetailsContextMapper.mapUserFromContext(userData, authentication.getName(),
                loadUserAuthorities(userData, authentication.getName(), (String) authentication.getCredentials()));

        createOrUpdateUser(userData);
        
        return createSuccessfulAuthentication(userToken, user);
    }

    @Override
    protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData,
            String username, String password) {
        return getAuthoritiesPopulator().getGrantedAuthorities(userData, username);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OsiamLdapAuthentication.class.isAssignableFrom(authentication));
    }
    
    private void createOrUpdateUser(DirContextOperations userData) {
        String userName = userData.getStringAttribute("uid");
        
        UserDetails user = userDetailsService.loadUserByUsername(userName);
        if((syncUserData && user != null) || user == null) {
            
            String displayName = userData.getStringAttribute("displayName");
            String givenName = userData.getStringAttribute("givenName");
            String familyName = userData.getStringAttribute("sn");
            String email = userData.getStringAttribute("mail");
            
            boolean foundName = false;
            Name.Builder nameBuilder = new Name.Builder();
    
            User.Builder builder = new User.Builder(userName);
            
            if (!Strings.isNullOrEmpty(displayName)) {
                builder.setDisplayName(displayName);
            }
            if (!Strings.isNullOrEmpty(givenName)) {
                nameBuilder.setGivenName(givenName);
                foundName = true;
            }
            if (!Strings.isNullOrEmpty(familyName)) {
                nameBuilder.setFamilyName(familyName);
                foundName = true;
            }
            if (!Strings.isNullOrEmpty(email)) {
                Email.Builder emailBuilder = new Email.Builder().setValue(email);
                List<Email> emails = new ArrayList<Email>();
                emails.add(emailBuilder.build());
                builder.setEmails(emails);
            }
            
            if(foundName) {
                builder.setName(nameBuilder.build());
            }
            
            builder.setActive(true);
    
            userDetailsService.createNewUser(builder.build());
        }
    }
}
