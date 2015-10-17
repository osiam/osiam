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

package org.osiam.security.authorization;

import org.osiam.client.OsiamConnector;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AccessTokenValidationService implements ResourceServerTokenServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenValidationService.class);

    private String authServerHome;

    private OsiamConnector connector;

    static {
        OsiamConnector.setMaxConnections(40);
        OsiamConnector.setMaxConnectionsPerRoute(40);
    }

    @Autowired
    public AccessTokenValidationService(@Value("${org.osiam.auth-server.home}") String authServerHome) {
        this.authServerHome = authServerHome;
        this.connector = new OsiamConnector.Builder()
                .setAuthServerEndpoint(authServerHome)
                .withReadTimeout(10000)
                .withConnectTimeout(5000)
                .build();
    }

    @Override
    public OAuth2Authentication loadAuthentication(String token) {
        AccessToken accessToken = validateAccessToken(token);

        Set<String> scopes = new HashSet<>();
        if (accessToken.getScopes() != null) {
            for (Scope scope : accessToken.getScopes()) {
                scopes.add(scope.toString());
            }
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put(AccessTokenConverter.CLIENT_ID, accessToken.getClientId());
        OAuth2Request authRequest = new OAuth2Request(parameters, accessToken.getClientId(), null, true, scopes, null, null,
                null, null);

        Authentication auth = null;

        if (!accessToken.isClientOnly()) {
            User authUser = new User.Builder(accessToken.getUserName()).setId(accessToken.getUserId()).build();

            auth = new UsernamePasswordAuthenticationToken(authUser, null, new ArrayList<GrantedAuthority>());
        }

        return new OAuth2Authentication(authRequest, auth);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String token) {
        AccessToken accessToken = validateAccessToken(token);

        Set<String> scopes = new HashSet<>();
        for (Scope scope : accessToken.getScopes()) {
            scopes.add(scope.toString());
        }

        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(token);
        oAuth2AccessToken.setScope(scopes);
        oAuth2AccessToken.setExpiration(accessToken.getExpiresAt());
        oAuth2AccessToken.setTokenType(DefaultOAuth2AccessToken.BEARER_TYPE);

        return oAuth2AccessToken;
    }

    /**
     * Revokes the access tokens of the user with the given ID.
     *
     * @param id    the user ID
     * @param token the token to access the service
     */
    public void revokeAccessTokens(String id, String token) {
        AccessToken accessToken = new AccessToken.Builder(token).build();
        try {
            connector.revokeAllAccessTokens(id, accessToken);
        } catch (ConnectionInitializationException e) {
            LOGGER.warn(String.format("Unable to revoke access token on auth-server %s: %s",
                    authServerHome, e.getMessage()));
            throw e;
        } catch (UnauthorizedException e) {
            LOGGER.warn(String.format("Error revoking all access tokens on auth-server: %s", e.getMessage()));
            throw new InvalidTokenException("The provided access token is not valid", e);
        }
    }

    private AccessToken validateAccessToken(String token) {
        AccessToken accessToken;
        try {
            accessToken = connector.validateAccessToken(new AccessToken.Builder(token).build());
        } catch (ConnectionInitializationException e) {
            LOGGER.warn(String.format("Unable to retrieve access token from auth-server %s: %s",
                    authServerHome, e.getMessage()));
            throw e;
        } catch (UnauthorizedException e) {
            LOGGER.warn(String.format("Unable to retrieve access token from auth-server: %s", e.getMessage()));
            throw new InvalidTokenException("The provided access token is not valid", e);
        }
        return accessToken;
    }
}
