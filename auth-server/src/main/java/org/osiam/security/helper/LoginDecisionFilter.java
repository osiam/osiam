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

package org.osiam.security.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osiam.auth.login.internal.InternalAuthentication;
import org.osiam.auth.login.ldap.OsiamLdapAuthentication;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.google.common.base.Strings;

public class LoginDecisionFilter extends AbstractAuthenticationProcessingFilter implements
        ApplicationListener<AbstractAuthenticationEvent> {

    @Value("${org.osiam.auth-server.tempLock.count:0}")
    private Integer maxLoginFailures;

    private boolean postOnly = true;
    private final Map<String, Integer> accessCounter = Collections.synchronizedMap(new HashMap<String, Integer>());

    public LoginDecisionFilter() {
        super("/login/check");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        UsernamePasswordAuthenticationToken authRequest = null;

        String username = request.getParameter(getUsernameParameter());
        String password = request.getParameter(getPasswordParameter());

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        checkUserLocking(username);

        String provider = request.getParameter("provider");

        if (!Strings.isNullOrEmpty(provider) && provider.equals("ldap")) {
            authRequest = new OsiamLdapAuthentication(username, password);
        } else {
            authRequest = new InternalAuthentication(username, password, new ArrayList<GrantedAuthority>());
        }

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private void checkUserLocking(String username) {
        if(isLockMechanismDisabled()) {
            return;
        }
        if (accessCounter.get(username) != null && accessCounter.get(username) >= maxLoginFailures) {
            throw new LockedException("The user '" + username + "' is temporary locked.");
        }
    }

    private boolean isLockMechanismDisabled() {
        return maxLoginFailures <= 0;
    }

    /**
     * Provided so that subclasses may configure what is put into the authentication request's details property.
     *
     * @param request
     *        that an authentication request is being created for
     * @param authRequest
     *        the authentication request object that should have its details set
     */
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * Defines whether only HTTP POST requests will be allowed by this filter. If set to true, and an authentication
     * request is received which is not a POST request, an exception will be raised immediately and authentication will
     * not be attempted. The <tt>unsuccessfulAuthentication()</tt> method will be called as if handling a failed
     * authentication.
     * <p>
     * Defaults to <tt>true</tt> but may be overridden by subclasses.
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return "username";
    }

    public final String getPasswordParameter() {
        return "password";
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
                }
            }
        }

        if (appEvent instanceof AuthenticationFailureBadCredentialsEvent) {
            if (accessCounter.containsKey(currentUserName)) {
                accessCounter.put(currentUserName, accessCounter.get(currentUserName) + 1);
            } else {
                accessCounter.put(currentUserName, 1);
            }
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
