package org.osiam.storage.entities

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.User
import org.osiam.storage.entities.extension.ExtensionEntity
import org.osiam.storage.entities.extension.ExtensionFieldEntity
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity
import spock.lang.Specification

class UserExtensionSpec extends Specification {

    UserEntity userEntity = new UserEntity()

    def "adding extensions to a user should result in setting the user also to the extension"() {
        given:
        def extensions = [
                new ExtensionFieldValueEntity()] as Set
        userEntity.setUserExtensions(extensions)

        when:
        def result = userEntity.getUserExtensions()

        then:
        result == extensions
        result[0].getUser() == userEntity
    }

    def "If extensions are null empty set should be returned"() {
        when:
        def emptySet = userEntity.getUserExtensions()

        then:
        emptySet != null
    }

    def "mapping of user extensions from scim to entity"() {
        given:
        def user = new User.Builder("userName").
                addExtension("urn1", new Extension(["gender": "male", "age": "30"])).
                addExtension("urn2", new Extension(["newsletter": "true", "size": "180"]))
                .build()

        when:
        def userEntity = UserEntity.fromScim(user)

        then:
        def sortedExtensionList = userEntity.getUserExtensions().sort { it.extensionField.name }

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

    def 'extensions can be registered'() {
        given:
        def extension = new ExtensionEntity()

        when:
        userEntity.registerExtension(extension);

        then:
        userEntity.registeredExtensions.contains(extension)
    }

    def 'registering null as extension raises exception'() {
        when:
        userEntity.registerExtension(null);

        then:
        thrown(IllegalArgumentException)
    }

    def 'adding a field value works'() {
        given:
        def extension = new ExtensionEntity()

        def extensionField = new ExtensionFieldEntity()
        extensionField.extension = extension
        extensionField.name = "testField"

        def fieldValue = new ExtensionFieldValueEntity()

        when:
        userEntity.addOrUpdateExtensionValue(extensionField, fieldValue)

        then:
        userEntity.extensionFieldValues.contains(fieldValue)
    }

    def 'adding null as extension value raises exception'() {
        given:
        def extension = new ExtensionEntity()

        when:
        userEntity.addOrUpdateExtensionValue(new ExtensionFieldEntity(), null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'updating an extension value works'() {
        given:
        def extension = new ExtensionEntity()

        def extensionField = new ExtensionFieldEntity()
        extensionField.extension = extension
        extensionField.name = "testField"

        def fieldValue = new ExtensionFieldValueEntity()
        fieldValue.value = "fieldValue"

        def fieldValueUpdated = new ExtensionFieldValueEntity()
        fieldValueUpdated.value = "fieldValueUpdated"

        userEntity.addOrUpdateExtensionValue(extensionField, fieldValue)

        when:
        userEntity.addOrUpdateExtensionValue(extensionField, fieldValueUpdated)

        then:
        extensionValuesOnlyContains(fieldValueUpdated, extensionField) == true
    }

    def 'updating/adding an extension value sets references to extension field and user'() {
        given:
        def extension = new ExtensionEntity()

        def extensionField = new ExtensionFieldEntity()
        extensionField.extension = extension
        extensionField.name = "testField"

        def fieldValue = new ExtensionFieldValueEntity()
        fieldValue.value = "fieldValue"

        when:
        userEntity.addOrUpdateExtensionValue(extensionField, fieldValue)

        then:
        fieldValue.user == userEntity
        fieldValue.extensionField == extensionField
    }

    def extensionValuesOnlyContains(fieldValue, extensionField) {
        def ok = false;
        for (extensionFieldValue in userEntity.extensionFieldValues) {
            if (extensionFieldValue.extensionField != extensionField) {
                continue
            }

            if (extensionFieldValue.value == fieldValue.value) {
                ok = true
                continue
            } else {
                ok = false
                break
            }
        }
        ok
    }
}
