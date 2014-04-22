package org.osiam.auth.login.ldap

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.Authentication
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;

import spock.lang.Specification

class OsiamLdapAuthenticationProviderSpec extends Specification {

    def 'the ldap provider only supports OsiamLdapAuthentication class'() {
        given:
        LdapContextSource contextSource = new LdapContextSource()
        OsiamLdapAuthoritiesPopulator rolePopulator = new OsiamLdapAuthoritiesPopulator(contextSource, "");
        OsiamLdapUserContextMapper mapper = new OsiamLdapUserContextMapper([:])
        LdapAuthenticator authenticator = new BindAuthenticator(contextSource)
        OsiamLdapAuthenticationProvider provider = new OsiamLdapAuthenticationProvider(authenticator, rolePopulator, mapper, [:])
        
        expect:
        provider.supports(OsiamLdapAuthentication)
    }
}
