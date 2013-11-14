package org.osiam.resources.converter

import org.osiam.resources.scim.*
import org.osiam.storage.dao.UserDAO
import org.osiam.storage.entities.*
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity
import spock.lang.Specification

class UserConverterSpec extends Specification {

    Map fixtures = [displayName: 'displayName',
            externalId: 'externalId',
            locale: 'locale',
            nickName: 'nickName',
            password: 'password',
            preferredLanguage: 'preferredLanguage',
            profileUrl: 'profileURL',
            timezone: 'timeZone',
            title: 'title',
            userName: 'userName',
            userType: 'userType',
            active: true]

    X509CertificateConverter x509CertificateConverter = Mock()
    RoleConverter roleConverter = Mock()
    PhotoConverter photoConverter = Mock()
    PhoneNumberConverter phoneNumberConverter = Mock()
    ImConverter imConverter = Mock()
    EntitlementConverter entitlementConverter = Mock()
    EmailConverter emailConverter = Mock()
    AddressConverter addressConverter = Mock()
    NameConverter nameConverter = Mock()
    ExtensionConverter extensionConverter = Mock()
    MetaConverter metaConverter = Mock()

    UserDAO userDao = Mock()

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
            userDao: userDao
    )

    def 'converting user entity to scim works as expected'() {
        given:
        def uuid = UUID.randomUUID()

        UserEntity userEntity = getFilledUserEntity(uuid)

        when:
        def user = userConverter.toScim(userEntity)

        then:
        1 * extensionConverter.toScim(_) >> ([] as Set<Extension>)
        1 * x509CertificateConverter.toScim(_) >> ([] as Set)
        1 * roleConverter.toScim(_) >> ([] as Set)
        1 * photoConverter.toScim(_) >> ([] as Set)
        1 * phoneNumberConverter.toScim(_) >> ([] as Set)
        1 * imConverter.toScim(_) >> ([] as Set)
        1 * entitlementConverter.toScim(_) >> ([] as Set)
        1 * emailConverter.toScim(_) >> ([] as Set)
        1 * addressConverter.toScim(_) >> ([] as Set)
        1 * nameConverter.toScim(_) >> (new Name())
        1 * metaConverter.toScim(_) >> (new Meta())

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

        userEntity.active == true
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

    def User getFilledUser(UUID internalId) {
        User.Builder userBuilder = new User.Builder(fixtures)
        userBuilder.setId(internalId.toString())

        userBuilder.setX509Certificates([Mock(MultiValuedAttribute)] as List<MultiValuedAttribute>)
        userBuilder.setRoles([Mock(MultiValuedAttribute)] as List<MultiValuedAttribute>)
        userBuilder.setEmails([Mock(MultiValuedAttribute)] as List<MultiValuedAttribute>)
        userBuilder.setEntitlements([Mock(MultiValuedAttribute)] as List<MultiValuedAttribute>)
        userBuilder.setPhoneNumbers([Mock(MultiValuedAttribute)] as List<MultiValuedAttribute>)
        userBuilder.setPhotos([Mock(MultiValuedAttribute)] as List<MultiValuedAttribute>)
        userBuilder.setIms([Mock(MultiValuedAttribute)] as List<MultiValuedAttribute>)
        userBuilder.setAddresses([Mock(Address)] as List<Address>)

        return userBuilder.build()
    }

    def UserEntity getFilledUserEntity(def internalId) {

        UserEntity userEntity = new UserEntity(fixtures)
        GroupEntity groupEntity = Mock(GroupEntity)
        groupEntity.getId() >> { UUID.randomUUID() }

        userEntity.setId(internalId)
        userEntity.setAddresses([Mock(AddressEntity)] as Set<AddressEntity>)
        userEntity.setEmails([Mock(EmailEntity)] as Set<EmailEntity>)
        userEntity.setEntitlements([Mock(EntitlementsEntity)] as Set<EntitlementsEntity>)
        userEntity.addToGroup(groupEntity)
        userEntity.setIms([Mock(ImEntity)] as Set<ImEntity>)
        userEntity.setName(Mock(NameEntity))
        userEntity.setPhoneNumbers([Mock(PhoneNumberEntity)] as Set<PhoneNumberEntity>)
        userEntity.setPhotos([Mock(PhotoEntity)] as Set<PhotoEntity>)
        userEntity.setRoles([Mock(RolesEntity)] as Set<RolesEntity>)
        userEntity.setX509Certificates([Mock(X509CertificateEntity)] as Set<X509CertificateEntity>)
        userEntity.setUserExtensions([Mock(ExtensionFieldValueEntity)] as Set<ExtensionFieldValueEntity>)
        userEntity.setMeta(Mock(MetaEntity))

        return userEntity
    }
}
