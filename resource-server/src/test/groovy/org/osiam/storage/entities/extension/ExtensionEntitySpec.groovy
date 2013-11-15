package org.osiam.storage.entities.extension

import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import spock.lang.Specification

class ExtensionEntitySpec extends Specification {

    ExtensionEntity extension = new ExtensionEntity();

    def "setter and getter for the Id should be present"() {
        def id = 42
        when:
        extension.setInternalId(id)

        then:
        extension.getInternalId() == id
    }

    def "setter and getter for the urn should be present"() {
        def urn = "myurn"

        when:
        extension.setUrn(urn)

        then:
        extension.getUrn() == urn
    }

    def "setter and getter for the ExtensionFields should be present"() {
        given:
        def fields = [new ExtensionFieldEntity()] as Set
        extension.setFields(fields)

        when:
        def result = extension.getFields()

        then:
        result == fields
        result[0].getExtension() == extension
    }

    def "should return empty set if extensionFields is empty"() {
        when:
        def result = extension.getFields()

        then:
        result != null
    }
}
