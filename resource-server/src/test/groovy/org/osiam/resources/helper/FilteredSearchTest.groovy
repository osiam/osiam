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

import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

class FilteredSearchTest extends Specification{
    def parser = new FilterParser()
    def aClass = UserEntity.class

    def "should parse equals (eq)"(){
        when:
        def result = parser.parse("userName eq \"bjensen\"", aClass)
        then:
        result.key == 'userName'
        result.constraint == SingularFilterChain.Constraints.EQUALS
        result.value == "bjensen"
    }

    def "should parse without \""(){
        when:
        def result = parser.parse("userName eq 1", aClass)
        then:
        result.key == 'userName'
        result.constraint == SingularFilterChain.Constraints.EQUALS
        result.value == 1
    }

    def "should parse contains (co)"(){
        when:
        def result = parser.parse("name.familyName co \"O'Malley\"", aClass)
        then:
        result.key == 'name.familyName'
        result.constraint == SingularFilterChain.Constraints.CONTAINS
        result.value == "O'Malley"
    }
    def "should parse starts with (sw)"(){
        when:
        def result = parser.parse("userName sw \"L\"", aClass)
        then:
        result.key == 'userName'
        result.constraint == SingularFilterChain.Constraints.STARTS_WITH
        result.value == "L"

    }
    def "should parse present (pr)"(){
        when:
        def result = parser.parse("title pr", aClass)
        then:
        result.key == 'title'
        result.constraint == SingularFilterChain.Constraints.PRESENT
        !result.value

    }
    def "should parse greater than (gt)"(){
        when:
        def result = parser.parse("meta.lastModified gt \"2011-05-13T04:42:34Z\"", aClass)
        then:
        result.key == 'meta.lastModified'
        result.constraint == SingularFilterChain.Constraints.GREATER_THAN
        result.value

    }
    def "should parse greater than or equal (ge)"(){
        when:
        def result = parser.parse("meta.lastModified ge \"2011-05-13T04:42:34Z\"", aClass)
        then:
        result.key == 'meta.lastModified'
        result.constraint == SingularFilterChain.Constraints.GREATER_EQUALS
        result.value

    }
    def "should parse less than (lt)"(){
        when:
        def result = parser.parse("meta.lastModified lt \"2011-05-13T04:42:34Z\"", aClass)
        then:
        result.key == 'meta.lastModified'
        result.constraint == SingularFilterChain.Constraints.LESS_THAN
        result.value

    }
    def "should parse less than or equal (le)"(){
        when:
        def result = parser.parse("meta.lastModified le \"2011-05-13T04:42:34Z\"", aClass)
        then:
        result.key == 'meta.lastModified'
        result.constraint == SingularFilterChain.Constraints.LESS_EQUALS
        result.value

    }

}
