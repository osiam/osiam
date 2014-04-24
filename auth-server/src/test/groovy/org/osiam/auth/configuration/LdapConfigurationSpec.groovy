/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
