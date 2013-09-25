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

package org.osiam.resources.provisioning

import org.osiam.storage.entities.AddressEntity
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.EntitlementsEntity
import org.osiam.storage.entities.ImEntity
import org.osiam.storage.entities.PhoneNumberEntity
import org.osiam.storage.entities.PhotoEntity
import org.osiam.storage.entities.RolesEntity
import org.osiam.storage.entities.X509CertificateEntity
import org.osiam.resources.provisioning.UserSCIMEntities
import spock.lang.Specification

class UserSCIMEntitiesTest extends Specification {
    def underTest = UserSCIMEntities.ENTITIES
    def "should contain emails"() {
        when:
           def result = underTest.fromString("emails")
        then:
        result.getClazz() == EmailEntity
        !result.isNotMultiValue()
    }
    def "should contain ims"() {
        when:
        def result = underTest.fromString("ims")
        then:
        result.getClazz() == ImEntity
        !result.isNotMultiValue()

    }
    def "should contain phonenumbers"() {
        when:
        def result = underTest.fromString("phonenumbers")
        then:
        result.getClazz() == PhoneNumberEntity
        !result.isNotMultiValue()

    }
    def "should contain photos"() {
        when:
        def result = underTest.fromString("photos")
        then:
        result.getClazz() == PhotoEntity
        !result.isNotMultiValue()

    }
    def "should contain entitlements"() {
        when:
        def result = underTest.fromString("entitlements")
        then:
        result.getClazz() == EntitlementsEntity
        !result.isNotMultiValue()

    }
    def "should contain roles"() {
        when:
        def result = underTest.fromString("roles")
        then:
        result.getClazz() == RolesEntity
        !result.isNotMultiValue()

    }
    def "should contain x509certificates"() {
        when:
        def result = underTest.fromString("x509certificates")
        then:
        result.getClazz() == X509CertificateEntity
        !result.isNotMultiValue()

    }
    def "should contain addresses"() {
        when:
        def result = underTest.fromString("addresses")
        then:
        result.getClazz() == AddressEntity
        result.isNotMultiValue()

    }

}
