package org.osiam.auth.token;

import org.osiam.auth.oauth_client.ClientRepository;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.provisioning.SCIMUserProvisioning;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class TokenService {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private SCIMUserProvisioning userProvisioning;

    @Autowired
    private ClientRepository clientRepository;

    public AccessToken validateToken(final String token) {
        OAuth2Authentication auth = tokenStore.readAuthentication(token);
        OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth);
        OAuth2Request authReq = auth.getOAuth2Request();

        AccessToken.Builder tokenBuilder = new AccessToken.Builder(token).setClientId(authReq.getClientId());

        if (auth.getUserAuthentication() != null && auth.getPrincipal() instanceof User) {
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

    public void revokeToken(final String token) {
        tokenStore.removeAccessToken(new DefaultOAuth2AccessToken(token));
    }

    public void revokeAllTokensOfUser(final String userId) {
        User user = userProvisioning.getById(userId);
        // the token store maps the tokens of a user to the string representation of the principal
        String searchKey = new User.Builder(user.getUserName())
                .setId(userId)
                .build()
                .toString();
        List<String> clientIds = clientRepository.findAllClientIds();
        List<OAuth2AccessToken> tokens = new LinkedList<>();
        for (String clientId : clientIds) {
            Collection<OAuth2AccessToken> tokenForClient = tokenStore.findTokensByClientIdAndUserName(
                    clientId, searchKey
            );
            tokens.addAll(tokenForClient);
        }
        for (OAuth2AccessToken token : tokens) {
            tokenStore.removeAccessToken(token);
        }
    }
}
