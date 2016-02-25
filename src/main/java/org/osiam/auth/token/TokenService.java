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
package org.osiam.auth.token;

import org.osiam.auth.oauth_client.ClientRepository;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.provisioning.SCIMUserProvisioning;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class TokenService {

    private final TokenStore tokenStore;
    private final SCIMUserProvisioning userProvisioning;
    private final ClientRepository clientRepository;

    @Autowired
    public TokenService(TokenStore tokenStore,
                        SCIMUserProvisioning userProvisioning,
                        ClientRepository clientRepository) {
        this.tokenStore = tokenStore;
        this.userProvisioning = userProvisioning;
        this.clientRepository = clientRepository;
    }

    public AccessToken validateToken(final String token) {
        OAuth2Authentication auth = tokenStore.readAuthentication(token);
        OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth);
        OAuth2Request authReq = auth.getOAuth2Request();

        AccessToken.Builder tokenBuilder = new AccessToken.Builder(token).setClientId(authReq.getClientId());

        if (auth.getUserAuthentication() != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            tokenBuilder.setUserName(user.getUserName());
            tokenBuilder.setUserId(user.getId());
        }

        tokenBuilder.setExpiresAt(accessToken.getExpiration());
        for (String scopeString : authReq.getScope()) {
            tokenBuilder.addScope(new Scope(scopeString));
        }

        return tokenBuilder.build();
    }

    public void revokeToken(final String token) {
        tokenStore.removeAccessToken(new DefaultOAuth2AccessToken(token));
    }

    public void revokeAllTokensOfUser(final String userId) {
        User user = userProvisioning.getById(userId);
        // the token store maps the tokens of a user to the string representation of the principal
        String searchKey = new User.Builder(user.getUserName())
                .setId(userId)
                .build()
                .toString();
        List<String> clientIds = clientRepository.findAllClientIds();
        List<OAuth2AccessToken> tokens = new LinkedList<>();
        for (String clientId : clientIds) {
            Collection<OAuth2AccessToken> tokenForClient = tokenStore.findTokensByClientIdAndUserName(
                    clientId, searchKey
            );
            tokens.addAll(tokenForClient);
        }
        for (OAuth2AccessToken token : tokens) {
            tokenStore.removeAccessToken(token);
        }
    }
}
