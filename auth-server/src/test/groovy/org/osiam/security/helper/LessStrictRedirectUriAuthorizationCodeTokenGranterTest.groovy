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

package org.osiam.security.helper

import org.springframework.security.oauth2.common.exceptions.InvalidClientException
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
import org.springframework.security.oauth2.provider.code.AuthorizationRequestHolder
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices
import spock.lang.Specification

class LessStrictRedirectUriAuthorizationCodeTokenGranterTest extends Specification {
    def tokenService = Mock(AuthorizationServerTokenServices)
    def clientDetailsService = Mock(ClientDetailsService)
    def authzCodeServices = Mock(AuthorizationCodeServices)
    def underTest = new LessStrictRedirectUriAuthorizationCodeTokenGranter(tokenService, clientDetailsService)
    AuthorizationRequest authzRequest = Mock(AuthorizationRequest)
    AuthorizationRequestHolder storedAuth = Mock(AuthorizationRequestHolder)
    AuthorizationRequest authNRequest = Mock(AuthorizationRequest)
    def pendingRedirectUri = "localhost:unso"

    def setup(){

        authzCodeServices.consumeAuthorizationCode("12345") >> storedAuth
        storedAuth.getAuthenticationRequest() >> authNRequest
        authNRequest.getAuthorizationParameters() >> [redirect_uri: pendingRedirectUri]
        underTest.authorizationCodeServices = authzCodeServices
        authNRequest.getRedirectUri() >> pendingRedirectUri

    }
    def set_redirect_uri(redirect_uri){
        authzRequest.getAuthorizationParameters() >> [code:"12345", redirect_uri: redirect_uri]
    }

    def "should continue if the pending redirect uri is equal to the send redirect uri"(){
        given:
          set_redirect_uri(pendingRedirectUri)

        when:
          def result = underTest.getOAuth2Authentication(authzRequest)
        then:
        result
    }

    def "should continue if the pending redirect uri starts with the send redirect uri"(){
        given:
        set_redirect_uri("localhost")

        when:
        def result = underTest.getOAuth2Authentication(authzRequest)
        then:
        result
    }

    def "should continue if the pending redirect uri is null"(){
        given:
        authNRequest.getAuthorizationParameters() >> [redirect_uri: null]
        set_redirect_uri("localhost")
        when:
        def result = underTest.getOAuth2Authentication(authzRequest)
        then:

        result
    }

    def "should abort if the send redirect uri is null but the pending is not"(){
        given:
        set_redirect_uri(null)
        when:
        underTest.getOAuth2Authentication(authzRequest)
        then:
        def e = thrown(RedirectMismatchException)
        e.message == "Redirect URI mismatch."
    }

    def "should abort if the pending redirect uri doesn't starts with the send redirect uri"(){
        given:
        set_redirect_uri("localhorst")

        when:
        underTest.getOAuth2Authentication(authzRequest)
        then:
        def e = thrown(RedirectMismatchException)
        e.message == "Redirect URI mismatch."
    }

    def "should abort if client_id miss matches"(){
        given:
        set_redirect_uri("localhost")
        authNRequest.getClientId() >> "client_id"
        authzRequest.getClientId() >> "clientid"

        when:
        underTest.getOAuth2Authentication(authzRequest)
        then:
        def e = thrown(InvalidClientException)
        e.message == "Client ID mismatch"
    }

    def "should abort if no authorization code got submitted"(){
        given:
        authzRequest.getAuthorizationParameters() >> [code:null]

        when:
        underTest.getOAuth2Authentication(authzRequest)
        then:
        def e = thrown(OAuth2Exception)
        e.message == "An authorization code must be supplied."
    }

    def "should abort if no storedAuth got found"(){
        given:
        authzRequest.getAuthorizationParameters() >> [code:"123456"]

        when:
        underTest.getOAuth2Authentication(authzRequest)
        then:
        def e = thrown(InvalidGrantException)
        e.message == "Invalid authorization code: 123456"
    }
}
