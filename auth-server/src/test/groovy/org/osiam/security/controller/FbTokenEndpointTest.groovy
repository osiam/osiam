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
