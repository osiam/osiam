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

package org.osiam.security.authentication;

import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.helper.HttpClientHelper;
import org.osiam.resources.UserSpring;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("userDetailsService")
/**
 * Mainly used for demonstration, it is used to validate the user login, before he grants or denies the client access
 * to a resource.
 */
public class AuthenticationBean implements UserDetailsService {

    private static final String URL = "http://localhost:8080/osiam-resource-server/authentication/user/";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final HttpClientHelper httpClientHelper = new HttpClientHelper();
        final String result = httpClientHelper.executeHttpGet(URL+username);

        final UserSpring userSpring;
        try {
            userSpring = mapper.readValue(result, UserSpring.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return userSpring;
    }
}
