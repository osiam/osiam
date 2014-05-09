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

package org.osiam.auth.token;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.osiam.auth.oauth_client.OsiamAuthServerClientProvider;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;

/**
 * TokenProvider which created an accesstoken for the auth server client on startup
 * 
 */
@Service
@DependsOn("osiamAuthServerClientProvider")
public class OsiamAccessTokenProvider {

    private AccessToken accessToken;
    
    @Inject
    private DefaultTokenServices tokenServices;
    
    @PostConstruct
    public void createAccessToken() {
        Set<String> scopes = new HashSet<String>();
        scopes.add(Scope.GET.toString());
        scopes.add(Scope.POST.toString());
        scopes.add(Scope.PUT.toString());
        scopes.add(Scope.PATCH.toString());
        scopes.add(Scope.DELETE.toString());

        DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(OsiamAuthServerClientProvider.AUTH_SERVER_CLIENT_ID, scopes);
        authorizationRequest.setApproved(true);
        
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(authorizationRequest, null);

        accessToken = new AccessToken.Builder(tokenServices.createAccessToken(oAuth2Authentication).getValue()).build();
    }
    
    public AccessToken getAccessToken() {
        return accessToken;
    }
}
