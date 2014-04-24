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

import javax.servlet.http.HttpSession

import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.GrantedAuthority

import spock.lang.Specification

class OsiamLdapAuthoritiesPopulatorSpec extends Specification {

    def 'The populator add the user role "ROLE_USER"'() {
        given:
        def role = 'ROLE_USER';
        OsiamLdapAuthoritiesPopulator populator = new OsiamLdapAuthoritiesPopulator(new LdapContextSource(), '')
        DirContextOperations userData = new DirContextAdapter()
        
        when:
        Set<GrantedAuthority> authorities = populator.getAdditionalRoles(userData, '')
        
        then:
        !authorities.empty
        authorities.first().authority == role
    }
}
