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

package org.osiam.resources.scim

import spock.lang.Specification

class SchemaRegressionSpec extends Specification {

    def 'REG-BT-13'() {
        given:
        def extension1Urn = "urn:org.osiam:schemas:test:1.0:Test1"
        def extension1 = new Extension.Builder(extension1Urn).build()
        def extension2Urn = "urn:org.osiam:schemas:test:1.0:Test2"
        def extension2 = new Extension.Builder(extension2Urn).build()
        def coreSchemaUrn = User.SCHEMA

        when:
        def userWithExtensions = new User.Builder("test2")
                .addExtension(extension1)
                .addExtension(extension2)
                .build()

        def userWithoutExtensions = new User.Builder("test2")
                .build()
        then:
        userWithoutExtensions.schemas.contains(coreSchemaUrn)
        !userWithoutExtensions.schemas.contains(extension1Urn)
        !userWithoutExtensions.schemas.contains(extension2Urn)
    }
}
