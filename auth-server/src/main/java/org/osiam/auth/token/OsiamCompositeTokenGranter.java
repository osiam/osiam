package org.osiam.auth.token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.osiam.resources.scim.User;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

public class OsiamCompositeTokenGranter extends CompositeTokenGranter {

    @Inject
    private DefaultTokenServices tokenServices;
    
    public OsiamCompositeTokenGranter(List<TokenGranter> tokenGranters) {
        super(tokenGranters);
    }

    public OAuth2AccessToken grant(String grantType, AuthorizationRequest authorizationRequest) {
        OAuth2AccessToken grant = super.grant(grantType, authorizationRequest);
        if (grant != null) {
            DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) grant;
            Map<String, Object>  additionalInformation = new HashMap<String, Object>();
            additionalInformation.put("access_token", token.getValue());
            additionalInformation.put("expires_at", token.getExpiration());
            
            StringBuilder scopes = new StringBuilder();
            for (String scopeString : token.getScope()) {
                scopes.append(scopeString).append(" ");
            }
            additionalInformation.put("scopes", scopes);
            
            if(token.getRefreshToken() != null) {
                DefaultExpiringOAuth2RefreshToken refreshToken = (DefaultExpiringOAuth2RefreshToken) token.getRefreshToken();
                additionalInformation.put("refresh_token", refreshToken.getValue());
                additionalInformation.put("refresh_token_expires_at", refreshToken.getExpiration());
            }
            
            additionalInformation.put("token_type", token.getTokenType());
            additionalInformation.put("client_id", authorizationRequest.getClientId());
            
            OAuth2Authentication auth = tokenServices.loadAuthentication(token.getValue());
            
            if(auth.getUserAuthentication() != null && auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();
                additionalInformation.put("user_name", user.getUserName());
                additionalInformation.put("user_id", user.getId());
            }
            
            token.setAdditionalInformation(additionalInformation);
        }
        return grant;
    }
}
