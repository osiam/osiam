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

import org.codehaus.jackson.map.ObjectMapper
import org.osiam.helper.HttpClientHelper
import org.osiam.resources.UserSpring
import spock.lang.Specification

class AuthenticationBeanTest extends Specification {

    def jacksonMapperMock = Mock(ObjectMapper)
    def httpClientHelperMock = Mock(HttpClientHelper)
    def authenticationBean = new AuthenticationBean(mapper: jacksonMapperMock, httpClientHelper: httpClientHelperMock,
            httpScheme: "http", serverHost: "localhost", serverPort: 8080)

    def "AuthenticationBean should implement springs UserDetailsService and therefore returning a user found by user name as UserSpring representation"() {
        given:
        def userSpringMock = Mock(UserSpring)
        def resultingUser = "the resulting user as JSON string"

        when:
        def result = authenticationBean.loadUserByUsername("UserName")

        then:
        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-resource-server/authentication/user/UserName") >> resultingUser
        1 * jacksonMapperMock.readValue(resultingUser, UserSpring.class) >> userSpringMock
        result instanceof UserSpring
    }

    def "If jackson throws an IOException it should be mapped to an RuntimeException"() {
        given:
        def resultingUser = "the resulting user as JSON string"

        when:
        authenticationBean.loadUserByUsername("UserName")

        then:
        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-resource-server/authentication/user/UserName") >> resultingUser
        1 * jacksonMapperMock.readValue(resultingUser, UserSpring.class) >> {throw new IOException()}
        thrown(RuntimeException.class)
    }
}