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

import org.osiam.resources.converter.AddressConverter
import org.osiam.resources.scim.Address
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.AddressEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification

class AddressUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    AddressEntity addressEntity = Mock()
    AddressConverter addressConverter = Mock()
    AddressUpdater addressUpdater = new AddressUpdater(addressConverter : addressConverter)

    def 'removing all addresses is possible'() {
        when:
        addressUpdater.update(null, userEntity, ['addresses'] as Set)

        then:
        1 * userEntity.removeAllAddresses()
        userEntity.getAddresses() >> ([
            new AddressEntity(formatted : IRRELEVANT),
            new AddressEntity(formatted : IRRELEVANT_02)] as Set)
    }

    def 'removing an address is possible'(){
        given:
        Address address01 = new Address.Builder(formatted : IRRELEVANT, operation : 'delete', ).build()
        AddressEntity addressEntity01 = new AddressEntity(formatted : IRRELEVANT)

        when:
        addressUpdater.update([address01] as List, userEntity, [] as Set)

        then:
        1 * addressConverter.fromScim(address01) >> addressEntity01
        1 * userEntity.removeAddress(addressEntity01)
    }

    def 'adding a new address is possible'(){
        given:
        Address address = new Address.Builder(formatted : IRRELEVANT).build()
        AddressEntity addressEntity = new AddressEntity(formatted : IRRELEVANT)

        when:
        addressUpdater.update([address] as List, userEntity, [] as Set)

        then:
        1 * addressConverter.fromScim(address) >> addressEntity
        1 * userEntity.addAddress(addressEntity)
    }
    
    def 'adding a new primary address removes the primary attribite from the old one'(){
        given:
        Address newPrimaryAddress = new Address.Builder(formatted : IRRELEVANT, primary : true).build()
        AddressEntity newPrimaryAddressEntity = new AddressEntity(formatted : IRRELEVANT, primary : true)

        AddressEntity oldPrimaryAddressEntity = Spy()
        oldPrimaryAddressEntity.setFormatted(IRRELEVANT_02)
        oldPrimaryAddressEntity.setPrimary(true)

        AddressEntity addressEntity = new AddressEntity(formatted : IRRELEVANT_02, primary : false)

        when:
        addressUpdater.update([newPrimaryAddress] as List, userEntity, [] as Set)

        then:
        1 * addressConverter.fromScim(newPrimaryAddress) >> newPrimaryAddressEntity
        1 * userEntity.getAddresses() >> ([oldPrimaryAddressEntity] as Set)
        1 * userEntity.addAddress(newPrimaryAddressEntity)
        1 * oldPrimaryAddressEntity.setPrimary(false)
    }
}
