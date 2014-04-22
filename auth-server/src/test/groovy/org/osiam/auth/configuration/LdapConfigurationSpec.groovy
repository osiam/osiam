package org.osiam.auth.configuration

import org.osiam.auth.login.ldap.OsiamLdapAuthenticationProvider
import org.osiam.auth.login.ldap.OsiamLdapAuthoritiesPopulator
import org.osiam.auth.login.ldap.OsiamLdapUserContextMapper
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.ldap.DefaultSpringSecurityContextSource

import spock.lang.Specification

class LdapConfigurationSpec extends Specification {

    LdapConfiguration ldapConfiguration = new LdapConfiguration()
    
    def 'when ldap is not enabled, the contextSource is null'() {
        given:
        ldapConfiguration.isLdapConfigured = false
        
        when:
        DefaultSpringSecurityContextSource contextSource = ldapConfiguration.createLdapContextSource()
        
        then:
        contextSource == null
    }
    
    def 'when ldap is enabled, the contextSource is not null and has the given url'() {
        given:
        ldapConfiguration.isLdapConfigured = true
        ldapConfiguration.url = 'ldaps://localhost:8080/ou=example,ou=users'
        
        when:
        DefaultSpringSecurityContextSource contextSource = ldapConfiguration.createLdapContextSource()
        
        then:
        contextSource != null
        contextSource.urls[0] == 'ldaps://localhost:8080/'
    }
    
    def 'when ldap is not enabled, the ldap provider is null'() {
        given:
        ldapConfiguration.isLdapConfigured = false
        
        when:
        OsiamLdapAuthenticationProvider provider = ldapConfiguration.createLdapAuthProvider()
        
        then:
        provider == null
    }
    
    def 'when ldap is enabled, the ldap provider is not null and all dependencies are build'() {
        given:
        ldapConfiguration.authenticationManager = new ProviderManager()
        ldapConfiguration.authenticationManager.providers = new ArrayList<AuthenticationProvider>()
        ldapConfiguration.isLdapConfigured = true
        ldapConfiguration.url = 'ldaps://localhost:8080/ou=example,ou=users'
        ldapConfiguration.dnPatterns = ['cn={0},ou=people']
        ldapConfiguration.attributes = ['mail']
        ldapConfiguration.attributeMapping = []
        ldapConfiguration.scimLdapAttributes = [:]
        
        when:
        OsiamLdapAuthenticationProvider provider = ldapConfiguration.createLdapAuthProvider()
        
        then:
        provider != null
        provider.authoritiesPopulator instanceof OsiamLdapAuthoritiesPopulator
    }
}
