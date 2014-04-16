package org.osiam.auth.login.ldap

import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.Authentication
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;

import spock.lang.Specification

class OsiamLdapProviderSpec extends Specification {

    def 'the internal provider only supports OsiamLdapAuthentication class'() {
        given:
        LdapContextSource contextSource = new LdapContextSource()
        OsiamLdapAuthoritiesPopulator rolePopulator = new OsiamLdapAuthoritiesPopulator(contextSource, "");
        LdapAuthenticator authenticator = new BindAuthenticator(contextSource)
        OsiamLdapAuthenticationProvider provider = new OsiamLdapAuthenticationProvider(authenticator, rolePopulator)
        
        expect:
        provider.supports(OsiamLdapAuthentication)
    }
}
