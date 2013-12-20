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
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.Name
import spock.lang.Specification

class UserEntitySpec extends Specification {

    private static final VALUE = 'irrelevant'

    UserEntity userEntity = new UserEntity()

    def name = new NameEntity()
    def scimName

    def addresses = new ArrayList()
    def emails = new ArrayList()
    def entitlements = new ArrayList()
    def groups = new ArrayList()
    def ims = new ArrayList()
    def phoneNumbers = new ArrayList()
    def photos = new ArrayList()
    def roles = new ArrayList()
    def certificates = new ArrayList()

    def setup() {
        name.setFamilyName("familyName")
        name.setFormatted("formattedName")
        name.setGivenName("givenName")
        name.setHonorificPrefix("prefix")
        name.setHonorificSuffix("suffix")
        name.setId(1234)
        name.setMiddleName("middleName")


        scimName = new Name.Builder().
                setFamilyName("familyName").
                setFormatted("formattedName").
                setGivenName("givenName").
                setHonorificPrefix("prefix").
                setHonorificSuffix("suffix").
                setMiddleName("middleName").
                build()

        addresses.add(new Address.Builder().
                setCountry("country").
                setFormatted("formatted").
                setLocality("locality").
                setPostalCode("123456").
                setRegion("region").
                setStreetAddress("streetAddress").setPrimary(true).
                build())

        emails.add(new MultiValuedAttribute.Builder().
                setPrimary(true).
                setType("home").
                setValue(VALUE).
                build())

        entitlements.add(new MultiValuedAttribute.Builder().
                setValue(VALUE).
                build())

        groups.add(new MultiValuedAttribute.Builder().
                setValue(UUID.randomUUID().toString()).
                setDisplay("display").
                build())

        ims.add(new MultiValuedAttribute.Builder().
                setValue(VALUE).
                setType("icq").
                build())

        phoneNumbers.add(new MultiValuedAttribute.Builder().
                setValue(VALUE).
                setType("home").
                build())

        photos.add(new MultiValuedAttribute.Builder().
                setValue("picture.gif").
                setType("photo").
                build())

        roles.add(new MultiValuedAttribute.Builder().
                setValue("").
                build())

        certificates.add(new MultiValuedAttribute.Builder().
                setValue(VALUE).
                build())
    }

    def "should be able to insert meta"() {
        given:
        def meta = new MetaEntity()
        when:
        userEntity.setMeta(meta)
        then:
        userEntity.getMeta() == meta
    }

    def "User entity should set resourceType to User"() {
        when:
        def result = new UserEntity(userName: "blubb", id: UUID.randomUUID())

        then:
        result.meta.resourceType == "User"
    }

    def 'touch should update lastModified field of the meta object'() {
        given:
        userEntity.meta.lastModified = new Date(0)

        when:
        userEntity.touch()

        then:
        userEntity.meta.lastModified.time > 0
    }

    def "If extensions are null empty set should be returned"() {
        when:
        def emptySet = userEntity.getUserExtensions()

        then:
        emptySet != null
    }

}