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

package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.PhoneNumberConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.PhoneNumber
import org.osiam.storage.entities.PhoneNumberEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class PhoneNumbersUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    PhoneNumberEntity phoneNumberEntity = Mock()
    PhoneNumberConverter phoneNumberConverter = Mock()
    PhoneNumberUpdater phoneNumberUpdater = new PhoneNumberUpdater(phoneNumberConverter : phoneNumberConverter)

    def 'removing all phoneNumbers is possible'() {
        when:
        phoneNumberUpdater.update(null, userEntity, ['phoneNumbers'] as Set)

        then:
        1 * userEntity.removeAllPhoneNumbers()
        userEntity.getPhoneNumbers() >> ([
            new PhoneNumberEntity(value : IRRELEVANT),
            new PhoneNumberEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an phoneNumber is possible'(){
        given:
        MultiValuedAttribute phoneNumber01 = new PhoneNumber.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        PhoneNumberEntity phoneNumberEntity01 = new PhoneNumberEntity(value : IRRELEVANT)

        when:
        phoneNumberUpdater.update([phoneNumber01] as List, userEntity, [] as Set)

        then:
        1 * phoneNumberConverter.fromScim(phoneNumber01) >> phoneNumberEntity01
        1 * userEntity.removePhoneNumber(phoneNumberEntity01)
    }

    def 'adding a new phoneNumber is possible'(){
        given:
        MultiValuedAttribute phoneNumber = new PhoneNumber.Builder(value : IRRELEVANT).build()
        PhoneNumberEntity phoneNumberEntity = new PhoneNumberEntity(value : IRRELEVANT)

        when:
        phoneNumberUpdater.update([phoneNumber] as List, userEntity, [] as Set)

        then:
        1 * phoneNumberConverter.fromScim(phoneNumber) >> phoneNumberEntity
        1 * userEntity.addPhoneNumber(phoneNumberEntity)
    }
}
