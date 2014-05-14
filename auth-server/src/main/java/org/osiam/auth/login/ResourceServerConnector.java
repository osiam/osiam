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

package org.osiam.auth.login;

import javax.inject.Inject;

import org.osiam.auth.oauth_client.OsiamAuthServerClientProvider;
import org.osiam.auth.token.OsiamAccessTokenProvider;
import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.StringQueryBuilder;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResourceServerConnector {

    @Value("${org.osiam.resource-server.home}")
    private String resourceServerHome;

    @Value("${org.osiam.auth-server.home}")
    private String authServerHome;

    @Inject
    private OsiamAccessTokenProvider osiamAccessTokenProvider;

    @Inject
    private OsiamAuthServerClientProvider authServerClientProvider;

    public User getUserByUsername(final String userName) {
        OsiamConnector osiamConnector = createOsiamConnector();
        String queryString = new StringQueryBuilder().setFilter("userName eq \"" + userName + "\""
                + " and active eq \"true\"").build();
        SCIMSearchResult<User> result = osiamConnector.searchUsers(queryString,
                osiamAccessTokenProvider.createAccessToken());
        if (result.getTotalResults() != 1) {
            return null;
        } else {
            return result.getResources().get(0);
        }
    }

    public User createUser(User user) {
        OsiamConnector osiamConnector = createOsiamConnector();
        return osiamConnector.createUser(user, osiamAccessTokenProvider.createAccessToken());
    }

    public User updateUser(String userId, UpdateUser user) {
        OsiamConnector osiamConnector = createOsiamConnector();
        return osiamConnector.updateUser(userId, user, osiamAccessTokenProvider.createAccessToken());
    }

    public User searchUserByUserNameAndPassword(String userName, String hashedPassword) {
        OsiamConnector osiamConnector = createOsiamConnector();
        String queryString = new StringQueryBuilder().setFilter("userName eq \"" + userName + "\""
                + " and password eq \"" + hashedPassword + "\"").build();
        SCIMSearchResult<User> result = osiamConnector.searchUsers(queryString,
                osiamAccessTokenProvider.createAccessToken());
        if (result.getTotalResults() != 1) {
            return null;
        } else {
            return result.getResources().get(0);
        }
    }

    private OsiamConnector createOsiamConnector() {
        OsiamConnector.Builder oConBuilder = new OsiamConnector.Builder().
                setAuthServerEndpoint(authServerHome).
                setResourceServerEndpoint(resourceServerHome).
                setGrantType(GrantType.CLIENT_CREDENTIALS).
                setClientId(OsiamAuthServerClientProvider.AUTH_SERVER_CLIENT_ID).
                setClientSecret(authServerClientProvider.getClientSecret()).
                setScope(Scope.ALL);
        return oConBuilder.build();
    }
}
