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

package org.osiam.resources.helper

import com.fasterxml.jackson.databind.JsonMappingException
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.User
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class JsonInputValidatorSpec extends Specification {

    def jsonInputValidator = new JsonInputValidator()
    HttpServletRequest httpServletRequestMock
    BufferedReader readerMock = Mock()

    def setup() {
        servletRequestWithMethod 'POST'
    }

    def 'validating user if JSON is valid works'() {
        given:
        def userJson = """{"userName":"irrelevant","password": "123","schemas" : ["$User.SCHEMA"]}"""
        readerMock.readLine() >>> [userJson, null]

        when:
        def user = jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        user.userName == 'irrelevant'
    }

    def 'missing mandatory userName raises exception'() {
        given:
        def userJson = """{"schemas" : ["$User.SCHEMA"]}"""
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        thrown(IllegalArgumentException)
    }

    def 'missing mandatory schema declaration for user raises exception'() {
        given:
        def userJson = '{"userName" : "irrelevant"}'
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        thrown(JsonMappingException)
    }

    def 'using PATCH without userName field works'() {
        given:
        def userJson = """{"password":"123", "schemas" : ["$User.SCHEMA"]}"""
        servletRequestWithMethod 'PATCH'
        readerMock.readLine() >>> [userJson, null]

        expect:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)
    }

    def 'validating invalid JSON structure raises exception'() {
        given:
        def userJson = """{"schemas":["$User.SCHEMA"],"userName":"irrelevant","password":"123"""
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        thrown(IllegalArgumentException)
    }

    def 'removing uuid from user when using POST works'() {
        given:
        def userJson = """{"id":"irrelevant","schemas":["$User.SCHEMA"],"userName":"irrelevant","password":"123"}"""
        readerMock.readLine() >>> [userJson, null]

        when:
        def user = jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        user.id == null
    }

    def 'validating group if JSON is valid works'() {
        given:
        def groupJson = """{"schemas":["$Group.SCHEMA"],"displayName":"display name"}"""
        readerMock.readLine() >>> [groupJson, null]

        when:
        def group = jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        group.displayName == 'display name'
    }

    def 'missing mandatory displayName raises exception'() {
        given:
        def groupJson = """{"schemas":["$Group.SCHEMA"],"members": []}"""
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        thrown(IllegalArgumentException)
    }

    def 'using PATCH without displayName works'() {
        given:
        def groupJson = """{"schemas":["$Group.SCHEMA"],"members": []}"""
        servletRequestWithMethod 'PATCH'
        readerMock.readLine() >>> [groupJson, null]

        expect:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

    }

    def 'trying to validate invalid JSON structure for group raises exception'() {
        given:
        def groupJson = '{"displayName":"display name"'
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        thrown(IllegalArgumentException)
    }

    def servletRequestWithMethod(String requestMethodType) {
        httpServletRequestMock = Mock()
        httpServletRequestMock.getReader() >> readerMock
        httpServletRequestMock.getMethod() >> requestMethodType
    }
}
