/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.converter

import org.osiam.resources.scim.Address
import org.osiam.storage.entities.AddressEntity
import spock.lang.Specification

class AddressConverterSpec extends Specification {

    AddressEntity addressEntity
    Address scimAddress
    AddressConverter addressConverter

    def setup() {
        addressEntity = new AddressEntity(
                formatted: 'example',
                streetAddress: 'Fake Street 123',
                locality: 'de',
                region: 'de',
                postalCode: '12345',
                country: 'Germany',
                display: 'myaddress')
        addressEntity.setPrimary(true)

        scimAddress = new Address.Builder()
                .setFormatted('example')
                .setStreetAddress('Fake Street 123')
                .setLocality('de')
                .setRegion('de')
                .setPostalCode('12345')
                .setCountry('Germany')
                .setPrimary(true)
                .setDisplay('myaddress')
                .build()

        addressConverter = new AddressConverter()
    }

    def 'convert entity to SCIM Address'() {
        when:
        Address address = addressConverter.toScim(addressEntity)

        then:
        address.equals(scimAddress)
    }

    def 'convert SCIM Address to entity'() {
        when:
        AddressEntity entity = addressConverter.fromScim(scimAddress)

        then:
        entity == addressEntity
    }

    def "primary in entity is false when null in SCIM"() {
        given:
        Address address = new Address.Builder()
                .setCountry("country")
                .setFormatted("formatted")
                .setLocality("locality")
                .setPostalCode("123456")
                .setRegion("region")
                .setStreetAddress("streetAddress")
                .setDisplay('myaddress')
                .build()
        when:
        def result = addressConverter.fromScim(address)

        then:
        !result.isPrimary()
    }

    def 'passing null SCIM Address returns null'() {
        expect:
        addressConverter.fromScim(null) == null
    }

    def 'passing null entity returns null'() {
        expect:
        addressConverter.toScim(null) == null
    }

    def 'all attributes of the SCIM Address should be null'() {
        given:
        AddressEntity addressEntity = new AddressEntity()

        when:
        Address result = addressConverter.toScim(addressEntity)

        then:
        result.country == null
        result.formatted == null
        result.locality == null
        result.postalCode == null
        result.region == null
        result.streetAddress == null
        result.display == null
    }

    def 'all attributes of the entity should be null'() {
        given:
        Address address = new Address.Builder().build()

        when:
        AddressEntity result = addressConverter.fromScim(address)

        then:
        result.country == null
        result.formatted == null
        result.locality == null
        result.postalCode == null
        result.region == null
        result.streetAddress == null
        result.display == null
    }
}
