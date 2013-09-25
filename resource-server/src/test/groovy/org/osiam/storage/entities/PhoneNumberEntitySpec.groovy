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

package org.osiam.storage.entities

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.PhoneNumberEntity
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.03.13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
class PhoneNumberEntitySpec extends Specification {

    PhoneNumberEntity phoneNumberEntity = new PhoneNumberEntity()
    def userEntity = Mock(UserEntity)

    def "setter and getter for the value should be present"() {
        when:
        phoneNumberEntity.setValue("555-555-555")

        then:
        phoneNumberEntity.getValue() == "555-555-555"
    }

    def "setter and getter for the type should be present"() {
        when:
        phoneNumberEntity.setType("work")

        then:
        phoneNumberEntity.getType() == "work"
    }

    def "setter and getter for the user should be present"() {
        when:
        phoneNumberEntity.setUser(userEntity)

        then:
        phoneNumberEntity.getUser() == userEntity
    }

    def "mapping to scim should be present"() {
        when:
        def multivalue = phoneNumberEntity.toScim()

        then:
        multivalue.value == phoneNumberEntity.value
        multivalue.type == phoneNumberEntity.type
    }

    def "mapping from scim should be present"() {
        given:
        MultiValuedAttribute phoneNumber = new MultiValuedAttribute.Builder().
                setValue("blaaaa").
                setType("home").
                build()

        when:
        def result = PhoneNumberEntity.fromScim(phoneNumber)

        then:
        result != null
    }

    def "should throw an exception if the type is unknown"() {
        when:
        phoneNumberEntity.setType("huch")

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "No enum constant org.osiam.storage.entities.PhoneNumberEntity.CanonicalPhoneNumberTypes.huch"
    }

    def "setter and getter for id should be present"() {
        when:
        phoneNumberEntity.setMultiValueId(1234)

        then:
        phoneNumberEntity.getMultiValueId() == 1234
    }
}