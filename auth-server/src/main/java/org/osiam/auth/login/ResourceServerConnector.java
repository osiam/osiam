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
                osiamAccessTokenProvider.getAccessToken());
        if (result.getTotalResults() != 1) {
            return null;
        } else {
            return result.getResources().get(0);
        }
    }

    public User createUser(User user) {
        OsiamConnector osiamConnector = createOsiamConnector();
        return osiamConnector.createUser(user, osiamAccessTokenProvider.getAccessToken());
    }

    public User updateUser(String userId, UpdateUser user) {
        OsiamConnector osiamConnector = createOsiamConnector();
        return osiamConnector.updateUser(userId, user, osiamAccessTokenProvider.getAccessToken());
    }

    public User searchUserByUserNameAndPassword(String userName, String hashedPassword) {
        OsiamConnector osiamConnector = createOsiamConnector();
        String queryString = new StringQueryBuilder().setFilter("userName eq \"" + userName + "\""
                + " and password eq \"" + hashedPassword + "\"").build();
        SCIMSearchResult<User> result = osiamConnector.searchUsers(queryString,
                osiamAccessTokenProvider.getAccessToken());
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
