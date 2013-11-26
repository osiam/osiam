package org.osiam.storage.entities.extension

import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import spock.lang.Specification

class ExtensionEntitySpec extends Specification {

    def fieldName = 'IRRELEVANT'

    def 'setter and getter for the ExtensionFields should be present'() {
        given:
        def fields = [new ExtensionFieldEntity()] as Set
        ExtensionEntity extension = new ExtensionEntity(fields: fields)

        when:
        def result = extension.getFields()

        then:
        result == fields
        result[0].getExtension() == extension
    }

    def 'getFields() on empty Extension returns an empty Set'() {
        expect:
        new ExtensionEntity().getFields() == [] as Set
    }

    def 'getFieldForName() returns the correct FieldEntity'() {
        given:
        ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity(name: fieldName)
        ExtensionEntity extension = new ExtensionEntity(fields: [fieldEntity] as Set)

        expect:
        extension.getFieldForName(fieldName) == fieldEntity

    }

    def 'getFieldForName() for unknown field raises Exception'() {
        given:
        ExtensionEntity extension = new ExtensionEntity()

        when:
        extension.getFieldForName(fieldName)

        then:
        thrown(IllegalArgumentException)
    }


}
