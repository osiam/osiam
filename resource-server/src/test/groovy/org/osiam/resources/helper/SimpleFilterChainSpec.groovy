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

import org.osiam.storage.entities.*
import spock.lang.Specification

class SimpleFilterChainSpec extends Specification {

    def userEntity = new UserEntity()

    def "should parse equals (eq)"() {
        when:
        def result = new UserSimpleFilterChain("userName eq \"bjensen\"", aClass).buildCriterion()
        then:
        result.propertyName == 'userName'
        result.op == "="
        result.value == "bjensen"
    }

    def "should parse without \""() {
        when:
        def result = new UserSimpleFilterChain("userName eq 1", aClass).buildCriterion()
        then:
        result.propertyName == 'userName'
        result.op == "="
        result.value == 1
    }

    def "should parse contains (co)"() {
        when:
        def result = new UserSimpleFilterChain("name.familyName co \"O'Malley\"", aClass).buildCriterion()
        then:
        result.propertyName == 'name.familyName'
        result.op == " like "
        result.value == "%O'Malley%"
    }

    def "should parse starts with (sw)"() {
        when:
        def result = new UserSimpleFilterChain("userName sw \"L\"", aClass).buildCriterion()
        then:
        result.propertyName == 'userName'
        result.op == " like "
        result.value == "L%"

    }

    def "should parse present (pr)"() {
        when:
        def result = new UserSimpleFilterChain("title pr", aClass).buildCriterion()
        then:
        result.propertyName == 'title'
    }

    def "should parse greater than (gt)"() {
        when:
        def result = new UserSimpleFilterChain("meta.lastModified gt \"2011-05-13T04:42:34Z\"", aClass).buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == ">"
        result.value

    }

    def "should parse greater than or equal (ge)"() {
        when:
        def result = new UserSimpleFilterChain("meta.lastModified ge \"2011-05-13T04:42:34Z\"", aClass).buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == ">="
        result.value

    }

    def "should parse less than (lt)"() {
        when:
        def result = new UserSimpleFilterChain("meta.lastModified lt \"2011-05-13T04:42:34Z\"", aClass).buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == "<"
        result.value

    }

    def "should parse less than or equal (le)"() {
        when:
        def result = new UserSimpleFilterChain("meta.lastModified le \"2011-05-13T04:42:34Z\"", aClass).buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == "<="
        result.value instanceof Date

    }

    def "should parse iso time"() {
        when:
        def result = new UserSimpleFilterChain("meta.lastModified le \"2011-05-13T04:42:34+02\"", aClass).buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == "<="
        result.value instanceof Date

    }

    def "should throw exception if no constraint matches"() {
        when:
        new UserSimpleFilterChain("userName xx \"bjensen\"", aClass)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.getMessage() == "userName xx \"bjensen\"" + " is not a SingularFilterChain."
    }

    def "should be able to search for address values"() {
        when:
        def result = new UserSimpleFilterChain("addresses.streetAddress eq theStreet", aClass).buildCriterion()
        then:
        result.propertyName == 'addresses.streetAddress'
        result.op == "="
        result.value instanceof String
    }

    def "should be possible to search for user boolean values like active"() {
        when:
        def result = new UserSimpleFilterChain("active eq true", aClass).buildCriterion()
        then:
        result.propertyName == 'active'
        result.op == "="
        result.value instanceof Boolean
    }

    def "should throw exception if boolean values are not valid"() {
        when:
        new UserSimpleFilterChain("active eq crap", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "Value of Field active mismatch!"
    }

    def "should be possible to search for multiValue boolean values like emails.primary"() {
        when:
        def result = new UserSimpleFilterChain("emails.primary eq true", aClass).buildCriterion()
        then:
        result.propertyName == 'emails.primary'
        result.op == "="
        result.value instanceof Boolean
    }

    def "should throw exception if multiValue boolean values are not valid"() {
        when:
        new UserSimpleFilterChain("emails.primary eq crap", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "Value of Field emails.primary mismatch!"
    }

    def "should be possible to search for multiValue types like emails.type"() {
        when:
        def result = new UserSimpleFilterChain("emails.type eq work", aClass).buildCriterion()
        then:
        result.propertyName == 'emails.type'
        result.op == "="
        result.value instanceof EmailEntity.CanonicalEmailTypes
    }

    def "should throw exception if emails.type mismatch"() {
        when:
        new UserSimpleFilterChain("emails.type eq crap", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "No enum constant org.osiam.storage.entities.EmailEntity.CanonicalEmailTypes.crap"
    }

    def "should be possible to search for multiValue types like phoneNumbers.type"() {
        when:
        def result = new UserSimpleFilterChain("phoneNumbers.type eq mobile", aClass).buildCriterion()
        then:
        result.propertyName == 'phoneNumbers.type'
        result.op == "="
        result.value instanceof PhoneNumberEntity.CanonicalPhoneNumberTypes
    }

    def "should throw exception if phoneNumbers.type mismatch"() {
        when:
        new UserSimpleFilterChain("phoneNumbers.type eq crap", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "No enum constant org.osiam.storage.entities.PhoneNumberEntity.CanonicalPhoneNumberTypes.crap"
    }

    def "should be possible to search for multiValue types like photos.type"() {
        when:
        def result = new UserSimpleFilterChain("photos.type eq photo", aClass).buildCriterion()
        then:
        result.propertyName == 'photos.type'
        result.op == "="
        result.value instanceof PhotoEntity.CanonicalPhotoTypes
    }

    def "should throw exception if photos.type mismatch"() {
        when:
        new UserSimpleFilterChain("photos.type eq crap", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "No enum constant org.osiam.storage.entities.PhotoEntity.CanonicalPhotoTypes.crap"
    }

    def "should be possible to search for multiValue types like ims.type"() {
        when:
        def result = new UserSimpleFilterChain("ims.type eq icq", aClass).buildCriterion()
        then:
        result.propertyName == 'ims.type'
        result.op == "="
        result.value instanceof ImEntity.CanonicalImTypes
    }

    def "should throw exception if ims.type mismatch"() {
        when:
        new UserSimpleFilterChain("ims.type eq crap", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "No enum constant org.osiam.storage.entities.ImEntity.CanonicalImTypes.crap"
    }

    def "should be possible to search for address boolean values like addresses.primary"() {
        when:
        def result = new UserSimpleFilterChain("addresses.primary eq true", aClass).buildCriterion()
        then:
        result.propertyName == 'addresses.primary'
        result.op == "="
        result.value instanceof Boolean
    }

    def "should throw exception if addresses boolean values are not valid"() {
        when:
        new UserSimpleFilterChain("addresses.primary eq crap", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "Value of Field addresses.primary mismatch!"
    }

    def "should throw exception if EmailEntity Enum values are used with filter operator co"() {
        when:
        new UserSimpleFilterChain("emails.type co work", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "String filter operators 'co' and 'sw' are not applicable on field 'type'."
    }

    def "should throw exception if EmailEntity Enum values are used with filter operator sw"() {
        when:
        new UserSimpleFilterChain("emails.type sw work", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "String filter operators 'co' and 'sw' are not applicable on field 'type'."
    }

    def "should throw exception if Boolean values are used with filter operator co"() {
        when:
        new UserSimpleFilterChain("active co true", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "String filter operators 'co' and 'sw' are not applicable on field 'active' of type 'Boolean'."
    }

    def "should throw exception if Boolean values are used with filter operator sw"() {
        when:
        new UserSimpleFilterChain("active sw true", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "String filter operators 'co' and 'sw' are not applicable on field 'active' of type 'Boolean'."
    }

    def "should throw exception if field name is not available"() {
        when:
        new UserSimpleFilterChain("non-existent sw non", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "Filtering not possible. Field 'non-existent' not available."
    }

    def "should throw exception if field prefix is missing on subkey"() {
        when: "prefix 'meta. is missing on attribute created"
        new UserSimpleFilterChain("created eq 2013-08-08T19:46:20.638", aClass).buildCriterion()
        then:
        def result = thrown(IllegalArgumentException.class)
        result.getMessage() == "Filtering not possible. Field 'created' not available."
    }
}