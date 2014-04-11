package org.osiam.auth.login.oauth;

import java.util.Arrays;
import java.util.Map;

import org.osiam.auth.login.internal.InternalAuthentication;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class OsiamGranter extends ResourceOwnerPasswordTokenGranter {

    private final AuthenticationManager authenticationManager;
    
    public OsiamGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService) {
        super(authenticationManager, tokenServices, clientDetailsService);
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    protected OAuth2Authentication getOAuth2Authentication(AuthorizationRequest clientToken) {

        Map<String, String> parameters = clientToken.getAuthorizationParameters();
        String username = parameters.get("username");
        String password = parameters.get("password");

        Authentication userAuth = new InternalAuthentication(username, password);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        }
        catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }
        catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/bad grant
            throw new InvalidGrantException(e.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        DefaultAuthorizationRequest request = new DefaultAuthorizationRequest(clientToken);
        request.remove(Arrays.asList("password"));

        return new OAuth2Authentication(request, userAuth);
    }

}
