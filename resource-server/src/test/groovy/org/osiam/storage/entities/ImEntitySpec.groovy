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

import spock.lang.Specification


class ImEntitySpec extends Specification {

    ImEntity imsEntity = new ImEntity()
    def userEntity = Mock(UserEntity)

    def "setter and getter for the value should be present"() {
        when:
        imsEntity.setValue("someone@googlemail.com")

        then:
        imsEntity.getValue() == "someone@googlemail.com"
    }

    def "setter and getter for the type should be present"() {
        when:
        imsEntity.setType("gtalk")

        then:
        imsEntity.getType() == "gtalk"
    }

    def "setter and getter for the user should be present"() {
        when:
        imsEntity.setUser(userEntity)

        then:
        imsEntity.getUser() == userEntity
    }

    def "should throw an exception if the type is unknown"() {
        when:
        imsEntity.setType("huch")

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "No enum constant org.osiam.storage.entities.ImEntity.CanonicalImTypes.huch"
    }

    def "setter and getter for id should be present"() {
        when:
        imsEntity.setMultiValueId(1234)

        then:
        imsEntity.getMultiValueId() == 1234
    }
}