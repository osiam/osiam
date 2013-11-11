package org.osiam.resources.helper

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.User
import org.osiam.storage.dao.ExtensionDao
import org.osiam.storage.dao.UserDAO
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.extension.ExtensionEntity
import org.osiam.storage.entities.extension.ExtensionFieldEntity;

import spock.lang.Specification

import javax.persistence.NoResultException

class ScimConverterSpec extends Specification {

    def userDao = Mock(UserDAO)
    private ScimConverter scimConverter = new ScimConverter(userDao: userDao)

    private static final VALUE = 'irrelevant'

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

    def setup(){
        userDao.getById(_) >> { throw new NoResultException() }
    }
    def "should be possible to map a scim user class to a user entity"() {
        given:
        def internalId = UUID.randomUUID()

        User user = new User.Builder("username").
                setActive(true).
                setAddresses(addresses).
                setDisplayName("displayname").
                setEmails(emails).
                setEntitlements(entitlements).
                setGroups(groups).
                setIms(ims).
                setLocale("locale").
                setName(scimName).
                setNickName("nickname").
                setPassword("password").
                setPhoneNumbers(phoneNumbers).
                setPhotos(photos).
                setPreferredLanguage("preferredLanguage").
                setProfileUrl("profileUrl").
                setRoles(roles).
                setTimezone("timeZone").
                setTitle("title").
                setUserType("userType").
                setX509Certificates(certificates).
                setExternalId("externalId").
                setId(internalId.toString()).
                build()

        when:
        def userEntity = scimConverter.createFromScim(user)

        then:
        userEntity.getUserName() == "username"
        userEntity.getDisplayName() == "displayname"
        userEntity.getLocale() == "locale"
        userEntity.getNickName() == "nickname"
        userEntity.getPassword() == "password"
        userEntity.getPreferredLanguage() == "preferredLanguage"
        userEntity.getProfileUrl() == "profileUrl"
        userEntity.getTimezone() == "timeZone"
        userEntity.getTitle() == "title"
        userEntity.getUserType() == "userType"
        userEntity.getExternalId() == "externalId"
    }

    def "external id should be null if empty string is provided due to database uniqueness"() {
        given:
        User user = new User.Builder("username").
                setExternalId("").
                build()

        when:
        def userEntity = scimConverter.createFromScim(user)

        then:
        userEntity.getUserName() == "username"
        userEntity.getExternalId() == null
    }

    def 'mapping of user extensions from scim to entity'() {
        given:
        ExtensionEntity extensionEntity = new ExtensionEntity()

        ExtensionFieldEntity extensionFieldGender = new ExtensionFieldEntity([name: 'gender', extension: extensionEntity])
        ExtensionFieldEntity extensionFieldAge = new ExtensionFieldEntity([name: 'age', extension: extensionEntity])

        extensionEntity.urn = 'urn1'
        extensionEntity.fields = [extensionFieldGender, extensionFieldAge] as Set

        ExtensionDao extensionDao = Mock(ExtensionDao)
        extensionDao.getExtensionByUrn('urn1') >> extensionEntity
        
        Extension extension = new Extension('urn1')
        extension.addOrUpdateField('gender', 'male')
        extension.addOrUpdateField('age', '30')
        
        def user = new User.Builder('userName')
                .addExtension('urn1', extension)
                .build()
        scimConverter.extensionDao = extensionDao

        when:
        def userEntity = scimConverter.createFromScim(user)

        then:
        def sortedExtensionList = userEntity.getUserExtensions().sort{it.extensionField.name}

        userEntity.getUserExtensions().size() == 2

        sortedExtensionList[0].getUser() == userEntity
        sortedExtensionList[0].getValue() == '30'
        sortedExtensionList[0].getExtensionField().getName() == 'age'
        sortedExtensionList[0].getExtensionField().getExtension().getUrn() == 'urn1'

        sortedExtensionList[1].getUser() == userEntity
        sortedExtensionList[1].getValue() == 'male'
        sortedExtensionList[1].getExtensionField().getName() == 'gender'
        sortedExtensionList[1].getExtensionField().getExtension().getUrn() == 'urn1'
    }

}
