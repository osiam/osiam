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
                country: 'Germany')
        addressEntity.setPrimary(true)


        scimAddress = new Address.Builder()
                .setFormatted('example')
                .setStreetAddress('Fake Street 123')
                .setLocality('de')
                .setRegion('de')
                .setPostalCode('12345')
                .setCountry('Germany')
                .setPrimary(true)
                .build()

        addressConverter = new AddressConverter()
    }

    def 'convert addressEntity to scim address works'() {
        when:
        Address address = addressConverter.toScim(addressEntity)

        then:
        address.equals(scimAddress)
    }

    def 'convert scim address to  addressEntity works'() {
        when:
        AddressEntity entity = addressConverter.fromScim(scimAddress)

        then:
        entity == addressEntity
    }

    def "primary in entity is false when null in scim"() {
        given:
        Address address = new Address.Builder().
                setCountry("country").
                setFormatted("formatted").
                setLocality("locality").
                setPostalCode("123456").
                setRegion("region").
                setStreetAddress("streetAddress").
                build()
        when:
        def result = addressConverter.fromScim(address)

        then:
        !result.isPrimary()
    }

    def 'passing null scim address returns null'() {
        expect:
        addressConverter.fromScim(null) == null
    }

    def 'passing null addressEntity returns null'() {
        expect:
        addressConverter.toScim(null) == null
    }
}
