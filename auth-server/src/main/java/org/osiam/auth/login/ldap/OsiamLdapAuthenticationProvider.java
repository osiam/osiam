package org.osiam.auth.login.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Email.Type;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.osiam.security.authentication.OsiamUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;

public class OsiamLdapAuthenticationProvider extends LdapAuthenticationProvider {

    private final String LDAP_PROVIDER = "ldap";
    private final String AUTH_EXTENSION = "urn:scim:schemas:osiam:2.0:authentication:server";

    @Inject
    private OsiamUserDetailsService userDetailsService;

    @Value("${org.osiam.auth.ldap.sync.user.data:true}")
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

        OsiamLdapUserDetailsImpl ldapUser = (OsiamLdapUserDetailsImpl) userDetailsContextMapper.mapUserFromContext(
                userData, authentication.getName(),
                loadUserAuthorities(userData, authentication.getName(), (String) authentication.getCredentials()));

        User scimUser = createOrUpdateUser(userData);
        ldapUser.setId(scimUser.getId());

        return createSuccessfulAuthentication(userToken, ldapUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OsiamLdapAuthentication.class.isAssignableFrom(authentication);
    }

    private User createOrUpdateUser(DirContextOperations userData) {
        String userName = userData.getStringAttribute("uid");

        User user = userDetailsService.getUserByUsername(userName);

        boolean userExists = user != null;

        String displayName = userData.getStringAttribute("displayName");
        String email = userData.getStringAttribute("mail");

        if (!userExists) {
            Extension extension = new Extension(AUTH_EXTENSION);
            extension.addOrUpdateField("origin", LDAP_PROVIDER);

            User.Builder builder = new User.Builder(userName)
                    .setDisplayName(displayName)
                    .setName(createName(userData))
                    .addExtension(extension)
                    .setActive(true);

            if (!Strings.isNullOrEmpty(email)) {
                Email.Builder emailBuilder = new Email.Builder().setValue(email).setType(new Type(LDAP_PROVIDER));
                List<Email> emails = new ArrayList<Email>();
                emails.add(emailBuilder.build());
                builder.setEmails(emails);
            }

            user = userDetailsService.createUser(builder.build());

        } else if (syncUserData && userExists) {
            UpdateUser.Builder updateUserBuilder = new UpdateUser.Builder()
                    .updateName(createName(userData))
                    .updateDisplayName(displayName);

            updateEmail(updateUserBuilder, user.getEmails(), email);

            user = userDetailsService.updateUser(user.getId(), updateUserBuilder.build());
        }

        return user;
    }

    private void updateEmail(UpdateUser.Builder updateBuilder, List<Email> emails, String emailValue) {
        Email newEmail = new Email.Builder().setValue(emailValue).setType(new Type(LDAP_PROVIDER)).build();
        for (Email email : emails) {
            if (email.getType() != null && email.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deleteEmail(email);
            }
        }
        updateBuilder.addEmail(newEmail);
    }

    private Name createName(DirContextOperations userData) {
        String givenName = userData.getStringAttribute("givenName");
        String familyName = userData.getStringAttribute("sn");
        Name name = null;

        if (!Strings.isNullOrEmpty(givenName) || !Strings.isNullOrEmpty(familyName)) {
            name = new Name.Builder()
                    .setGivenName(givenName)
                    .setFamilyName(familyName)
                    .build();
        }

        return name;
    }
}
