package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.PhoneNumberConverter
import org.osiam.resources.scim.MultiValuedAttribute
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
        MultiValuedAttribute phoneNumber01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        PhoneNumberEntity phoneNumberEntity01 = new PhoneNumberEntity(value : IRRELEVANT)

        when:
        phoneNumberUpdater.update([phoneNumber01] as List, userEntity, [] as Set)

        then:
        1 * phoneNumberConverter.fromScim(phoneNumber01) >> phoneNumberEntity01
        1 * userEntity.removePhoneNumber(phoneNumberEntity01)
    }

    def 'adding a new phoneNumber is possible'(){
        given:
        MultiValuedAttribute phoneNumber = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        PhoneNumberEntity phoneNumberEntity = new PhoneNumberEntity(value : IRRELEVANT)

        when:
        phoneNumberUpdater.update([phoneNumber] as List, userEntity, [] as Set)

        then:
        1 * phoneNumberConverter.fromScim(phoneNumber) >> phoneNumberEntity
        1 * userEntity.addPhoneNumber(phoneNumberEntity)
    }

}
