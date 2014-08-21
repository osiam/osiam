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

package org.osiam.auth.login.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.osiam.auth.login.ResourceServerConnector;
import org.osiam.resources.scim.Role;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class InternalAuthenticationProvider implements AuthenticationProvider, ApplicationListener<AbstractAuthenticationEvent> {

    @Value("${org.osiam.auth-server.tempLock.count:0}")
    private Integer maxLoginFailures;

    @Value("${org.osiam.auth-server.tempLock.timeout}")
    private Integer lockTimeout;

    private final Map<String, Integer> accessCounter = Collections.synchronizedMap(new HashMap<String, Integer>());
    private final Map<String, Date> lastFailedLogin = Collections.synchronizedMap(new HashMap<String, Date>());

    @Inject
    private ResourceServerConnector resourceServerConnector;

    @Inject
    private ShaPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        Preconditions.checkArgument(authentication instanceof InternalAuthentication,
                "InternalAuthenticationProvider only supports InternalAuthentication.");

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        if (Strings.isNullOrEmpty(username)) {
            throw new BadCredentialsException("InternalAuthenticationProvider: Empty Username");
        }

        if (Strings.isNullOrEmpty(password)) {
            throw new BadCredentialsException("InternalAuthenticationProvider: Empty Password");
        }

        checkUserLocking(username);

        // Determine username
        User user = resourceServerConnector.getUserByUsername(username);

        if (user == null) {
            throw new BadCredentialsException("The user with the username '" + username + "' doesn't exist!");
        }

        if (!user.isActive()) {
            throw new DisabledException("The user with the username '" + username + "' is disabled!");
        }

        String hashedPassword = passwordEncoder.encodePassword(password, user.getId());

        if (resourceServerConnector.searchUserByUserNameAndPassword(username, hashedPassword) == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        User authUser = new User.Builder(username).setId(user.getId()).build();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getValue()));
        }

        return new InternalAuthentication(authUser, password, grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return InternalAuthentication.class.isAssignableFrom(authentication);
    }

    private void checkUserLocking(String username) {
        Date logindate = lastFailedLogin.get(username);

        if(isLockMechanismDisabled()) {
            return;
        }
        if(logindate != null && new Date().getTime()-logindate.getTime() >= (lockTimeout*1000)) {
            accessCounter.remove(username);
            lastFailedLogin.remove(username);
        }
        if (accessCounter.get(username) != null && accessCounter.get(username) >= maxLoginFailures) {
            throw new LockedException("The user '" + username + "' is temporary locked.");
        }
    }

    private boolean isLockMechanismDisabled() {
        return maxLoginFailures <= 0;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent appEvent) {
        String currentUserName = extractUserName(appEvent);
        if (currentUserName == null || isLockMechanismDisabled()) {
            return;
        }

        if (appEvent instanceof AuthenticationSuccessEvent) {
            if (accessCounter.containsKey(currentUserName)) {
                if (accessCounter.get(currentUserName) < maxLoginFailures) {
                    accessCounter.remove(currentUserName);
                    lastFailedLogin.remove(currentUserName);
                }
            }
        }

        if (appEvent instanceof AuthenticationFailureBadCredentialsEvent) {
            if (accessCounter.containsKey(currentUserName)) {
                accessCounter.put(currentUserName, accessCounter.get(currentUserName) + 1);
            } else {
                accessCounter.put(currentUserName, 1);
            }
            lastFailedLogin.put(currentUserName, new Date());
        }
    }

    private String extractUserName(AbstractAuthenticationEvent appEvent) {
        if (appEvent.getSource() != null && appEvent.getSource() instanceof InternalAuthentication) {
            InternalAuthentication internalAuth = (InternalAuthentication) appEvent.getSource();

            if (internalAuth.getPrincipal() != null) {

                if (internalAuth.getPrincipal() instanceof User) {
                    User user = (User) internalAuth.getPrincipal();
                    return user.getUserName();
                }
                if (internalAuth.getPrincipal() instanceof String) {
                    return (String) internalAuth.getPrincipal();
                }
            }
        }

        return null;
    }
}
