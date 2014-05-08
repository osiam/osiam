package org.osiam.auth.token;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.osiam.auth.oauth_client.OsiamAuthServerClientProvider;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;

@Service
@DependsOn("osiamAuthServerClientProvider")
public class OsiamAccessTokenProvider {

    private AccessToken accessToken;
    
    @Inject
    private DefaultTokenServices tokenServices;
    
    @PostConstruct
    public void createAccessToken() {
        Set<String> scopes = new HashSet<String>();
        scopes.add(Scope.GET.toString());
        scopes.add(Scope.POST.toString());
        scopes.add(Scope.PUT.toString());
        scopes.add(Scope.PATCH.toString());
        scopes.add(Scope.DELETE.toString());

        DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(OsiamAuthServerClientProvider.AUTH_SERVER_CLIENT_ID, scopes);
        authorizationRequest.setApproved(true);
        
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(authorizationRequest, null);

        accessToken = new AccessToken.Builder(tokenServices.createAccessToken(oAuth2Authentication).getValue()).build();
    }
    
    public AccessToken getAccessToken() {
        return accessToken;
    }
}
