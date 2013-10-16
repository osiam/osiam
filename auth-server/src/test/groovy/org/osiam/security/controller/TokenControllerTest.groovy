package org.osiam.security.controller

import org.osiam.security.AuthenticationSpring
import org.osiam.security.AuthorizationRequestSpring
import org.osiam.security.OAuth2AuthenticationSpring
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * Created with IntelliJ IDEA.
 * User: jochen
 * Date: 14.10.13
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
class TokenControllerTest extends Specification {

    def defaultTokenServicesMock = Mock(DefaultTokenServices)
    def tokenController = new TokenController(tokenServices: defaultTokenServicesMock)

    def "The TokenController class should have the appropriate annotations and corresponding value configuration"() {
        when:
        def controllerAnnotation = TokenController.class.getAnnotation(Controller.class)
        def requestMappingAnnotation = TokenController.class.getAnnotation(RequestMapping.class);
        def value = requestMappingAnnotation.annotationType().getMethod("value").invoke(requestMappingAnnotation)

        then:
        controllerAnnotation.annotationType() is Controller
        requestMappingAnnotation.annotationType() is RequestMapping
        value == ['/token']
    }

    def "The validateToken method annotations should be present with appropriate configuration for RequestMapping and Response Body"() {
        given:
        Method method = TokenController.class.getDeclaredMethod("validateToken", String)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)

        then:
        mapping.value() == ["/validate/{token}"]
        mapping.method() == [RequestMethod.GET]
        body
    }

    def "The getToken method annotations should be present with appropriate configuration for RequestMapping and Response Body"() {
        given:
        Method method = TokenController.class.getDeclaredMethod("getToken", String)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)

        then:
        mapping.value() == ["/{token}"]
        mapping.method() == [RequestMethod.GET]
        body
    }

    def "The TokenController should implement spring's token service for token validation and returning OAuth2AuthenticationSpring"() {
        given:
        def oAuth2AuthenticationMock = Mock(OAuth2Authentication)

        when:
        def result = tokenController.validateToken("theToken")

        then:
        1 * defaultTokenServicesMock.loadAuthentication("theToken") >> oAuth2AuthenticationMock
        1 * oAuth2AuthenticationMock.getUserAuthentication() >> Mock(AuthenticationSpring)
        1 * oAuth2AuthenticationMock.getAuthorizationRequest() >> Mock(AuthorizationRequestSpring)
        result instanceof OAuth2AuthenticationSpring
    }

    def "The TokenController should implement springs token service and returning OAuth2AccessToken"() {
        when:
        def result = tokenController.getToken("theToken")

        then:
        1 * defaultTokenServicesMock.readAccessToken("theToken") >> Mock(OAuth2AccessToken)
        result instanceof OAuth2AccessToken
    }
}
