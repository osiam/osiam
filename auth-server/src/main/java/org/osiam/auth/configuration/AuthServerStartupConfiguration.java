package org.osiam.auth.configuration;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.osiam.auth.oauth_client.ClientDao;
import org.osiam.auth.oauth_client.ClientEntity;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AuthServerStartupConfiguration {

    public static final String AUTH_SERVER_CLIENT_ID = "auth-server";
    
    private AccessToken accessToken;
    
    @Inject
    private ClientDao clientDao;
    
    @Value("${org.osiam.auth-server.home}")
    private String authServerHome;
    
    @Inject
    private ProviderManager clientAuthenticationManager;
    
    @Inject
    private DefaultTokenServices tokenServices;
    
    @Inject
    private PlatformTransactionManager txManager;
    
    private String clientSecret;
    
    @PostConstruct
    public void createAccessToken() {
        
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                clientSecret = createAuthServerClient().getClientSecret();
                
                Set<String> scopes = new HashSet<String>();
                scopes.add(Scope.GET.toString());
                scopes.add(Scope.POST.toString());
                scopes.add(Scope.PUT.toString());
                scopes.add(Scope.PATCH.toString());
                scopes.add(Scope.DELETE.toString());

                DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(AUTH_SERVER_CLIENT_ID, scopes);
                authorizationRequest.setApproved(true);
                
                OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(authorizationRequest, null);

                accessToken = AccessToken.of(tokenServices.createAccessToken(oAuth2Authentication).getValue());
            }
        });
    }
    
    private ClientEntity createAuthServerClient() {
        final ClientEntity clientEntity = new ClientEntity();

        Set<String> scopes = new HashSet<String>();
        scopes.add(Scope.GET.toString());
        scopes.add(Scope.POST.toString());
        scopes.add(Scope.PUT.toString());
        scopes.add(Scope.PATCH.toString());
        scopes.add(Scope.DELETE.toString());
        
        Set<String> grants = new HashSet<String>();
        grants.add(GrantType.CLIENT_CREDENTIALS.toString());
        
        clientEntity.setId(AUTH_SERVER_CLIENT_ID);
        clientEntity.setRefreshTokenValiditySeconds(3600);
        clientEntity.setAccessTokenValiditySeconds(3600);
        clientEntity.setRedirectUri(authServerHome);
        clientEntity.setScope(scopes);
        clientEntity.setExpiry(new Date(3000, 1, 1));
        clientEntity.setImplicit(true);
        clientEntity.setValidityInSeconds(3600);
        clientEntity.setGrants(grants);

        return clientDao.create(clientEntity);
    }
    
    public AccessToken getAccessToken() {
        return accessToken;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
