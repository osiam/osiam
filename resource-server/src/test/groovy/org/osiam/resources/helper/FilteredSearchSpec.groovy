/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.helper

import javax.persistence.EntityManager

import org.osiam.storage.query.FilterConstraint
import org.osiam.storage.query.UserFilterParser
import org.osiam.storage.query.UserSimpleFilterChain

import spock.lang.Ignore
import spock.lang.Specification


@Ignore('Api change in progress')
class FilteredSearchSpec extends Specification {

    EntityManager em = Mock()
    def parser = new UserFilterParser(entityManager: em)

    def 'should parse equals (eq)'() {
        when:
        UserSimpleFilterChain result = parser.parse('userName eq "bjensen"')
        then:
        result.field == 'userName'
        result.constraint == FilterConstraint.EQUALS
        result.value == "bjensen"
    }

    def 'should parse without "'() {
        when:
        UserSimpleFilterChain result = parser.parse('userName eq 1')
        then:
        result.field == 'userName'
        result.constraint == FilterConstraint.EQUALS
        result.value == '1' // <- It's a String!
    }

    def 'should parse contains (co)'() {
        when:
        UserSimpleFilterChain result = parser.parse('name.familyName co "O\'Malley"')
        then:
        result.field == 'name.familyName'
        result.constraint == FilterConstraint.CONTAINS
        result.value == "O'Malley"
    }

    def 'should parse starts with (sw)'() {
        when:
        UserSimpleFilterChain result = parser.parse('userName sw "L"')
        then:
        result.field == 'userName'
        result.constraint == FilterConstraint.STARTS_WITH
        result.value == "L"
    }

    def "should parse present (pr)"() {
        when:
        UserSimpleFilterChain result = parser.parse('title pr')
        then:
        result.field == 'title'
        result.constraint == FilterConstraint.PRESENT
        !result.value
    }

    def "should parse greater than (gt)"() {
        when:
        UserSimpleFilterChain result = parser.parse('meta.lastModified gt "2011-05-13T04:42:34Z"')
        then:
        result.field == 'meta.lastModified'
        result.constraint == FilterConstraint.GREATER_THAN
        result.value
    }

    def "should parse greater than or equal (ge)"() {
        when:
        UserSimpleFilterChain result = parser.parse('meta.lastModified ge "2011-05-13T04:42:34Z"')
        then:
        result.field == 'meta.lastModified'
        result.constraint == FilterConstraint.GREATER_EQUALS
        result.value
    }

    def "should parse less than (lt)"() {
        when:
        UserSimpleFilterChain result = parser.parse('meta.lastModified lt "2011-05-13T04:42:34Z"')
        then:
        result.field == 'meta.lastModified'
        result.constraint == FilterConstraint.LESS_THAN
        result.value
    }

    def 'should parse less than or equal (le)'() {
        when:
        UserSimpleFilterChain result = parser.parse('meta.lastModified le "2011-05-13T04:42:34Z"')

        then:
        result.field == 'meta.lastModified'
        result.constraint == FilterConstraint.LESS_EQUALS
        result.value
    }
}
