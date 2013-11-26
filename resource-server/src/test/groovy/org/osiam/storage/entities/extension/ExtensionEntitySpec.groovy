package org.osiam.storage.entities.extension

import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import spock.lang.Specification

class ExtensionEntitySpec extends Specification {

    ExtensionEntity extension = new ExtensionEntity();

    def 'setter and getter for the ExtensionFields should be present'() {
        given:
        def fields = [new ExtensionFieldEntity()] as Set
        extension.setFields(fields)

        when:
        def result = extension.getFields()

        then:
        result == fields
        result[0].getExtension() == extension
    }

    def 'should return empty set if extensionFields is empty'() {
        when:
        def result = extension.getFields()

        then:
        result != null
    }
}
