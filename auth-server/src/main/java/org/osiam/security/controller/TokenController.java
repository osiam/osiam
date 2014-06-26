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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;
import org.osiam.security.authentication.AuthenticationError;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This Controller is used to handle OAuth2 access tokens with Spring Security.
 */
@Controller
@RequestMapping(value = "/token")
public class TokenController {

    @Inject
    private DefaultTokenServices tokenServices;

    @RequestMapping(value = "/validation", method = RequestMethod.POST)
    @ResponseBody
    public AccessToken tokenValidation(@RequestHeader final String authorization) {
        String token = getToken(authorization);
        OAuth2Authentication auth = tokenServices.loadAuthentication(token);
        OAuth2AccessToken accessToken = tokenServices.getAccessToken(auth);
        
        AuthorizationRequest authReq = auth.getAuthorizationRequest();
        AccessToken.Builder tokenBuilder = new AccessToken.Builder(token).setClientId(authReq.getClientId());
        
        if(auth.getUserAuthentication() != null && auth.getPrincipal() instanceof User) {
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
    
    @RequestMapping(value = "/revocation", method = RequestMethod.POST)
    @ResponseBody
    public void tokenRevokation(@RequestHeader final String authorization) {
        String token = getToken(authorization);
        tokenServices.revokeToken(token);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AuthenticationError handleClientAuthenticationException(InvalidTokenException ex, HttpServletRequest request) {
        return new AuthenticationError("invalid_token", ex.getMessage());
    }
    
    private String getToken(String authorization) {
    	int lastIndexOf = authorization.lastIndexOf(' ');
    	return authorization.substring(lastIndexOf + 1);
    }
}
