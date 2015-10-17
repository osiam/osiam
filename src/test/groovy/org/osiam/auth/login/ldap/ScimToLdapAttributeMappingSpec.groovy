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

import org.osiam.auth.exception.LdapConfigurationException
import spock.lang.Specification

class ScimToLdapAttributeMappingSpec extends Specification {

    def 'constructor creates a default mapping from userName to uid'() {
        given:
        String[] attributeMapping = []

        when:
        ScimToLdapAttributeMapping scimToLdapAttributeMapping = new ScimToLdapAttributeMapping(attributeMapping)

        then:
        scimToLdapAttributeMapping.toLdapAttribute('userName') == 'uid'
    }

    def 'default mapping from userName to uid can be overridden'() {
        given:
        String[] attributeMapping = ['userName:override']

        when:
        ScimToLdapAttributeMapping scimToLdapAttributeMapping = new ScimToLdapAttributeMapping(attributeMapping)

        then:
        scimToLdapAttributeMapping.toLdapAttribute('userName') == 'override'
    }

    def 'constructor creates valid mapping from an array'() {
        given:
        String[]  attributeMapping = ['email:mail', 'nickName:ldapNickname']

        when:
        ScimToLdapAttributeMapping scimToLdapAttributeMapping = new ScimToLdapAttributeMapping(attributeMapping)

        then:
        scimToLdapAttributeMapping.toLdapAttribute('email') == 'mail'
        scimToLdapAttributeMapping.toLdapAttribute('nickName') == 'ldapNickname'
    }

    def 'constructor throws LdapConfigurationException when facing unknown SCIM attributes'() {
        given:
        String[]  attributeMapping = ['mail:mail']

        when:
        new ScimToLdapAttributeMapping(attributeMapping)

        then:
        thrown(LdapConfigurationException)
    }

    def 'returns collection of mapped SCIM attributes'() {
        given:
        String[]  attributeMapping = ['email:mail', 'nickName:ldapNickname']
        ScimToLdapAttributeMapping scimToLdapAttributeMapping = new ScimToLdapAttributeMapping(attributeMapping)

        when:
        def scimAttributes = scimToLdapAttributeMapping.scimAttributes()

        then:
        scimAttributes.contains('email')
        scimAttributes.contains('nickName')
    }

    def 'returns collection of mapped LDAP attributes'() {
        given:
        String[]  attributeMapping = ['email:mail', 'nickName:ldapNickname']
        ScimToLdapAttributeMapping scimToLdapAttributeMapping = new ScimToLdapAttributeMapping(attributeMapping)

        when:
        def scimAttributes = scimToLdapAttributeMapping.ldapAttributes()

        then:
        scimAttributes.contains('mail')
        scimAttributes.contains('ldapNickname')
    }
}
