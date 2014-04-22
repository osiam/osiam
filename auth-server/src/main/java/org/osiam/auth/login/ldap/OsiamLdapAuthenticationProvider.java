package org.osiam.auth.login.ldap;

import javax.inject.Inject;

import org.osiam.auth.configuration.LdapConfiguration;
import org.osiam.auth.exception.LdapAuthenticationProcessException;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.osiam.security.authentication.OsiamUserDetailsService;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class OsiamLdapAuthenticationProvider extends LdapAuthenticationProvider {

    @Inject
    private OsiamUserDetailsService userDetailsService;

    @Inject
    private LdapConfiguration ldapConfiguration;

    @Inject
    private OsiamLdapUserContextMapper osiamLdapUserContextMapper;

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

        OsiamLdapUserDetailsImpl ldapUser = osiamLdapUserContextMapper.mapUserFromContext(userData,
                authentication.getName(),
                loadUserAuthorities(userData, authentication.getName(), (String) authentication.getCredentials()));

        ldapUser = synchronizeLdapData(userData, ldapUser);

        return createSuccessfulAuthentication(userToken, ldapUser);
    }

    public OsiamLdapUserDetailsImpl synchronizeLdapData(DirContextOperations ldapUserData,
            OsiamLdapUserDetailsImpl ldapUser) {
        String userName = ldapUserData.getStringAttribute(ldapConfiguration.getScimLdapAttributes().get("userName"));

        User user = userDetailsService.getUserByUsername(userName);

        boolean userExists = user != null;

        if (userExists
                && (!user.isExtensionPresent(LdapConfiguration.AUTH_EXTENSION)
                        || !user.getExtension(LdapConfiguration.AUTH_EXTENSION).isFieldPresent("origin")
                        || !user.getExtension(LdapConfiguration.AUTH_EXTENSION).getFieldAsString("origin")
                        .equals(LdapConfiguration.LDAP_PROVIDER))) {

            throw new LdapAuthenticationProcessException("Can't create the '"
                    + LdapConfiguration.LDAP_PROVIDER
                    + "' user with the username '"
                    + userName + "'. An internal user with the same username exists.");
        }

        if (!userExists) {
            user = osiamLdapUserContextMapper.mapUser(ldapUserData);
            user = userDetailsService.createUser(user);
        } else if (ldapConfiguration.isSyncUserData() && userExists) {
            UpdateUser updateUser = osiamLdapUserContextMapper.mapUpdateUser(user, ldapUserData);
            user = userDetailsService.updateUser(user.getId(), updateUser);
        }

        ldapUser.setId(user.getId());
        return ldapUser;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OsiamLdapAuthentication.class.isAssignableFrom(authentication);
    }
}
