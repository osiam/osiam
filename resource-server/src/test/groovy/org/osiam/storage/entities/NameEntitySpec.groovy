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

package org.osiam.storage.entities

import org.osiam.resources.scim.Name
import org.osiam.storage.entities.NameEntity
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.03.13
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
class NameEntitySpec extends Specification {

    NameEntity nameEntity = new NameEntity()

    def "setter and getter for the Id should be present"() {
        when:
        nameEntity.setId(123456)

        then:
        nameEntity.getId() == 123456
    }

    def "setter and getter for formatted should be present"() {
        when:
        nameEntity.setFormatted("Ms. Barbara J Jensen III")

        then:
        nameEntity.getFormatted() == "Ms. Barbara J Jensen III"
    }

    def "setter and getter for the family name should be present"() {
        when:
        nameEntity.setFamilyName("Jensen")

        then:
        nameEntity.getFamilyName() == "Jensen"
    }

    def "setter and getter for the given name should be present"() {
        when:
        nameEntity.setGivenName("Barbara")

        then:
        nameEntity.getGivenName() == "Barbara"
    }

    def "setter and getter for the middle name should be present"() {
        when:
        nameEntity.setMiddleName("Jane")

        then:
        nameEntity.getMiddleName() == "Jane"
    }

    def "setter and getter for the honorific prefix should be present"() {
        when:
        nameEntity.setHonorificPrefix("Ms.")

        then:
        nameEntity.getHonorificPrefix() == "Ms."
    }

    def "setter and getter for the honorific suffix should be present"() {
        when:
        nameEntity.setHonorificSuffix("III")

        then:
        nameEntity.getHonorificSuffix() == "III"
    }

    def "mapping to scim should be present"() {
        when:
        def name = nameEntity.toScim()

        then:
        name.familyName == nameEntity.familyName
        name.formatted == nameEntity.formatted
        name.givenName == nameEntity.givenName
        name.honorificPrefix == nameEntity.honorificPrefix
        name.honorificSuffix == nameEntity.honorificSuffix
        name.middleName == nameEntity.middleName
    }

    def "mapping from scim should be present"() {
        given:
        Name name = new Name.Builder().
                setFamilyName("familyName").
                setFormatted("formattedName").
                setGivenName("givenName").
                setHonorificPrefix("prefix").
                setHonorificSuffix("suffix").
                setMiddleName("middleName").
                build()

        when:
        def result = NameEntity.fromScim(name)

        then:
        result != null
    }
}