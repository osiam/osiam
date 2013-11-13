package org.osiam.resources.exceptions

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 05.07.13
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
class JsonPropertyMessageTransformerSpec extends Specification {

    def jsonPropertyMessageTransformer = new JsonPropertyMessageTransformer()

    def "should return null if message to transform is null"() {
        when:
        def result = jsonPropertyMessageTransformer.transform(null)

        then:
        result == null
    }
}
