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

class ExtensionEntitySpec extends Specification {

    def fieldName = 'IRRELEVANT'

    def 'setter and getter for the ExtensionFields should be present'() {
        given:
        def fields = [new ExtensionFieldEntity()] as Set
        ExtensionEntity extension = new ExtensionEntity(fields: fields)

        when:
        def result = extension.getFields()

        then:
        result == fields
        result[0].getExtension() == extension
    }

    def 'getFields() on empty Extension returns an empty Set'() {
        expect:
        new ExtensionEntity().getFields() == [] as Set
    }

    def 'getFieldForName() returns the correct FieldEntity'() {
        given:
        ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity(name: fieldName)
        ExtensionEntity extension = new ExtensionEntity(fields: [fieldEntity] as Set)

        expect:
        extension.getFieldForName(fieldName) == fieldEntity

    }

    def 'getFieldForName() for unknown field raises Exception'() {
        given:
        ExtensionEntity extension = new ExtensionEntity()

        when:
        extension.getFieldForName(fieldName)

        then:
        thrown(IllegalArgumentException)
    }

}
