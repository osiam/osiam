package org.osiam.storage.entities.extension

import spock.lang.Specification

class ExtensionEntitySpec extends Specification {

    ExtensionEntity extension = new ExtensionEntity();

    def "setter and getter for the Id should be present"(){
        def id = 42
        when:
        extension.setInternalId(id)

        then:
        extension.getInternalId() == id
    }

    def "setter and getter for the urn should be present"(){
        def urn = "myurn"

        when:
        extension.setExtensionUrn(urn)

        then:
        extension.getExtensionUrn() == urn
    }

    def "setter and getter for the ExtensionFields should be present"(){
        given:
        def fields = [new ExtensionFieldEntity()] as Set
        extension.setExtensionFields(fields)

        when:
        def result = extension.getExtensionFields()

        then:
        result == fields
        result[0].getExtension() == extension
    }

    def "should return empty set if extensionFields is empty"() {
        when:
        def result = extension.getExtensionFields()

        then:
        result != null
    }
}
