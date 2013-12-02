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

import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 24.10.13
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
class ExtensionFieldEntitySpec extends Specification {

    ExtensionFieldEntity extField = new ExtensionFieldEntity();

    def "setter and getter for the Id should be present"() {
        def id = 42
        when:
        extField.setInternalId(id)

        then:
        extField.getInternalId() == id
    }

    def "setter and getter for the name should be present"() {
        def name = "Donald Duck"
        when:
        extField.setName(name)

        then:
        extField.getName() == name
    }

    def "setter and getter for the isRequired should be present"() {

        when:
        extField.setRequired(true)

        then:
        extField.isRequired()
    }

    def "setter and getter for extension should be present"() {
        given:
        def extension = Mock(ExtensionEntity)

        when:
        extField.setExtension(extension)

        then:
        extField.getExtension() == extension
    }
}
