package org.osiam.resources.helper

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.User
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification

class ScimConverterSpec extends Specification {

    private ScimConverter scimConverter = new ScimConverter() 
    
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
        def userEntity = scimConverter.fromScim(user)

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
        def userEntity = scimConverter.fromScim(user)

        then:
        userEntity.getUserName() == "username"
        userEntity.getExternalId() == null
    }
    
    def "mapping of user extensions from scim to entity"() {
        given:
        def user = new User.Builder("userName").
                addExtension("urn1", new Extension('urn1', ["gender":"male","age":"30"])).
                addExtension("urn2", new Extension('urn2', ["newsletter":"true","size":"180"]))
                .build()

        when:
        def userEntity = scimConverter.fromScim(user)

        then:
        def sortedExtensionList = userEntity.getUserExtensions().sort{it.extensionField.name}

        userEntity.getUserExtensions().size() == 4

        sortedExtensionList[0].getUser() == userEntity
        sortedExtensionList[0].getValue() == "30"
        sortedExtensionList[0].getExtensionField().getName() == "age"
        sortedExtensionList[0].getExtensionField().getExtension().getExtensionUrn() == "urn1"

        sortedExtensionList[1].getUser() == userEntity
        sortedExtensionList[1].getValue() == "male"
        sortedExtensionList[1].getExtensionField().getName() == "gender"
        sortedExtensionList[1].getExtensionField().getExtension().getExtensionUrn() == "urn1"

        sortedExtensionList[2].getUser() == userEntity
        sortedExtensionList[2].getValue() == "true"
        sortedExtensionList[2].getExtensionField().getName() == "newsletter"
        sortedExtensionList[2].getExtensionField().getExtension().getExtensionUrn() == "urn2"

        sortedExtensionList[3].getUser() == userEntity
        sortedExtensionList[3].getValue() == "180"
        sortedExtensionList[3].getExtensionField().getName() == "size"
        sortedExtensionList[3].getExtensionField().getExtension().getExtensionUrn() == "urn2"
    }
    
}
