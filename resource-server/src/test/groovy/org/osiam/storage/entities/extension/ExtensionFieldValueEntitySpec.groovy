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

package org.osiam.storage.entities.extension

import org.osiam.storage.entities.ExtensionFieldEntity
import org.osiam.storage.entities.ExtensionFieldValueEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 24.10.13
 * Time: 13:34
 * To change this template use File | Settings | File Templates.
 */
class ExtensionFieldValueEntitySpec extends Specification {
    ExtensionFieldValueEntity extFieldValue = new ExtensionFieldValueEntity();

    def "setter and getter for the Id should be present"() {
        def id = 42
        when:
        extFieldValue.setInternalId(id)

        then:
        extFieldValue.getInternalId() == id
    }

    def "setter and getter for the extensionField should be present"() {
        def extensionField = Mock(ExtensionFieldEntity.class)
        when:
        extFieldValue.setExtensionField(extensionField)

        then:
        extFieldValue.getExtensionField() == extensionField
    }

    def "setter and getter for the userEntity should be present"() {
        def userEntity = Mock(UserEntity.class)
        when:
        extFieldValue.setUser(userEntity)

        then:
        extFieldValue.getUser() == userEntity
    }

    def "setter and getter for the value should be present"() {
        def value = ":-)"
        when:
        extFieldValue.setValue(value)

        then:
        extFieldValue.getValue() == value
    }
}
