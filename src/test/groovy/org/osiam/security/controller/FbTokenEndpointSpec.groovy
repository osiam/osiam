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

class FbTokenEndpointSpec extends Specification {

    ResponseEntity tokenEndpointResult = Mock(ResponseEntity)
    OAuth2AccessToken accessToken = Mock(OAuth2AccessToken)
    TokenEndpoint tokenEndpoint = Mock(TokenEndpoint)
    FbTokenEndpoint fbTokenEndpoint = new FbTokenEndpoint(tokenEndpoint: tokenEndpoint)

    def "Manipulates the result of TokenEndpoint to be like Facebook's one"() {
        given:
        def accessTokenValue = UUID.randomUUID().toString()
        tokenEndpointResult.getBody() >> accessToken

        when:
        def result = fbTokenEndpoint.accessToken(null, "authorization-code", null)

        then:
        1 * tokenEndpoint.getAccessToken(null, null) >> tokenEndpointResult
        1 * accessToken.getValue() >> accessTokenValue
        1 * accessToken.getExpiresIn() >> 23
        result == "access_token=${accessTokenValue}&expires=23"
    }
}
