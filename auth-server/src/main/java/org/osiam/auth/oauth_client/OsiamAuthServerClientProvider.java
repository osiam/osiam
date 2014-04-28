package org.osiam.auth.oauth_client;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.osiam.auth.exception.ResourceNotFoundException;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OsiamAuthServerClientProvider {

    private static final Logger LOGGER = Logger.getLogger(OsiamAuthServerClientProvider.class.getName());
    
    public static final String AUTH_SERVER_CLIENT_ID = "auth-server";
    
    @Inject
    private ClientDao clientDao;
    
    @Value("${org.osiam.auth-server.home}")
    private String authServerHome;
    
    private String clientSecret;
    
    @PostConstruct
    private void createAuthServerClient() {
        
        ClientEntity clientEntity = null;
        
        try {
            clientEntity = clientDao.getClient(AUTH_SERVER_CLIENT_ID);
        } catch (ResourceNotFoundException e) {
            LOGGER.log(Level.INFO, "No auth server found. The auth server will be created.");
        }
        
        if(clientEntity == null) {
            clientEntity = new ClientEntity();
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
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 3000);
            clientEntity.setExpiry(calendar.getTime());
            clientEntity.setImplicit(true);
            clientEntity.setValidityInSeconds(3600);
            clientEntity.setGrants(grants);
    
            clientEntity = clientDao.create(clientEntity);
        }
        
        clientSecret = clientEntity.getClientSecret();
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
