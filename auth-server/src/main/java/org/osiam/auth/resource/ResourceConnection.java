package org.osiam.auth.resource;

import javax.inject.Inject;

import org.osiam.auth.configuration.AuthServerStartupConfiguration;
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
public class ResourceConnection {
    
    @Value("${org.osiam.resource-server.home}")
    private String resourceServerHome;

    @Value("${org.osiam.auth-server.home}")
    private String authServerHome;
    
    @Inject
    private AuthServerStartupConfiguration authServerStartupConfiguration;

    public User getUserByUsername(final String userName) {
        OsiamConnector osiamConnector = createOsiamConnector();
        String queryString = new StringQueryBuilder().setFilter("userName eq \"" + userName + "\"").build();
        SCIMSearchResult<User> result = osiamConnector.searchUsers(queryString, authServerStartupConfiguration.getAccessToken());
        if (result.getTotalResults() != 1) {
            return null;
        } else {
            return result.getResources().get(0);
        }
    }

    public User createUser(User user) {
        OsiamConnector osiamConnector = createOsiamConnector();
        return osiamConnector.createUser(user, authServerStartupConfiguration.getAccessToken());
    }

    public User updateUser(String userId, UpdateUser user) {
        OsiamConnector osiamConnector = createOsiamConnector();
        return osiamConnector.updateUser(userId, user, authServerStartupConfiguration.getAccessToken());
    }

    private OsiamConnector createOsiamConnector() {
        OsiamConnector.Builder oConBuilder = new OsiamConnector.Builder().
                setAuthServerEndpoint(authServerHome).
                setResourceServerEndpoint(resourceServerHome).
                setGrantType(GrantType.CLIENT_CREDENTIALS).
                setClientId(AuthServerStartupConfiguration.AUTH_SERVER_CLIENT_ID).
                setClientSecret(authServerStartupConfiguration.getClientSecret()).
                setScope(Scope.ALL);
        return oConBuilder.build();
    }
}
