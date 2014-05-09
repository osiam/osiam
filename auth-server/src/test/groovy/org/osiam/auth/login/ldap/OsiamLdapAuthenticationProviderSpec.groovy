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

package org.osiam.auth.login.ldap

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.Authentication
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

import spock.lang.Specification

class OsiamLdapAuthenticationProviderSpec extends Specification {

    def 'the ldap provider only supports OsiamLdapAuthentication class'() {
        given:
        LdapContextSource contextSource = new LdapContextSource()
        DefaultLdapAuthoritiesPopulator rolePopulator = new DefaultLdapAuthoritiesPopulator(contextSource, "");
        OsiamLdapUserContextMapper mapper = new OsiamLdapUserContextMapper([:])
        LdapAuthenticator authenticator = new BindAuthenticator(contextSource)
        OsiamLdapAuthenticationProvider provider = new OsiamLdapAuthenticationProvider(authenticator, rolePopulator, mapper)
        
        expect:
        provider.supports(OsiamLdapAuthentication)
    }
}
