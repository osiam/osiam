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

package org.osiam.security.controller;

import org.osiam.resources.RoleSpring;
import org.osiam.security.AuthenticationSpring;
import org.osiam.security.AuthorizationRequestSpring;
import org.osiam.security.OAuth2AuthenticationSpring;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Collection;

/**
 * This Controller is used to handle OAuth2 access tokens with Spring Security.
 */
@Controller
@RequestMapping(value = "/token")
public class TokenController {

    @Inject
    private DefaultTokenServices tokenServices;

    @RequestMapping(value = "/validate/{token}", method = RequestMethod.GET)
    @ResponseBody
    public OAuth2AuthenticationSpring validateToken(@PathVariable final String token) {
        OAuth2Authentication oAuth2Authentication = tokenServices.loadAuthentication(token);

        OAuth2AuthenticationSpring oAuth2AuthenticationSpring = new OAuth2AuthenticationSpring();

        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();

        AuthenticationSpring authenticationSpring = new AuthenticationSpring();
        authenticationSpring.setPrincipal(userAuthentication.getPrincipal());
        authenticationSpring.setName(userAuthentication.getName());
        authenticationSpring.setAuthorities((Collection<? extends RoleSpring>) userAuthentication.getAuthorities());
        authenticationSpring.setCredentials(userAuthentication.getCredentials());
        authenticationSpring.setDetails(userAuthentication.getDetails());
        authenticationSpring.setAuthenticated(userAuthentication.isAuthenticated());

        AuthorizationRequestSpring authorizationRequestSpring = new AuthorizationRequestSpring();

        AuthorizationRequest authorizationRequest = oAuth2Authentication.getAuthorizationRequest();

        authorizationRequestSpring.setApprovalParameters(authorizationRequest.getApprovalParameters());
        authorizationRequestSpring.setApproved(authorizationRequest.isApproved());
        authorizationRequestSpring.setAuthorities(authorizationRequest.getAuthorities());
        authorizationRequestSpring.setAuthorizationParameters(authorizationRequest.getAuthorizationParameters());
        authorizationRequestSpring.setClientId(authorizationRequest.getClientId());
        authorizationRequestSpring.setDenied(authorizationRequest.isDenied());
        authorizationRequestSpring.setRedirectUri(authorizationRequest.getRedirectUri());
        authorizationRequestSpring.setResourceIds(authorizationRequest.getResourceIds());
        authorizationRequestSpring.setResponseTypes(authorizationRequest.getResponseTypes());
        authorizationRequestSpring.setScope(authorizationRequest.getScope());
        authorizationRequestSpring.setState(authorizationRequest.getState());

        oAuth2AuthenticationSpring.setAuthenticationSpring(authenticationSpring);
        oAuth2AuthenticationSpring.setAuthorizationRequestSpring(authorizationRequestSpring);

        return oAuth2AuthenticationSpring;
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    @ResponseBody
    public OAuth2AccessToken getToken(@PathVariable final String token) {
        return tokenServices.readAccessToken(token);
    }
}
