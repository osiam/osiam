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
package org.osiam.resources.scim

import org.apache.commons.lang3.SerializationUtils
import org.osiam.test.util.DateHelper
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

class UserSpec extends Specification {

    static def EXTENSION_URN = 'urn:org.osiam:schemas:test:1.0:Test'
    static def EXTENSION_EMPTY = new Extension.Builder(EXTENSION_URN).build()
    static def CORE_SCHEMA_SET = [User.SCHEMA] as Set

    def 'should contain core schemas as default'() {
        when:
        def user = new User.Builder('username').build()
        then:
        user.schemas == CORE_SCHEMA_SET
    }

    def 'should be possible to create an user without name for PATCH'() {
        when:
        def user = new User.Builder().build()
        then:
        user.schemas == CORE_SCHEMA_SET
        user.name == null
    }

    def 'should be able to contain schemas'() {
        when:
        User user = new User.Builder('username').build()

        then:
        user.schemas[0] == User.SCHEMA
    }

    @Unroll
    def 'using #parameter for userName raises exception'() {
        when:
        new User.Builder(parameter)

        then:
        thrown(IllegalArgumentException)

        where:
        parameter << [null, '']
    }

    def 'should generate a user based on builder'() {
        given:

        Extension extension = getExtension('extension')
        Meta meta = new Meta.Builder().build()
        Address address = getAddress()
        Email email = getEmail()
        Entitlement entitlement = getEntitlement()
        Im im = getIm()
        Name name = getName()
        PhoneNumber phoneNumber = getPhoneNumber()
        Photo photo = getPhoto()
        Role role = getRole()
        X509Certificate x509Certificate = getX509Certificate()

        def builder = new User.Builder('username')
                .setId('id')
                .setActive(true)
                .addAddresses([address] as List)
                .setDisplayName('displayName')
                .addEmails([email] as List)
                .addEntitlements([entitlement] as List)
                .setExternalId('externalId')
                .addIms([im] as List)
                .setLocale('de_DE')
                .setName(name)
                .setNickName('nickname')
                .setPassword('password')
                .addPhoneNumbers([phoneNumber] as List)
                .addPhotos([photo] as List)
                .setPreferredLanguage('german')
                .setProfileUrl('/user/username')
                .addRoles([role] as List)
                .setTimezone('MEZ')
                .setTitle('title')
                .addX509Certificates([x509Certificate] as List)
                .setMeta(meta)
                .setUserType('userType')
                .addExtension(extension)

        when:
        User user = builder.build()

        then:
        user.active == builder.active
        user.addresses.first() == address
        user.displayName == 'displayName'
        user.emails == builder.emails
        user.entitlements.first() == entitlement
        user.ims.first() == im
        user.locale == 'de_DE'
        user.name == builder.name
        user.nickName == 'nickname'
        user.password == 'password'
        user.phoneNumbers.first() == phoneNumber
        user.photos.first() == photo
        user.preferredLanguage == 'german'
        user.profileUrl == '/user/username'
        user.roles.first() == role
        user.timezone == 'MEZ'
        user.title == 'title'
        user.userType == 'userType'
        user.x509Certificates.first() == x509Certificate
        user.userName == 'username'
        user.id == 'id'
        user.meta == meta
        user.externalId == 'externalId'
        user.schemas.containsAll([User.SCHEMA, 'extension'])
        user.getExtensions().get('extension').getField('gender', ExtensionFieldType.STRING) == extension.getField('gender', ExtensionFieldType.STRING)
    }

    @Unroll
    def 'creating a user with copy builder copies #field field if present'() {
        given:
        def builder = new User.Builder('user')


        when:
        builder[(field)] = value
        def user = builder.build()
        def copiedUser = new User.Builder(user).build()

        then:
        copiedUser[(field)] == value

        where:
        field              | value
        'emails'           | [new Email.Builder().build()]
        'phoneNumbers'     | [new PhoneNumber.Builder().build()]
        'ims'              | [new Im.Builder().build()]
        'photos'           | [new Photo.Builder().build()]
        'addresses'        | [new Address.Builder().build()]
        'groups'           | [new GroupRef.Builder().build()]
        'entitlements'     | [new Entitlement.Builder().build()]
        'roles'            | [new Role.Builder().build()]
        'x509Certificates' | [new X509Certificate.Builder().build()]
        'extensions'       | [(EXTENSION_URN): (EXTENSION_EMPTY)]
    }

    @Unroll
    def 'creating a user with copy builder initializes #field empty if missing in original'() {
        given:
        def builder = new User.Builder('user')

        when:
        builder[(field)] = value
        def user = builder.build()
        def copiedUser = new User.Builder(user).build()

        then:
        copiedUser[(field)] == value

        where:
        field              | value
        'emails'           | []
        'phoneNumbers'     | []
        'ims'              | []
        'photos'           | []
        'addresses'        | []
        'groups'           | []
        'entitlements'     | []
        'roles'            | []
        'x509Certificates' | []
        'extensions'       | [:]
    }

    def 'enriching extension using the getter raises exception'() {
        given:
        def user = new User.Builder('test2').build()
        when:
        user.getExtensions().put(EXTENSION_URN, EXTENSION_EMPTY)
        then:
        thrown(UnsupportedOperationException)
    }

    def 'builder should add a schema to the schema Set for each added extension'() {
        given:
        def extension1Urn = 'urn:org.osiam:schemas:test:1.0:Test1'
        def extension1 = new Extension.Builder(extension1Urn).build()
        def extension2Urn = 'urn:org.osiam:schemas:test:1.0:Test2'
        def extension2 = new Extension.Builder(extension2Urn).build()
        when:
        def user = new User.Builder('test2')
                .addExtension(extension1)
                .addExtension(extension2)
                .build()
        then:
        user.schemas.contains(extension1Urn)
        user.schemas.contains(extension2Urn)
    }

    def 'scim core schema must always be present in schema set when adding extensions'() {
        given:
        def extension1Urn = 'urn:org.osiam:schemas:test:1.0:Test1'
        def extension1 = new Extension.Builder(extension1Urn).build()
        def extension2Urn = 'urn:org.osiam:schemas:test:1.0:Test2'
        def extension2 = new Extension.Builder(extension2Urn).build()
        def coreSchemaUrn = User.SCHEMA

        when:
        def user = new User.Builder('test2')
                .addExtension(extension1)
                .addExtension(extension2)
                .build()
        then:
        user.schemas.contains(coreSchemaUrn)
    }

    def 'an added extension can be retrieved'() {
        given:
        def user = new User.Builder('test2')
                .addExtension(EXTENSION_EMPTY)
                .build()
        expect:
        user.getExtension(EXTENSION_URN) == EXTENSION_EMPTY
    }

    def 'extensions can be added in bulk'() {
        given:
        def extensions = new HashSet<Extension>()
        extensions.add(EXTENSION_EMPTY)
        def user = new User.Builder('test')
                .addExtensions(extensions)
                .build()

        expect:
        user.getExtension(EXTENSION_URN) == EXTENSION_EMPTY

    }

    @Unroll
    def 'retrieving extension with #testCase urn raises exception'() {
        given:
        def user = new User.Builder('test2')
                .build()
        when:
        user.getExtension(urn)
        then:
        thrown(expectedException)

        where:
        testCase  | urn           | expectedException
        'null'    | null          | IllegalArgumentException
        'empty'   | ''            | IllegalArgumentException
        'invalid' | EXTENSION_URN | NoSuchElementException
    }

    def 'using the copy-of builder with null as parameter raises exception'() {
        when:
        new User.Builder(null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'the copied user should have the given username'() {
        given:
        User oldUser = new User.Builder("oldUserName").setActive(true).build()
        String newUserName = 'newUserName'
        User newUser

        when:
        newUser = new User.Builder(newUserName, oldUser).build()

        then:
        newUser.isActive()
        newUser.getUserName() == newUserName
    }

    def 'the primary email is returned'() {
        given:
        User user = new User.Builder("user")
                .addEmail(getEmail())
                .build()

        expect:
        user.getPrimaryOrFirstEmail().present
        user.getPrimaryOrFirstEmail().get().primary
    }

    def 'the first nonprimary email is returned'() {
        given:
        def first = new Email.Builder()
                .setPrimary(false)
                .setValue('first@tarent.de')
                .build()

        def second = new Email.Builder()
                .setPrimary(false)
                .setValue('second@tarent.de')
                .build()

        when:
        def user = new User.Builder("user")
                .addEmail(first)
                .addEmail(second)
                .build()

        then:
        user.primaryOrFirstEmail.present
        user.primaryOrFirstEmail.get() == first
    }

    def "no email is returned if the user doesn't have one"() {
        given:
        def user = new User.Builder("user").build()

        expect:
        !user.primaryOrFirstEmail.present
    }

    Email getEmail() {
        new Email.Builder()
                .setPrimary(true)
                .setValue('test@tarent.de')
                .setType(Email.Type.WORK)
                .setDisplay('mymail')
                .build()
    }

    Address getAddress() {
        new Address.Builder()
                .setCountry('Germany')
                .setFormatted('formatted')
                .setLocality('Berlin')
                .setPostalCode('12345')
                .setPrimary(true)
                .setRegion('Berlin')
                .setStreetAddress('Voltastr. 5')
                .setType(Address.Type.WORK)
                .setDisplay('myaddress')
                .build()
    }

    Extension getExtension(urn) {
        new Extension.Builder(urn)
                .setField('gender', 'male')
                .build()
    }

    Entitlement getEntitlement() {
        new Entitlement.Builder()
                .setPrimary(true)
                .setType(new Entitlement.Type('irrelevant'))
                .setValue('entitlement')
                .setDisplay('myentitlement')
                .build()
    }

    Im getIm() {
        new Im.Builder()
                .setPrimary(true)
                .setType(Im.Type.AIM)
                .setValue('aim')
                .setDisplay('aim')
                .build()
    }

    Name getName() {
        new Name.Builder()
                .setFamilyName('test')
                .setFormatted('formatted')
                .setGivenName('test')
                .setHonorificPrefix('Dr.')
                .setHonorificSuffix('Mr.')
                .setMiddleName('test')
                .build()
    }

    PhoneNumber getPhoneNumber() {
        new PhoneNumber.Builder()
                .setPrimary(true)
                .setType(PhoneNumber.Type.WORK)
                .setValue('03012345678')
                .setDisplay('myphonenumber')
                .build()
    }

    Photo getPhoto() {
        new Photo.Builder()
                .setPrimary(true)
                .setType(Photo.Type.PHOTO)
                .setValue('username.jpg')
                .setDisplay('myphoto')
                .build()
    }

    Role getRole() {
        new Role.Builder()
                .setPrimary(true)
                .setValue('user_role')
                .setDisplay('myrole')
                .build()
    }

    X509Certificate getX509Certificate() {
        new X509Certificate.Builder()
                .setPrimary(true)
                .setValue('x509Certificate')
                .setDisplay('mycert')
                .build()
    }

    def 'user can be serialized and deserialized'() {
        def date = DateHelper.createDate(2008, 0, 23, 18, 29, 49)
        given:
        User user = new User.Builder('bjensen')
                .setId('a4bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4')
                .setMeta(new Meta.Builder()
                .setLocation('https://example.com/v1/Users/2819c223...')
                .setResourceType('User').build())
                .setExternalId('bjensen')
                .setName(new Name.Builder()
                .setFormatted('Ms. Barbara J Jensen III')
                .setFamilyName('Jensen')
                .setGivenName('Barbara').build())
                .setDisplayName('BarbaraJ.')
                .setNickName('Barbara')
                .setTitle('Dr.')
                .setLocale('de')
                .addEmail(new Email.Builder()
                .setValue('bjensen@example.com')
                .setType(Email.Type.WORK)
                .setDisplay('myemail').build())
                .addPhoto(new Photo.Builder()
                .setValue('example.png').build())
                .addPhoneNumber(new PhoneNumber.Builder()
                .setValue('555-555-8377')
                .setType(PhoneNumber.Type.WORK).build())
                .addAddress(new Address.Builder()
                .setType(Address.Type.WORK)
                .setStreetAddress('example street 42')
                .setLocality('Bonn')
                .setRegion('North Rhine-Westphalia')
                .setPostalCode('11111')
                .setCountry('Germany')
                .setDisplay('myaddress').build())
                .addExtension(new Extension.Builder('urn:scim:schemas:extension:test:1.0:User')
                .setField('keyString', 'example')
                .setField('keyBoolean', true)
                .setField('keyInteger', 123G)
                .setField('keyDecimal', 123.456G)
                .setField('keyBinary', ByteBuffer.wrap([101, 120, 97, 109, 112, 108, 101] as byte[]))
                .setField('keyReference', new URI('https://example.com/Users/28'))
                .setField('keyDateTime', date).build())
                .build()

        when:
        def newUser = (User) SerializationUtils.deserialize(SerializationUtils.serialize(user))

        then:
        user == newUser
        user.getEmails().get(0) == newUser.getEmails().get(0)
        user.getEmails().get(0).getValue() == newUser.getEmails().get(0).getValue()
        user.getEmails().get(0).getType() == newUser.getEmails().get(0).getType()
        user.getPhoneNumbers().get(0) == newUser.getPhoneNumbers().get(0)
        user.getPhoneNumbers().get(0).getDisplay() == newUser.getPhoneNumbers().get(0).getDisplay()
        user.getPhoneNumbers().get(0).getType() == newUser.getPhoneNumbers().get(0).getType()
        user.isActive() == newUser.isActive()

        newUser.getExtension('urn:scim:schemas:extension:test:1.0:User').getFieldAsString('keyString') == 'example'
        newUser.getExtension('urn:scim:schemas:extension:test:1.0:User').getFieldAsBoolean('keyBoolean')
        newUser.getExtension('urn:scim:schemas:extension:test:1.0:User').getFieldAsInteger('keyInteger') == 123G
        newUser.getExtension('urn:scim:schemas:extension:test:1.0:User').getFieldAsDecimal('keyDecimal') == 123.456G
        newUser.getExtension('urn:scim:schemas:extension:test:1.0:User')
                .getFieldAsByteBuffer('keyBinary') == ByteBuffer.wrap([101, 120, 97, 109, 112, 108, 101] as byte[])
        newUser.getExtension('urn:scim:schemas:extension:test:1.0:User')
                .getFieldAsReference('keyReference') == new URI('https://example.com/Users/28')
        newUser.getExtension('urn:scim:schemas:extension:test:1.0:User')
                .getFieldAsDate('keyDateTime') == date

    }
}
