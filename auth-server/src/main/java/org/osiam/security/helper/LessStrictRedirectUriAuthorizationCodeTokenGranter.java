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

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.HashMap;
import java.util.Map;

/**
 * Token granter for the authorization code grant type
 *
 * <p>This class is a fork of {@link AuthorizationCodeTokenGranter}. The only difference is that we
 * want to check if the pending redirect URI starts with the sent redirect URI. The reason for this
 * is, that some OAuth 2 clients (e.g. Liferay) are using the default redirect URI when getting the
 * authorization code, but send a simpler redirect uri when getting the access token, because it
 * doesn't matter when exchanging the auth code for an access token.
 */
public class LessStrictRedirectUriAuthorizationCodeTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "authorization_code";

    private final AuthorizationCodeServices authorizationCodeServices;

    public LessStrictRedirectUriAuthorizationCodeTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            AuthorizationCodeServices authorizationCodeServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory
    ) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authorizationCodeServices = authorizationCodeServices;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = tokenRequest.getRequestParameters();
        String authorizationCode = parameters.get("code");
        String redirectUri = parameters.get(OAuth2Utils.REDIRECT_URI);

        if (authorizationCode == null) {
            throw new InvalidRequestException("An authorization code must be supplied.");
        }

        OAuth2Authentication storedAuth = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
        if (storedAuth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + authorizationCode);
        }

        OAuth2Request pendingOAuth2Request = storedAuth.getOAuth2Request();
        // https://jira.springsource.org/browse/SECOAUTH-333
        // This might be null, if the authorization was done without the redirect_uri parameter
        String redirectUriApprovalParameter = pendingOAuth2Request.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);

        if (redirectUriApprovalParameter != null && redirectUri == null
                || redirectUriApprovalParameter != null
                && !pendingOAuth2Request.getRedirectUri().startsWith(redirectUri)) {
            throw new RedirectMismatchException("Redirect URI mismatch.");
        }

        String pendingClientId = pendingOAuth2Request.getClientId();
        String clientId = tokenRequest.getClientId();
        if (clientId != null && !clientId.equals(pendingClientId)) {
            // just a sanity check.
            throw new InvalidClientException("Client ID mismatch");
        }

        // Secret is not required in the authorization request, so it won't be available
        // in the pendingAuthorizationRequest. We do want to check that a secret is provided
        // in the token request, but that happens elsewhere.

        Map<String, String> combinedParameters = new HashMap<>(pendingOAuth2Request.getRequestParameters());
        // Combine the parameters adding the new ones last so they override if there are any clashes
        combinedParameters.putAll(parameters);

        // Make a new stored request with the combined parameters
        OAuth2Request finalStoredOAuth2Request = pendingOAuth2Request.createOAuth2Request(combinedParameters);

        Authentication userAuth = storedAuth.getUserAuthentication();

        return new OAuth2Authentication(finalStoredOAuth2Request, userAuth);
    }
}
