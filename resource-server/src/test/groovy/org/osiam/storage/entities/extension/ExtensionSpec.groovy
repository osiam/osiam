package org.osiam.storage.entities.extension

import spock.lang.Specification

class ExtensionSpec extends Specification {

    Extension extension = new Extension();

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
        def fields = new HashSet<ExtensionField>()
        when:
        extension.setExtensionFields(fields)

        then:
        extension.getExtensionFields() == fields
    }

}
