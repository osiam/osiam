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

import javax.inject.Inject;

import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.query.StringQueryBuilder;
import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.resources.UserSpring;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Mainly used for demonstration, it is used to validate the user login, before he grants or denies the client access to
 * a resource.
 */
@Service("userDetailsService")
public class OsiamUserDetailsService implements UserDetailsService {

    @Value("${osiam.server.port}")
    private int serverPort;

    @Value("${osiam.server.host}")
    private String serverHost;

    @Value("${osiam.server.http.scheme}")
    private String httpScheme;

    @Value("${org.osiam.auth.client.id}")
    private String clientId;

    @Value("${org.osiam.auth.client.secret}")
    private String clientSecret;

    private final String clientScope = "GET POST PATCH";

    @Inject
    private HttpClientHelper httpClientHelper;

    private ObjectMapper mapper; // NOSONAR : need to mock the dependency therefor the final identifier was removed

    public OsiamUserDetailsService() {
        mapper = new ObjectMapper();
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final String serverUri = httpScheme + "://" + serverHost + ":" + serverPort
                + "/osiam-resource-server/authentication/user";

        final HttpClientRequestResult result = httpClientHelper.executeHttpPost(serverUri, username);

        final UserSpring userSpring;
        try {
            userSpring = mapper.readValue(result.getBody(), UserSpring.class);
        } catch (IOException e) {
            throw new RuntimeException(e); // NOSONAR : Need only wrapping to a runtime exception
        }

        return userSpring;
    }

    public User getUserByUsername(final String userName) {
        OsiamConnector osiamConnector = createOsiamConnector();
        String queryString = new StringQueryBuilder().setFilter("userName eq \"" + userName + "\"").build();
        SCIMSearchResult<User> result = osiamConnector.searchUsers(queryString, osiamConnector.retrieveAccessToken());
        if (result.getTotalResults() != 1) {
            return null;
        } else {
            return result.getResources().get(0);
        }
    }

    public User createUser(User user) {
        OsiamConnector osiamConnector = createOsiamConnector();
        return osiamConnector.createUser(user, osiamConnector.retrieveAccessToken());
    }

    public User updateUser(String userId, UpdateUser user) {
        OsiamConnector osiamConnector = createOsiamConnector();
        return osiamConnector.updateUser(userId, user, osiamConnector.retrieveAccessToken());
    }

    private OsiamConnector createOsiamConnector() {
        OsiamConnector.Builder oConBuilder = new OsiamConnector.Builder().
                setAuthServerEndpoint(buildServerBaseUri("osiam-auth-server")).
                setResourceServerEndpoint(buildServerBaseUri("osiam-resource-server")).
                setGrantType(GrantType.CLIENT_CREDENTIALS).
                setClientId(clientId).
                setClientSecret(clientSecret).
                setScope(clientScope);
        return oConBuilder.build();
    }

    private String buildServerBaseUri(String endpoint) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(httpScheme)
                .append("://")
                .append(serverHost)
                .append(":")
                .append(serverPort)
                .append("/")
                .append(endpoint);

        return stringBuilder.toString();
    }
}