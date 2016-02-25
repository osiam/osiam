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

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.spi.AppenderAttachable
import org.osiam.resources.exception.InvalidFilterException
import org.osiam.resources.scim.User
import org.slf4j.LoggerFactory
import spock.lang.Specification
import spock.lang.Unroll


class UserFilterExpressionSpec extends Specification {

    @Unroll
    def 'QueryField "#queryField" can be resolved'() {
        given:
        def expression = new UserFilterExpression("${queryField}", FilterConstraint.EQUALS, 'irrelevant')

        expect:
        expression.field.queryField.toString() == queryField
        expression.field.getUrn() == User.SCHEMA
        !expression.field.isExtension()

        where:
        queryField << UserQueryField.values().collect { it.toString() }
    }

    @Unroll
    def 'Adding the core schema manually to field "#queryField" works'() {
        given:
        def expression = new UserFilterExpression("${User.SCHEMA}:${queryField}", FilterConstraint.CONTAINS, 'irrelewant')

        expect:
        expression.field.queryField.toString() == queryField
        expression.field.getUrn() == User.SCHEMA
        !expression.field.isExtension()

        where:
        queryField << UserQueryField.values().collect { it.toString() }
    }

    @Unroll
    def 'Field separated by "#separator" can be located'() {
        given:
        def expression = new UserFilterExpression("urn${separator}field", FilterConstraint.EQUALS, 'irrelevant')

        expect:
        expression.field.name == 'field'
        expression.field.isExtension()

        where:
        separator << [".", ":"]
    }

    def 'Field without urn is located'() {
        given:
        def expression = new UserFilterExpression('displayname', FilterConstraint.EQUALS, 'irrelevant')

        expect:
        expression.field.name == 'displayname'
    }

    @Unroll
    def 'Separating field "#queryField" by period rasises an exception'() {
        when:
        def expression = new UserFilterExpression("${User.SCHEMA}.${queryField}", FilterConstraint.EQUALS, 'irrelevant')

        then:
        thrown(InvalidFilterException)

        where:
        queryField << UserQueryField.values().collect { it.toString() }
    }

    def 'Make sure warning is logged if field is separated by a period'() {
        given:
        def appender = Mock(Appender)
        def rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
        ((AppenderAttachable<ILoggingEvent>) rootLogger).addAppender(appender)

        when:
        def expression = new UserFilterExpression("urn.field", FilterConstraint.EQUALS, "irrelevant")

        then:
        expression.field.name == 'field'
        expression.field.urn == 'urn'
        1 * appender.doAppend({
            it.message == 'Period (.) used as field separator in urn.field. This is not SCIM conform and deprecated!'
        })
    }
}
