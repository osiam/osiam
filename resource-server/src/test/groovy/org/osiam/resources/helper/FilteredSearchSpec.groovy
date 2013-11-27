/*
 * Copyright 2013
 *     tarent AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osiam.resources.helper

import org.osiam.storage.filter.FilterConstraint
import org.osiam.storage.filter.UserFilterParser
import org.osiam.storage.filter.UserSimpleFilterChain

import spock.lang.Ignore
import spock.lang.Specification

@Ignore('Api change in progress')
class FilteredSearchSpec extends Specification {
    def parser = new UserFilterParser()

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
