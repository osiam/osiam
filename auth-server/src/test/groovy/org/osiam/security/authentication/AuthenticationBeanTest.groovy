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

package org.osiam.security.authentication

import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.resources.UserSpring

import spock.lang.Specification

import com.fasterxml.jackson.databind.ObjectMapper

class AuthenticationBeanTest extends Specification {

    ObjectMapper jacksonMapperMock = Mock()
    HttpClientHelper httpClientHelperMock = Mock()
    AuthenticationBean authenticationBean = new AuthenticationBean(mapper: jacksonMapperMock, httpClientHelper: httpClientHelperMock,
            httpScheme: "http", serverHost: "localhost", serverPort: 8080)

    def "AuthenticationBean should implement springs UserDetailsService and therefore returning a user found by user name as UserSpring representation"() {
        given:
        def userSpringMock = Mock(UserSpring)
        def resultingUser = "the resulting user as JSON string"
        def response = new HttpClientRequestResult(resultingUser, 200)

        when:
        def result = authenticationBean.loadUserByUsername("UserName")

        then:
        1 * httpClientHelperMock.executeHttpPost("http://localhost:8080/osiam-resource-server/authentication/user", "UserName") >> response
        1 * jacksonMapperMock.readValue(response.body, UserSpring.class) >> userSpringMock
        result instanceof UserSpring
    }

}