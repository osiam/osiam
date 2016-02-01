package org.osiam.storage.query

import org.osiam.resources.exception.InvalidFilterException
import org.osiam.resources.scim.Group
import spock.lang.Specification
import spock.lang.Unroll

class GroupFilterExpressionSpec extends Specification {

    @Unroll
    def 'QueryField "#queryField" can be resolved'() {
        given:
        def expression = new GroupFilterExpression("${queryField}", FilterConstraint.EQUALS, 'irrelevant')

        expect:
        expression.field.queryField.toString() == queryField
        expression.field.getUrn() == Group.SCHEMA
        !expression.field.isExtension()

        where:
        queryField << GroupQueryField.values().collect { it.toString() }
    }

    @Unroll
    def 'Resolving fully qualified query field "#queryField"'() {
        given:
        def expression = new GroupFilterExpression("${Group.SCHEMA}:${queryField}", FilterConstraint.EQUALS, 'irrelevant')

        expect:
        expression.field.queryField.toString() == queryField
        expression.field.getUrn() == Group.SCHEMA
        !expression.field.isExtension()

        where:
        queryField << GroupQueryField.values().collect { it.toString() }
    }

    @Unroll
    def 'Separating field "#queryField" by period rasises an exception'() {
        when:
        def expression = new GroupFilterExpression("${Group.SCHEMA}.${queryField}", FilterConstraint.EQUALS, 'irrelevant')

        then:
        thrown(InvalidFilterException)

        where:
        queryField << GroupQueryField.values().collect { it.toString() }
    }

    def 'Querying for group extension raises exception'() {
        when:
        new GroupFilterExpression("urn:field", FilterConstraint.EQUALS, 'irrelevant')

        then:
        thrown(InvalidFilterException)
    }


}
