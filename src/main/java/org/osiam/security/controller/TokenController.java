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
package org.osiam.security.controller;

import org.osiam.auth.token.TokenService;
import org.osiam.client.oauth.AccessToken;
import org.osiam.security.authentication.AuthenticationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * This Controller is used to handle OAuth2 access tokens with Spring Security.
 */
@Controller
@RequestMapping(value = "/token")
public class TokenController {

    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @RequestMapping(value = "/validation", method = RequestMethod.POST)
    @ResponseBody
    public AccessToken validateToken(@RequestHeader("Authorization") final String authorization) {
        return tokenService.validateToken(getToken(authorization));
    }

    @RequestMapping(value = "/revocation", method = RequestMethod.POST)
    @ResponseBody
    public void revokeToken(@RequestHeader("Authorization") final String authorization) {
        tokenService.revokeToken(getToken(authorization));
    }

    @RequestMapping(value = "/revocation/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public void revokeAllTokensOfUser(@PathVariable("userId") final String userId) {
        tokenService.revokeAllTokensOfUser(userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AuthenticationError handleException(InvalidTokenException ex,
                                               HttpServletRequest request) {
        return new AuthenticationError("invalid_token", ex.getMessage());
    }

    private String getToken(String authorization) {
        int lastIndexOf = authorization.lastIndexOf(' ');
        return authorization.substring(lastIndexOf + 1);
    }
}
