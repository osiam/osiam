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

import org.osiam.resources.scim.Address
import org.osiam.storage.entities.AddressEntity
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.03.13
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
class AddressEntitySpec extends Specification {

    AddressEntity addressEntity = new AddressEntity()
    def userEntity = Mock(UserEntity)

    def "setter and getter for the Id should be present"() {
        when:
        addressEntity.setId(123456)

        then:
        addressEntity.getId() == 123456
    }

    def "setter and getter for the formatted address should be present"() {
        when:
        addressEntity.setFormatted("funky street 1337 Honkytown")

        then:
        addressEntity.getFormatted() == "funky street 1337 Honkytown"
    }

    def "setter and getter for the street address should be present"() {
        when:
        addressEntity.setStreetAddress("funky street")

        then:
        addressEntity.getStreetAddress() == "funky street"
    }

    def "setter and getter for the locality should be present"() {
        when:
        addressEntity.setLocality("locality")

        then:
        addressEntity.getLocality() == "locality"
    }

    def "setter and getter for the region should be present"() {
        when:
        addressEntity.setRegion("region")

        then:
        addressEntity.getRegion() == "region"
    }

    def "setter and getter for the postal code should be present"() {
        when:
        addressEntity.setPostalCode("123456")

        then:
        addressEntity.getPostalCode() == "123456"
    }

    def "setter and getter for the country should be present"() {
        when:
        addressEntity.setCountry("Germany")

        then:
        addressEntity.getCountry() == "Germany"
    }

    def "setter and getter for the type should be present"() {
        when:
        addressEntity.setType("home")

        then:
        addressEntity.getType() == "home"
    }

    def "getter for the type should not throw exception if type is null"() {
        when:
        addressEntity.setType(null)

        then:
        addressEntity.getType() == null
    }

    def "setter and getter for primary should be present"() {
        when:
        addressEntity.setPrimary(true)

        then:
        addressEntity.isPrimary()
    }

    def "setter and getter for the user should be present"() {
        when:
        addressEntity.setUser(userEntity)

        then:
        addressEntity.getUser() == userEntity
    }

    def "mapping to scim should be present"() {
        when:
        def address = addressEntity.toScim()

        then:
        address.country == addressEntity.country
        address.formatted == addressEntity.formatted
        address.locality == addressEntity.locality
        address.postalCode == addressEntity.postalCode.toString()
        address.region == addressEntity.region
        address.streetAddress == addressEntity.streetAddress
    }

    def "mapping from scim should be possible"() {
        given:
        Address address =new Address.Builder().
                setCountry("country").
                setFormatted("formatted").
                setLocality("locality").
                setPostalCode("123456").
                setRegion("region").
                setStreetAddress("streetAddress").setPrimary(true).
                build()
        when:
        def result = AddressEntity.fromScim(address)

        then:
        result != null
    }

    def "should set primary to false when null"() {
        given:
        Address address =new Address.Builder().
                setCountry("country").
                setFormatted("formatted").
                setLocality("locality").
                setPostalCode("123456").
                setRegion("region").
                setStreetAddress("streetAddress").
                build()
        when:
        def result = AddressEntity.fromScim(address)

        then:
        !result.isPrimary()
    }
}