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

import org.osiam.storage.entities.RolesEntity
import org.springframework.security.core.GrantedAuthority
import org.osiam.resources.scim.MultiValuedAttribute
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.03.13
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
class RolesEntitySpec extends Specification {

    RolesEntity rolesEntity = new RolesEntity()

    def "setter and getter for the value should be present"(){
        when:
        rolesEntity.setValue("someValue")

        then:
        rolesEntity.getValue() == "someValue"
    }

    def "mapping to scim should be present"() {
        when:
        def multivalue = rolesEntity.toScim()

        then:
        multivalue.value == rolesEntity.value
    }

    def "mapping from scim should be present"() {
        given:
        MultiValuedAttribute attribute = new MultiValuedAttribute.Builder().
                setValue("blaaaa").
                build()

        when:
        def result = RolesEntity.fromScim(attribute)

        then:
        result != null
    }

    def "setter and getter for id should be present"() {
        when:
        rolesEntity.setMultiValueId(1234)

        then:
        rolesEntity.getMultiValueId() == 1234
    }
}