package org.osiam.auth.login.oauth

import org.osiam.auth.login.internal.InternalAuthentication
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication

import spock.lang.Specification

class OsiamResourceOwnerPasswordTokenGranterSpec extends Specification {

    def 'the osiam granter uses the InternalAuthentication token for the client authentication'() {
        given:
        ProviderManager authenticationManager = Mock()
        OsiamResourceOwnerPasswordTokenGranter osiamGranter = new OsiamResourceOwnerPasswordTokenGranter(authenticationManager, null, null)
        AuthorizationRequest request = new DefaultAuthorizationRequest(new HashMap())
        InternalAuthentication authentication = new InternalAuthentication('username', 'password', new ArrayList<GrantedAuthority>())
        
        when:
        OAuth2Authentication auth = osiamGranter.getOAuth2Authentication(request)
        
        then:
        authenticationManager.authenticate(_) >> authentication
        auth.getUserAuthentication() instanceof InternalAuthentication
    }
}
