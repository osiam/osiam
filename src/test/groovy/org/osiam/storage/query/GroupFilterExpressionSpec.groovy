/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
