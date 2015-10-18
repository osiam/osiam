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

package org.osiam.resources.converter

import org.osiam.resources.scim.*
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.*
import spock.lang.Specification

class UserConverterSpec extends Specification {

    Map fixtures = [displayName      : 'displayName',
                    externalId       : 'externalId',
                    locale           : 'locale',
                    nickName         : 'nickName',
                    password         : 'password',
                    preferredLanguage: 'preferredLanguage',
                    profileUrl       : 'profileURL',
                    timezone         : 'timeZone',
                    title            : 'title',
                    userName         : 'userName',
                    userType         : 'userType',
                    active           : true]

    def x509CertificateConverter = Mock(X509CertificateConverter)
    def roleConverter = Mock(RoleConverter)
    def photoConverter = Mock(PhotoConverter)
    def phoneNumberConverter = Mock(PhoneNumberConverter)
    def imConverter = Mock(ImConverter)
    def entitlementConverter = Mock(EntitlementConverter)
    def emailConverter = Mock(EmailConverter)
    def addressConverter = Mock(AddressConverter)
    def nameConverter = Mock(NameConverter)
    def extensionConverter = Mock(ExtensionConverter)
    def metaConverter = Mock(MetaConverter)

    def userDao = Mock(UserDao)

    UserConverter userConverter = new UserConverter(
            x509CertificateConverter: x509CertificateConverter,
            roleConverter: roleConverter,
            photoConverter: photoConverter,
            phoneNumberConverter: phoneNumberConverter,
            imConverter: imConverter,
            entitlementConverter: entitlementConverter,
            emailConverter: emailConverter,
            addressConverter: addressConverter,
            nameConverter: nameConverter,
            extensionConverter: extensionConverter,
            metaConverter: metaConverter,
    )

    def 'converting user entity to scim works as expected'() {
        given:
        def uuid = UUID.randomUUID()

        UserEntity userEntity = getFilledUserEntity(uuid)

        when:
        def user = userConverter.toScim(userEntity)

        then:
        1 * extensionConverter.toScim(_) >> ([] as Set<Extension>)
        1 * x509CertificateConverter.toScim(_) >> (new X509Certificate.Builder().build())
        1 * roleConverter.toScim(_) >> (new Role.Builder().build())
        1 * photoConverter.toScim(_) >> (new Photo.Builder().build())
        1 * phoneNumberConverter.toScim(_) >> (new PhoneNumber.Builder().build())
        1 * imConverter.toScim(_) >> (new Im.Builder().build())
        1 * entitlementConverter.toScim(_) >> (new Entitlement.Builder().build())
        1 * emailConverter.toScim(_) >> (new Email.Builder().build())
        1 * addressConverter.toScim(_) >> (new Address.Builder().build())
        1 * nameConverter.toScim(_) >> (new Name.Builder().build())
        1 * metaConverter.toScim(_) >> (new Meta.Builder().build())

        user.id == uuid.toString()
        user.isActive()
        user.displayName == fixtures["displayName"]
        user.externalId == fixtures["externalId"]
        user.locale == fixtures["locale"]
        user.nickName == fixtures["nickName"]
        user.password == fixtures["password"]
        user.preferredLanguage == fixtures["preferredLanguage"]
        user.profileUrl == fixtures["profileUrl"]
        user.timezone == fixtures["timezone"]
        user.title == fixtures["title"]
        user.userName == fixtures["userName"]
        user.userType == fixtures["userType"]
    }

    def 'converting scim user to entity works as expected'() {
        given:
        def uuid = UUID.randomUUID()

        User user = getFilledUser(uuid)

        when:
        def userEntity = userConverter.fromScim(user)

        then:
        1 * extensionConverter.fromScim(_) >> ([] as Set)
        1 * x509CertificateConverter.fromScim(_) >> ([] as Set)
        1 * roleConverter.fromScim(_) >> ([] as Set)
        1 * photoConverter.fromScim(_) >> ([] as Set)
        1 * phoneNumberConverter.fromScim(_) >> ([] as Set)
        1 * imConverter.fromScim(_) >> ([] as Set)
        1 * entitlementConverter.fromScim(_) >> ([] as Set)
        1 * emailConverter.fromScim(_) >> ([] as Set)
        1 * addressConverter.fromScim(_) >> ([] as Set)
        1 * nameConverter.fromScim(_) >> new NameEntity()

        userEntity.active
        userEntity.displayName == fixtures["displayName"]
        userEntity.externalId == fixtures["externalId"]
        userEntity.locale == fixtures["locale"]
        userEntity.nickName == fixtures["nickName"]
        userEntity.password == fixtures["password"]
        userEntity.preferredLanguage == fixtures["preferredLanguage"]
        userEntity.profileUrl == fixtures["profileUrl"]
        userEntity.timezone == fixtures["timezone"]
        userEntity.title == fixtures["title"]
        userEntity.userName == fixtures["userName"]
        userEntity.userType == fixtures["userType"]
    }

    def 'converting null scim user returns null'() {
        expect:
        userConverter.fromScim(null) == null
    }

    def 'converting null user entity returns null'() {
        expect:
        userConverter.toScim(null) == null
    }

    def 'SCIM User without active field set results in entity with active set to false'() {
        given:
        extensionConverter.fromScim(_) >> ([] as Set)
        User user = new User.Builder('irrelevant')
                .build()

        when:
        UserEntity userEntity = userConverter.fromScim(user)

        then:
        !userEntity.getActive()
    }

    def User getFilledUser(UUID internalId) {
        User.Builder userBuilder = new User.Builder(fixtures)
        userBuilder.setId(internalId.toString())

        userBuilder.addX509Certificates([new X509Certificate.Builder().build()] as List<X509Certificate>)
        userBuilder.addRoles([new Role.Builder().build()] as List<Role>)
        userBuilder.addEmails([new Email.Builder().build()] as List<Email>)
        userBuilder.addEntitlements([new Entitlement.Builder().build()] as List<Entitlement>)
        userBuilder.addPhoneNumbers([new PhoneNumber.Builder().build()] as List<PhoneNumber>)
        userBuilder.addPhotos([new Photo.Builder().build()] as List<Photo>)
        userBuilder.addIms([new Im.Builder().build()] as List<Im>)
        userBuilder.addAddresses([new Address.Builder().build()] as List<Address>)

        return userBuilder.build()
    }

    def UserEntity getFilledUserEntity(def internalId) {

        UserEntity userEntity = new UserEntity(fixtures)
        GroupEntity groupEntity = Mock(GroupEntity)
        groupEntity.getId() >> { UUID.randomUUID() }

        userEntity.setId(internalId)
        userEntity.addAddress(Mock(AddressEntity))
        userEntity.addEmail(Mock(EmailEntity))
        userEntity.addEntitlement(Mock(EntitlementEntity))
        userEntity.addToGroup(groupEntity)
        userEntity.addIm(Mock(ImEntity))
        userEntity.setName(Mock(NameEntity))
        userEntity.addPhoneNumber(Mock(PhoneNumberEntity))
        userEntity.addPhoto(Mock(PhotoEntity))
        userEntity.addRole(Mock(RoleEntity))
        userEntity.addX509Certificate(Mock(X509CertificateEntity))
        userEntity.addExtensionFieldValue(Mock(ExtensionFieldValueEntity))
        userEntity.setMeta(Mock(MetaEntity))

        return userEntity
    }
}
