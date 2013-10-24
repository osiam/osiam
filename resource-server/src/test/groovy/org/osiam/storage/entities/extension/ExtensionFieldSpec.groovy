package org.osiam.storage.entities.extension

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 24.10.13
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
class ExtensionFieldSpec extends Specification {

    ExtensionField extField = new ExtensionField();

    def "setter and getter for the Id should be present"(){
        def id = 42
        when:
        extField.setInternalId(id)

        then:
        extField.getInternalId() == id
    }

    def "setter and getter for the name should be present"(){
        def name = "Donald Duck"
        when:
        extField.setName(name)

        then:
        extField.getName() == name
    }

    def "setter and getter for the type should be present"(){
        def type = ExtensionFieldType.DECIMAL
        when:
        extField.setType(type)

        then:
        extField.getType() == type
    }

    def "setter and getter for the isRequired should be present"(){

        when:
        extField.setRequired(true)

        then:
        extField.isRequired()
    }

}
