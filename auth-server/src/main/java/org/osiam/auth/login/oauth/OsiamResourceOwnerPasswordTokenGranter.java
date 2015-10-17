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

package org.osiam.auth.login.oauth;

import java.util.*;

import org.osiam.auth.login.internal.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.password.*;
import org.springframework.security.oauth2.provider.token.*;

public class OsiamResourceOwnerPasswordTokenGranter extends ResourceOwnerPasswordTokenGranter {

    private final AuthenticationManager authenticationManager;

    public OsiamResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService, OAuth2RequestFactory oAuth2RequestFactory) {
        super(authenticationManager, tokenServices, clientDetailsService, oAuth2RequestFactory);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String username = parameters.get("username");
        String password = parameters.get("password");
        // Protect from downstream leaks of password
        parameters.remove("password");

        Authentication userAuth = new InternalAuthentication(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException e) {
            // AccountStatusException: covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            // BadCredentialsException: If the username/password are wrong the spec says we should send 400/bad grant
            throw new InvalidGrantException(e.getMessage(), e);
        }

        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
