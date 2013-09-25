package org.osiam.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
import spock.lang.Specification

class FbTokenEndpointTest extends Specification {
    TokenEndpoint tokenEndpoint = Mock(TokenEndpoint)
    ResponseEntity tokenEndpointResult = Mock(ResponseEntity)
    def underTest = new FbTokenEndpoint(tokenEndpoint: tokenEndpoint)
    def body = Mock(OAuth2AccessToken)
    def access_token = UUID.randomUUID().toString()

    def setup(){
        tokenEndpointResult.getBody() >> body
    }

    def "should manipulate the result of tokenendpoint to be facebook like"(){

        when:
        def result = underTest.accessToken(null, "authorization-code", null)
        then:
        1 * tokenEndpoint.getAccessToken(null, "authorization-code", null) >> tokenEndpointResult
        1 * body.getValue() >> access_token
        1 * body.expiresIn >> 23
        result == 'access_token='+access_token+'&expires=23'

    }

}
