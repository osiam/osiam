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

import java.io.IOException;

import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.resources.ClientSpring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is used by clientAuthenticationManager in Spring to authenticate a client when trading an auth_code to an
 * access_token.
 */
@Service("clientDetailsService")
public class OsiamClientDetailsService implements ClientDetailsService {

    @Value("${osiam.server.port}")
    private int serverPort;
    @Value("${osiam.server.host}")
    private String serverHost;
    @Value("${osiam.server.http.scheme}")
    private String httpScheme;

    private ObjectMapper mapper; // NOSONAR : need to mock the dependency therefor the final identifier was removed
    private HttpClientHelper httpClientHelper; // NOSONAR : need to mock the dependency therefor the final identifier
                                               // was removed

    public OsiamClientDetailsService() {
        mapper = new ObjectMapper();
        httpClientHelper = new HttpClientHelper();
    }

    @Override
    public ClientDetails loadClientByClientId(final String clientId) {
        final String serverUri = httpScheme + "://" + serverHost + ":" + serverPort
                + "/osiam-resource-server/authentication/client/";

        final HttpClientRequestResult response = httpClientHelper.executeHttpGet(serverUri + clientId);
        ClientSpring clientSpring;
        try {
            clientSpring = mapper.readValue(response.getBody(), ClientSpring.class);
        } catch (IOException e) {
            throw new RuntimeException(e); // NOSONAR : Need only wrapping to a runtime exception
        }

        return clientSpring;
    }

    public void updateClient(ClientSpring client, String clientId) {
        final String serverUri = httpScheme + "://" + serverHost + ":" + serverPort
                + "/osiam-resource-server/authentication/client/";
        httpClientHelper.executeHttpPut(serverUri + clientId, "expiry", client.getExpiry().toString());
    }
}