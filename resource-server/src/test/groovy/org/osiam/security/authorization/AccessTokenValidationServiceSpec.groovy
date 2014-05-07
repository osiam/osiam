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

package org.osiam.security.authorization

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication

import spock.lang.Specification

class AccessTokenValidationServiceSpec extends Specification {

    static final String ACCESS_TOKEN = "accessToken"

    def accessTokenValidationService = new AccessTokenValidationService(authServerHome: 'http://localhost:8080/osiam-auth-server')

//    def "Inherit from springs ResourceServerTokenServices and override the method to load the Authentication depending on the given accessToken as String"() {
//        given:
//        def resultAsString = "the result"
//        OAuth2AuthenticationSpring oAuth2AuthenticationSpringMock = Mock()
//        def response = new HttpClientRequestResult(resultAsString, 200)
//
//        when:
//        def result = accessTokenValidationService.loadAuthentication(ACCESS_TOKEN)
//
//        then:
//        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-auth-server/token/validate/accessToken") >> response
//        1 * jacksonMapperMock.readValue(response.body, OAuth2AuthenticationSpring.class) >> oAuth2AuthenticationSpringMock
//        1 * oAuth2AuthenticationSpringMock.getAuthentication() >> Mock(Authentication)
//        1 * oAuth2AuthenticationSpringMock.getAuthorizationRequest() >> Mock(AuthorizationRequest)
//        result instanceof OAuth2Authentication
//    }
//
//    def "Should wrap the IOException from JacksonMapper to RuntimeException for loadAuthentication method"() {
//        given:
//        def resultAsString = "the result"
//        def response = new HttpClientRequestResult(resultAsString, 200)
//
//        when:
//        accessTokenValidationService.loadAuthentication(ACCESS_TOKEN)
//
//        then:
//        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-auth-server/token/validate/accessToken") >> response
//        1 * jacksonMapperMock.readValue(response.body, OAuth2AuthenticationSpring.class) >> { throw new IOException() }
//        thrown(RuntimeException)
//    }
//
//    def "Should throw InvalidTokenException if a token can't be validated"() {
//
//        given:
//        def response = new HttpClientRequestResult("Irrelevant", 401)
//
//        when:
//        accessTokenValidationService.loadAuthentication(ACCESS_TOKEN)
//        then:
//        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-auth-server/token/validate/$ACCESS_TOKEN") >> response
//        thrown(InvalidTokenException)
//    }
//
//    def "Inherit from springs ResourceServerTokenServices and override the method read the OAuth2AccessToken depending on the given accessToken as String"() {
//        given:
//        def resultAsString = "the result"
//        OAuth2AccessToken oAuth2AccessTokenMock = Mock()
//        def response = new HttpClientRequestResult(resultAsString, 200)
//
//        when:
//        def result = accessTokenValidationService.readAccessToken(ACCESS_TOKEN)
//
//        then:
//        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-auth-server/token/$ACCESS_TOKEN") >> response
//        1 * jacksonMapperMock.readValue(response.body, OAuth2AccessToken.class) >> oAuth2AccessTokenMock
//        result instanceof OAuth2AccessToken
//    }
//
//    def "Should wrap the IOException from JacksonMapper to RuntimeException for readAccessToken method"() {
//        given:
//        def resultAsString = "the result"
//        def response = new HttpClientRequestResult(resultAsString, 200)
//
//        when:
//        accessTokenValidationService.readAccessToken(ACCESS_TOKEN)
//
//        then:
//        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-auth-server/token/$ACCESS_TOKEN") >> response
//        1 * jacksonMapperMock.readValue(response.body, OAuth2AccessToken.class) >> { throw new IOException() }
//        thrown(RuntimeException)
//    }
//
//    def "Should throw InvalidTokenException if a token can't be read"() {
//
//        given:
//        def response = new HttpClientRequestResult("Irrelevant", 401)
//
//        when:
//        accessTokenValidationService.readAccessToken(ACCESS_TOKEN)
//        then:
//        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-auth-server/token/$ACCESS_TOKEN") >> response
//        thrown(InvalidTokenException)
//    }
}