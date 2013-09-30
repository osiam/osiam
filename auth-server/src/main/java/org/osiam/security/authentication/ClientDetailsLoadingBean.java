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
import org.osiam.resources.ClientSpring;
import org.osiam.security.helper.HttpClientHelper;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * This class is used by clientAuthenticationManager in Spring to authenticate a client when trading an auth_code to an
 * access_token.
 */
@Component("clientDetails")
public class ClientDetailsLoadingBean implements ClientDetailsService {

    private static final String URL = "http://localhost:8080/osiam-resource-server/authentication/client/";

    private final ObjectMapper mapper = new ObjectMapper();

    private ClientSpring clientSpring;

    private final HttpClientHelper httpClientHelper;

    public ClientDetailsLoadingBean() {
        httpClientHelper = new HttpClientHelper();
    }

    @Override
    public ClientDetails loadClientByClientId(final String clientId) {
        final String response = httpClientHelper.executeHttpGet(URL+clientId);

        try {
            clientSpring = mapper.readValue(response, ClientSpring.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return clientSpring;
    }

    public ClientSpring getClientSpring() {
        return clientSpring;
    }

    public void updateClient(ClientSpring client, String clientId) {
        httpClientHelper.executeHttpPut(URL+clientId, "expiry", client.getExpiry().toString());
    }
}