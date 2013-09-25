package org.osiam.resources.exceptions

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 05.07.13
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
class JsonMappingMessageTransformerTest extends Specification {

    def jsonMappingMessageTransformer = new JsonMappingMessageTransformer()

    def "should return null if message to transform is null"() {
        when:
        def result = jsonMappingMessageTransformer.transform(null)

        then:
        result == null
    }
}
