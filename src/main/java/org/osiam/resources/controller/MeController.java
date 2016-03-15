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
package org.osiam.resources.controller;

import com.google.common.base.Strings;
import org.osiam.resources.exception.InvalidTokenException;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This Controller is used for getting information about the user who initialised the access_token.
 */
@RestController
@RequestMapping(value = "/Me")
@Transactional
public class MeController extends ResourceController<User> {

    private final ResourceServerTokenServices resourceServerTokenServices;

    @Autowired
    public MeController(ResourceServerTokenServices resourceServerTokenServices) {
        this.resourceServerTokenServices = resourceServerTokenServices;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String tokenHeader,
                                               UriComponentsBuilder builder) {

        if (Strings.isNullOrEmpty(tokenHeader)) {
            throw new IllegalArgumentException("No access token provided!"); // This should never happen!
        }

        String accessToken = tokenHeader.substring("Bearer ".length());

        OAuth2Authentication oAuth = resourceServerTokenServices.loadAuthentication(accessToken);
        if (oAuth.isClientOnly()) {
            throw new InvalidTokenException("Can't return an user. This access token belongs to a client.");
        }

        Authentication userAuthentication = oAuth.getUserAuthentication();

        Object principal = userAuthentication.getPrincipal();
        User user;
        if (principal instanceof User) {
            user = (User) principal;
        } else {
            throw new IllegalArgumentException("User not authenticated.");
        }
        return buildResponseWithLocation(user, builder, HttpStatus.OK);
    }
}
