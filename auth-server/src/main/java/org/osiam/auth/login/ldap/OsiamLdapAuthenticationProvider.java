/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.auth.login.ldap;

import javax.inject.Inject;

import org.osiam.auth.configuration.LdapConfiguration;
import org.osiam.auth.exception.LdapAuthenticationProcessException;
import org.osiam.auth.login.ResourceServerConnector;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class OsiamLdapAuthenticationProvider extends LdapAuthenticationProvider {

    @Inject
    private ResourceServerConnector resourceServerConnector;

    @Value("${org.osiam.auth-server.ldap.sync-user-data:true}")
    private boolean syncUserData;

    private OsiamLdapUserContextMapper osiamLdapUserContextMapper;

    public OsiamLdapAuthenticationProvider(LdapAuthenticator authenticator,
            LdapAuthoritiesPopulator authoritiesPopulator, OsiamLdapUserContextMapper osiamLdapUserContextMapper) {
        super(authenticator, authoritiesPopulator);
        this.osiamLdapUserContextMapper = osiamLdapUserContextMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        Preconditions.checkArgument(authentication instanceof OsiamLdapAuthentication,
                "OsiamLdapAuthenticationProvider only supports OsiamLdapAuthentication.");

        final OsiamLdapAuthentication userToken = (OsiamLdapAuthentication) authentication;

        String username = userToken.getName();
        String password = (String) authentication.getCredentials();

        logger.debug("Processing authentication request for user: " + username);

        if (Strings.isNullOrEmpty(username)) {
            throw new BadCredentialsException(messages.getMessage("LdapAuthenticationProvider.emptyUsername",
                    "Empty Username"));
        }

        if (Strings.isNullOrEmpty(password)) {
            throw new BadCredentialsException(messages.getMessage("AbstractLdapAuthenticationProvider.emptyPassword",
                    "Empty Password"));
        }
        
        User user = resourceServerConnector.getUserByUsername(username);
        checkIfInternalUserExists(user);

        DirContextOperations userData = doAuthentication(userToken);

        OsiamLdapUserDetailsImpl ldapUser = osiamLdapUserContextMapper.mapUserFromContext(userData,
                authentication.getName(),
                loadUserAuthorities(userData, authentication.getName(), (String) authentication.getCredentials()));

        ldapUser = synchronizeLdapData(userData, ldapUser, user);

        return createSuccessfulAuthentication(userToken, ldapUser);
    }

    private void checkIfInternalUserExists(User user) {
        if (user != null && !hasAuthServerExtension(user)) {
            throw new LdapAuthenticationProcessException("Can't create the '"
                    + LdapConfiguration.LDAP_PROVIDER
                    + "' user with the username '"
                    + user.getUserName() + "'. An internal user with the same username exists.");
        }
    }

    private boolean hasAuthServerExtension(User user) {
        if (!user.isExtensionPresent(LdapConfiguration.AUTH_EXTENSION)) {
            return false;
        }
        Extension authExtension = user.getExtension(LdapConfiguration.AUTH_EXTENSION);
        return authExtension.isFieldPresent("origin")
                && authExtension.getFieldAsString("origin").equals(LdapConfiguration.LDAP_PROVIDER);
    }
    
    private OsiamLdapUserDetailsImpl synchronizeLdapData(DirContextOperations ldapUserData,
            OsiamLdapUserDetailsImpl ldapUser, User user) {

        boolean userExists = user != null;
        
        if (!userExists) {
            user = osiamLdapUserContextMapper.mapUser(ldapUserData);
            user = resourceServerConnector.createUser(user);
        } else if (syncUserData && userExists) {
            UpdateUser updateUser = osiamLdapUserContextMapper.mapUpdateUser(user, ldapUserData);
            user = resourceServerConnector.updateUser(user.getId(), updateUser);
        }

        ldapUser.setId(user.getId());
        return ldapUser;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return OsiamLdapAuthentication.class.isAssignableFrom(authentication);
    }
}
