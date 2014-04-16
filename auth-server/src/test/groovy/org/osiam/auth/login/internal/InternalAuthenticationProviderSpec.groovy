package org.osiam.auth.login.internal

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

import spock.lang.Specification

class InternalAuthenticationProviderSpec extends Specification {

    def 'the internal provider only supports InternalAuthentication class'() {
        given:
        InternalAuthenticationProvider provider = new InternalAuthenticationProvider()
        
        expect:
        provider.supports(InternalAuthentication)
    }
}
