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

import javax.servlet.http.HttpServletRequest

import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.ui.Model
import org.springframework.web.servlet.ModelAndView

import spock.lang.Specification

class AccessConfirmationControllerSpec extends Specification{

    ClientDetailsService clientDetailsServiceMock = Mock()
    AccessConfirmationController underTest = new AccessConfirmationController(clientDetailsService: clientDetailsServiceMock)

    def 'should call clientDetailsService and put auth_request and client into model on access confirmation'(){
        given:
        HttpServletRequest httpRequest = Mock()
        AuthorizationRequest authRequest = Mock()
        ClientDetails client = Mock()
        def model = ['authorizationRequest': authRequest]
        authRequest.clientId >> 'huch'
        httpRequest.isUserInRole('ROLE_USER') >> true

        when:
        def result = underTest.getAccessConfirmation(model, httpRequest)

        then:
        1 * clientDetailsServiceMock.loadClientByClientId('huch') >> client
        result.model.get('auth_request') == authRequest
        result.model.get('client') == client
        result.model.get('hasUserRole') == true
        result.viewName == 'access_confirmation'
    }

    def 'should set error message on handleError'(){
        when:
        String result = underTest.handleError()

        then:
        result == 'oauth_error'
    }

}
