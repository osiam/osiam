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

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.resources.scim.User
import org.osiam.test.util.DateHelper
import org.osiam.test.util.JsonFixturesHelper
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

class UserDeserializerSpec extends Specification {

    private JsonFixturesHelper jsonFixtures = new JsonFixturesHelper();

    def 'Return a User Instance'() {
        when:
        User user = mapExtendedUser()
        then:
        user instanceof User
    }

    def 'A valid basic user is returned'() {
        when:
        User user = mapBasicUser()
        then:
        user.getUserName() == 'bjensen'
    }

    @Unroll
    def 'Deserializing a simple basic user sets #fieldName field not to null'() {
        when:
        User user = mapSimpleUser()
        then:
        user[fieldName] != null

        where:
        fieldName << [
                'emails',
                'phoneNumbers',
                'ims',
                'photos',
                'addresses',
                'groups',
                'entitlements',
                'roles',
                'x509Certificates',
                'extensions'
        ]
    }

    def 'Extension gets deserialized correctly'() {
        when:
        User user = mapExtendedUser()
        then:
        user.getExtensions().size() == 1
        Extension extension = user.getExtensions().values().first()
        extension.getUrn() == JsonFixturesHelper.ENTERPRISE_URN
    }

    @Unroll
    def 'Value #fieldName is deserialized correctly'() {
        when:
        def user = mapExtendedUser()

        then:
        def extension = user.getExtension(JsonFixturesHelper.ENTERPRISE_URN)
        extension.getField(fieldName, fieldType) == fieldValue

        where:
        fieldType                    | fieldName      | fieldValue
        ExtensionFieldType.STRING    | 'keyString'    | 'example'
        ExtensionFieldType.BOOLEAN   | 'keyBoolean'   | true
        ExtensionFieldType.INTEGER   | 'keyInteger'   | 123
        ExtensionFieldType.DECIMAL   | 'keyDecimal'   | 123.456
        ExtensionFieldType.BINARY    | 'keyBinary'    | ByteBuffer.wrap([
                101,
                120,
                97,
                109,
                112,
                108,
                101] as byte[])
        ExtensionFieldType.REFERENCE | 'keyReference' | new URI('https://example.com/Users/28')
        ExtensionFieldType.DATE_TIME | 'keyDateTime'  | DateHelper.createDate(2011, 7, 1, 18, 29, 49)
    }

    def 'Extension schema registered but missing field raises exception'() {
        when:
        mapInvalidExtendedUser()
        then:
        thrown(JsonProcessingException)
    }

    def 'Extension of wrong JSON type raises exception'() {
        when:
        mapWrongFieldExtendedUser()
        then:
        thrown(JsonMappingException)
    }

    private User mapBasicUser() {
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonBasicUser, User)
    }

    private User mapSimpleUser() {
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonSimpleUser, User)
    }

    private User mapExtendedUser() {
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonExtendedUser, User)
    }

    private User mapInvalidExtendedUser() {
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonExtendedUserWithoutExtensionData, User)
    }

    private User mapWrongFieldExtendedUser() {
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonExtendedUserWithWrongFieldType, User)
    }
}
