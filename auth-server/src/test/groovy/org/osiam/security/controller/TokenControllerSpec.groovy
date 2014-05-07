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

package org.osiam.security.controller

import java.lang.reflect.Method

import org.osiam.client.oauth.AccessToken;
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import spock.lang.Specification

class TokenControllerSpec extends Specification {

    DefaultTokenServices defaultTokenServicesMock = Mock()
    TokenController tokenController = new TokenController(tokenServices: defaultTokenServicesMock)

//    def "The TokenController class should have the appropriate annotations and corresponding value configuration"() {
//        when:
//        def controllerAnnotation = TokenController.class.getAnnotation(Controller.class)
//        def requestMappingAnnotation = TokenController.class.getAnnotation(RequestMapping.class);
//        def value = requestMappingAnnotation.annotationType().getMethod("value").invoke(requestMappingAnnotation)
//
//        then:
//        controllerAnnotation.annotationType() is Controller
//        requestMappingAnnotation.annotationType() is RequestMapping
//        value == ['/token']
//    }
//
//    def "The validateToken method annotations should be present with appropriate configuration for RequestMapping and Response Body"() {
//        given:
//        Method method = TokenController.class.getDeclaredMethod("validateToken", String)
//
//        when:
//        RequestMapping mapping = method.getAnnotation(RequestMapping)
//        ResponseBody body = method.getAnnotation(ResponseBody)
//
//        then:
//        mapping.value() == ["/validate/{token}"]
//        mapping.method() == [RequestMethod.GET]
//        body
//    }
//
//    def "The getToken method annotations should be present with appropriate configuration for RequestMapping and Response Body"() {
//        given:
//        Method method = TokenController.class.getDeclaredMethod("getToken", String)
//
//        when:
//        RequestMapping mapping = method.getAnnotation(RequestMapping)
//        ResponseBody body = method.getAnnotation(ResponseBody)
//
//        then:
//        mapping.value() == ["/{token}"]
//        mapping.method() == [RequestMethod.GET]
//        body
//    }
//
//    def "The TokenController should implement spring's token service for token validation and returning OAuth2AuthenticationSpring"() {
//        given:
//        OAuth2Authentication oAuth2AuthenticationMock = Mock()
//
//        when:
//        def result = tokenController.validateToken("theToken")
//
//        then:
//        1 * defaultTokenServicesMock.loadAuthentication("theToken") >> oAuth2AuthenticationMock
//        1 * oAuth2AuthenticationMock.getUserAuthentication() >> Mock(Authentication)
//        1 * oAuth2AuthenticationMock.getAuthorizationRequest() >> Mock(AuthorizationRequest)
//        result instanceof AccessToken
//    }
//
//    def "The TokenController should implement springs token service and returning OAuth2AccessToken"() {
//        when:
//        def result = tokenController.getToken("theToken")
//
//        then:
//        1 * defaultTokenServicesMock.readAccessToken("theToken") >> Mock(OAuth2AccessToken)
//        result instanceof OAuth2AccessToken
//    }
}