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

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.security.OAuth2AuthenticationSpring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AccessTokenValidationService implements ResourceServerTokenServices {

    private ObjectMapper mapper;
    private HttpClientHelper httpClient;

    @Value("${osiam.server.port}")
    private int serverPort;
    @Value("${osiam.server.host}")
    private String serverHost;
    @Value("${osiam.server.http.scheme}")
    private String httpScheme;

    public AccessTokenValidationService() {
        mapper = new ObjectMapper();
        httpClient = new HttpClientHelper();
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) {
        final String serverUri = httpScheme + "://" + serverHost + ":" + serverPort + "/osiam-auth-server";

        HttpClientRequestResult result = httpClient.executeHttpGet(serverUri + "/token/validate/" + accessToken, null, null);

        if (result.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            throw new InvalidTokenException("invalid_token");
        }

        OAuth2AuthenticationSpring oAuth2AuthenticationSpring;
        try {
            oAuth2AuthenticationSpring = mapper.readValue(result.getBody(), OAuth2AuthenticationSpring.class);
        } catch (IOException e) {
            throw new RuntimeException(e); //NOSONAR : Need only wrapping to a runtime exception
        }

        return new OAuth2Authentication(oAuth2AuthenticationSpring.getAuthorizationRequestSpring(), oAuth2AuthenticationSpring.getAuthenticationSpring());
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        final String serverUri = httpScheme + "://" + serverHost + ":" + serverPort + "/osiam-auth-server";

        HttpClientRequestResult result = httpClient.executeHttpGet(serverUri + "/token/" + accessToken, null, null);

        if (result.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            throw new InvalidTokenException("invalid_token");
        }

        OAuth2AccessToken oAuth2AccessToken;
        try {
            oAuth2AccessToken = mapper.readValue(result.getBody(), OAuth2AccessToken.class);
        } catch (IOException e) {
            throw new RuntimeException(e); //NOSONAR : Need only wrapping to a runtime exception
        }
        return oAuth2AccessToken;
    }
}