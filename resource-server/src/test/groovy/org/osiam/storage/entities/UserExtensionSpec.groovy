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
