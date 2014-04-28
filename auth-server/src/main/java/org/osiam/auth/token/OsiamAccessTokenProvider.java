package org.osiam.auth.token;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.osiam.auth.oauth_client.OsiamAuthServerClientProvider;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OsiamAccessTokenProvider {

    private AccessToken accessToken;
    
    @Inject
    private DefaultTokenServices tokenServices;
    
    @Inject
    private PlatformTransactionManager txManager;
    
    @PostConstruct
    public void createAccessToken() {
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Set<String> scopes = new HashSet<String>();
                scopes.add(Scope.GET.toString());
                scopes.add(Scope.POST.toString());
                scopes.add(Scope.PUT.toString());
                scopes.add(Scope.PATCH.toString());
                scopes.add(Scope.DELETE.toString());

                DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(OsiamAuthServerClientProvider.AUTH_SERVER_CLIENT_ID, scopes);
                authorizationRequest.setApproved(true);
                
                OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(authorizationRequest, null);

                accessToken = AccessToken.of(tokenServices.createAccessToken(oAuth2Authentication).getValue());
            }
        });
    }
    
    public AccessToken getAccessToken() {
        return accessToken;
    }
}
