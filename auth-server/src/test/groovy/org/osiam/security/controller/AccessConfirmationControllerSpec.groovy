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

import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import spock.lang.Specification

class AccessConfirmationControllerSpec extends Specification{

    AccessConfirmationController underTest = new AccessConfirmationController()
    ClientDetailsService clientDetailsServiceMock = Mock()

    def setup(){
        underTest.setClientDetailsService(clientDetailsServiceMock)
    }

    def "should call clientDetailsService and put auth_request and client into model on access confirmation"(){
        given:
        AuthorizationRequest request = Mock()
        ClientDetails client = Mock()
        request.clientId >> "huch"
        def model = ['authorizationRequest': request]

        when:
        def result = underTest.getAccessConfirmation(model)

        then:
        1 * clientDetailsServiceMock.loadClientByClientId("huch") >> client
        result.model.get("auth_request") == request
        result.model.get("client") == client
    }

    def "should set error message on handleError"(){
        given:
        def model = ['authorizationRequest': null]

        when:
        String result = underTest.handleError(model)

        then:
        result == "oauth_error"
        model.get("message") == "There was a problem with the OAuth2 protocol"
    }
}
