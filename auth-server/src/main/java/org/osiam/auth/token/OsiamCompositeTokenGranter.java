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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.osiam.resources.scim.User;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

/**
 * Custom TokenGranter, which add additional information to spring's accesstoken,
 * which the resource server needed. Iterate over all configured token granters and
 * choose the one which needed for the current authentication process.
 *
 */
public class OsiamCompositeTokenGranter extends CompositeTokenGranter {

    @Inject
    private DefaultTokenServices tokenServices;

    public OsiamCompositeTokenGranter(List<TokenGranter> tokenGranters) {
        super(tokenGranters);
    }

    public OAuth2AccessToken grant(String grantType, AuthorizationRequest authorizationRequest) {
        OAuth2AccessToken grant = super.grant(grantType, authorizationRequest);
        if (grant != null) {
            DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) grant;
            Map<String, Object>  additionalInformation = new HashMap<String, Object>();
            additionalInformation.put("expires_at", token.getExpiration());

            if(token.getRefreshToken() != null) {
                DefaultExpiringOAuth2RefreshToken refreshToken = (DefaultExpiringOAuth2RefreshToken) token.getRefreshToken();
                additionalInformation.put("refresh_token_expires_at", refreshToken.getExpiration());
            }

            additionalInformation.put("client_id", authorizationRequest.getClientId());

            OAuth2Authentication auth = tokenServices.loadAuthentication(token.getValue());

            if(auth.getUserAuthentication() != null && auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();
                additionalInformation.put("user_name", user.getUserName());
                additionalInformation.put("user_id", user.getId());
            }

            token.setAdditionalInformation(additionalInformation);
        }
        return grant;
    }
}
