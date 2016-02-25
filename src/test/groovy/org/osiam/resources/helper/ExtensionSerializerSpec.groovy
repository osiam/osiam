/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.helper

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.restassured.path.json.JsonPath
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.test.util.DateHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.jayway.restassured.path.json.JsonPath.from

class ExtensionSerializerSpec extends Specification {

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    def 'serializing an empty extension works'() {
        given:
        Extension extension = new Extension.Builder('extension').build()

        when:
        JsonPath json = com.jayway.restassured.path.json.JsonPath.from(mapper.writeValueAsString(extension))

        then:
        json.getMap('$').isEmpty()
    }

    @Unroll
    def 'serializing an extension with #type type works'() {
        given:
        Extension extension = new Extension.Builder('extension').setField('key', givenValue).build()

        when:
        JsonPath json = com.jayway.restassured.path.json.JsonPath.from(mapper.writeValueAsString(extension))

        then:
        json.get('key') == expectedValue

        where:
        type                         | givenValue                                    | expectedValue
        ExtensionFieldType.STRING    | 'example'                                     | 'example'
        ExtensionFieldType.INTEGER   | 123G                                          | 123
        ExtensionFieldType.DECIMAL   | 12.3G                                         | 12.3F
        ExtensionFieldType.BOOLEAN   | true                                          | true
        ExtensionFieldType.DATE_TIME | DateHelper.createDate(2008, 0, 23, 4, 56, 22) | '2008-01-23T04:56:22.000Z'
        ExtensionFieldType.BINARY    | ByteBuffer.wrap([
                101,
                120,
                97,
                109,
                112,
                108,
                101] as byte[])                                                      | 'ZXhhbXBsZQ=='
        ExtensionFieldType.REFERENCE | new URI('https://example.com/Users/28')       | 'https://example.com/Users/28'
    }
}
