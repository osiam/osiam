package org.osiam.storage.entities

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
        userEntity.addOrUpdateExtensionValue(fieldValue)

        then:
        userEntity.extensionFieldValues.contains(fieldValue)
    }

    def 'adding null as extension value raises exception'() {
        given:
        def extension = new ExtensionEntity()

        when:
        userEntity.addOrUpdateExtensionValue(null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'updating an extension value works'() {
        given:
        def extension = new ExtensionEntity()

        def extensionField = new ExtensionFieldEntity()
        extensionField.extension = extension
        extensionField.name = "testField"

        def fieldValue = new ExtensionFieldValueEntity(extensionField: extensionField, value: "fieldValue")

        def fieldValueUpdated = new ExtensionFieldValueEntity(extensionField: extensionField, value: "fieldValueUpdated")

        userEntity.addOrUpdateExtensionValue(fieldValue)

        when:
        userEntity.addOrUpdateExtensionValue(fieldValueUpdated)

        then:
        extensionValuesOnlyContains(fieldValueUpdated, extensionField)
    }

    def 'updating/adding an extension value sets references to user'() {
        given:
        def fieldValue = new ExtensionFieldValueEntity()
        fieldValue.value = "fieldValue"

        when:
        userEntity.addOrUpdateExtensionValue(fieldValue)

        then:
        fieldValue.user == userEntity
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
