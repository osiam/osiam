/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.auth.login.internal;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.osiam.resources.exception.ResourceNotFoundException;
import org.osiam.resources.provisioning.SCIMUserProvisioning;
import org.osiam.resources.scim.Role;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class InternalAuthenticationProvider implements AuthenticationProvider,
        ApplicationListener<AbstractAuthenticationEvent> {

    private final Map<String, Integer> accessCounter = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Date> lastFailedLogin = Collections.synchronizedMap(new HashMap<>());

    private final SCIMUserProvisioning userProvisioning;
    private final ShaPasswordEncoder shaPasswordEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Integer maxLoginFailures;
    private final Integer lockTimeout;

    @Autowired
    public InternalAuthenticationProvider(SCIMUserProvisioning userProvisioning,
                                          ShaPasswordEncoder shaPasswordEncoder,
                                          BCryptPasswordEncoder bCryptPasswordEncoder,
                                          @Value("${osiam.tempLock.count:0}") Integer maxLoginFailures,
                                          @Value("${osiam.tempLock.timeout:0}") Integer lockTimeout) {
        this.userProvisioning = userProvisioning;
        this.shaPasswordEncoder = shaPasswordEncoder;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.maxLoginFailures = maxLoginFailures;
        this.lockTimeout = lockTimeout;
    }

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

        assertUserNotLocked(username);

        User user;
        try {
            user = userProvisioning.getByUsernameWithPassword(username);
        } catch (ResourceNotFoundException rnfe) {
            throw new BadCredentialsException("The user with the username '" + username + "' doesn't exist!", rnfe);
        }

        if (user.isActive() != Boolean.TRUE) {
            throw new DisabledException("The user with the username '" + username + "' is disabled!");
        }

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            if (shaPasswordEncoder.isPasswordValid(user.getPassword(), password, user.getId())) {
                User replaceUser = new User.Builder(user).setPassword(password).build();
                userProvisioning.replace(user.getId(), replaceUser);
            } else {
                throw new BadCredentialsException("Bad credentials");
            }
        }

        User authUser = new User.Builder(username).setId(user.getId()).build();

        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getValue()))
                .collect(Collectors.toList());

        return new InternalAuthentication(authUser, password, grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return InternalAuthentication.class.isAssignableFrom(authentication);
    }

    private void assertUserNotLocked(String username) {
        if (isLockMechanismDisabled()) {
            return;
        }

        Date logindate = lastFailedLogin.get(username);

        if (logindate != null && isWaitTimeOver(logindate)) {
            accessCounter.remove(username);
            lastFailedLogin.remove(username);
        }
        if (accessCounter.get(username) != null && accessCounter.get(username) >= maxLoginFailures) {
            throw new LockedException("The user '" + username + "' is temporary locked.");
        }
    }

    private boolean isWaitTimeOver(Date startWaitingDate) {
        return System.currentTimeMillis() - startWaitingDate.getTime() >= TimeUnit.SECONDS.toMillis(lockTimeout);
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

        if (appEvent instanceof AuthenticationSuccessEvent &&
                accessCounter.containsKey(currentUserName) &&
                accessCounter.get(currentUserName) < maxLoginFailures) {

            accessCounter.remove(currentUserName);
            lastFailedLogin.remove(currentUserName);
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
