/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.security.helper

import org.osiam.auth.oauth_client.ClientEntity
import org.springframework.security.oauth2.common.exceptions.InvalidClientException
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException
import org.springframework.security.oauth2.provider.*
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices
import spock.lang.Specification

class LessStrictRedirectUriAuthorizationCodeTokenGranterSpec extends Specification {
    AuthorizationServerTokenServices tokenServices = Mock(AuthorizationServerTokenServices)
    ClientDetailsService clientDetailsService = Mock(ClientDetailsService)
    AuthorizationCodeServices authorizationCodeServices = Mock(AuthorizationCodeServices)
    OAuth2RequestFactory requestFactory = Mock(OAuth2RequestFactory)

    LessStrictRedirectUriAuthorizationCodeTokenGranter lessStrictRedirectUriAuthorizationCodeTokenGranter =
            new LessStrictRedirectUriAuthorizationCodeTokenGranter(
                    tokenServices,
                    authorizationCodeServices,
                    clientDetailsService,
                    requestFactory
            )

    def 'continues if the pending redirect uri is equal to the send redirect uri'() {
        given:
        TokenRequest tokenRequest = new TokenRequest([code: '12345', redirect_uri: 'localhost:5000/oauth'], 'clientId',
                [], 'authorization_code')
        OAuth2Authentication storedAuth = Mock(OAuth2Authentication)
        authorizationCodeServices.consumeAuthorizationCode('12345') >> storedAuth
        OAuth2Request pendingOAuth2Request = new OAuth2Request(
                [redirect_uri: 'localhost:5000/oauth'],
                'clientId',
                null,
                true,
                ['GET'] as Set,
                null,
                'localhost:5000/oauth',
                null,
                null)
        storedAuth.getOAuth2Request() >> pendingOAuth2Request

        when:
        def result = lessStrictRedirectUriAuthorizationCodeTokenGranter
                .getOAuth2Authentication(new ClientEntity(), tokenRequest)

        then:
        result
    }

    def 'continues if the pending redirect uri starts with the send redirect uri'() {
        given:
        TokenRequest tokenRequest = new TokenRequest([code: '12345', redirect_uri: 'localhost'], 'clientId',
                [], 'authorization_code')
        OAuth2Authentication storedAuth = Mock(OAuth2Authentication)
        authorizationCodeServices.consumeAuthorizationCode('12345') >> storedAuth
        OAuth2Request pendingOAuth2Request = new OAuth2Request(
                [redirect_uri: 'localhost:5000/oauth'],
                'clientId',
                null,
                true,
                ['GET'] as Set,
                null,
                'localhost:5000/oauth',
                null,
                null)
        storedAuth.getOAuth2Request() >> pendingOAuth2Request

        when:
        def result = lessStrictRedirectUriAuthorizationCodeTokenGranter
                .getOAuth2Authentication(new ClientEntity(), tokenRequest)

        then:
        result
    }

    def 'continues if the pending redirect uri is null'() {
        given:
        TokenRequest tokenRequest = new TokenRequest([code: '12345', redirect_uri: 'localhost:5000/oauth'], 'clientId',
                [], 'authorization_code')
        OAuth2Authentication storedAuth = Mock(OAuth2Authentication)
        authorizationCodeServices.consumeAuthorizationCode('12345') >> storedAuth
        OAuth2Request pendingOAuth2Request = new OAuth2Request(
                [redirect_uri: null],
                'clientId',
                null,
                true,
                ['GET'] as Set,
                null,
                null,
                null,
                null)
        storedAuth.getOAuth2Request() >> pendingOAuth2Request

        when:
        def result = lessStrictRedirectUriAuthorizationCodeTokenGranter
                .getOAuth2Authentication(new ClientEntity(), tokenRequest)

        then:
        result
    }

    def 'aborts if the send redirect uri is null but the pending is not'() {
        given:
        TokenRequest tokenRequest = new TokenRequest([code: '12345', redirect_uri: null], 'clientId',
                [], 'authorization_code')
        OAuth2Authentication storedAuth = Mock(OAuth2Authentication)
        authorizationCodeServices.consumeAuthorizationCode('12345') >> storedAuth
        OAuth2Request pendingOAuth2Request = new OAuth2Request(
                [redirect_uri: 'localhost:5000/oauth'],
                'clientId',
                null,
                true,
                ['GET'] as Set,
                null,
                'localhost:5000/oauth',
                null,
                null)
        storedAuth.getOAuth2Request() >> pendingOAuth2Request

        when:
        lessStrictRedirectUriAuthorizationCodeTokenGranter.getOAuth2Authentication(new ClientEntity(), tokenRequest)

        then:
        def e = thrown(RedirectMismatchException)
        e.message == 'Redirect URI mismatch.'
    }

    def 'aborts if the pending redirect uri does not starts with the send redirect uri'() {
        given:
        TokenRequest tokenRequest = new TokenRequest([code: '12345', redirect_uri: 'example.com'], 'clientId',
                [], 'authorization_code')
        OAuth2Authentication storedAuth = Mock(OAuth2Authentication)
        authorizationCodeServices.consumeAuthorizationCode('12345') >> storedAuth
        OAuth2Request pendingOAuth2Request = new OAuth2Request(
                [redirect_uri: 'localhost:5000/oauth'],
                'clientId',
                null,
                true,
                ['GET'] as Set,
                null,
                'localhost:5000/oauth',
                null,
                null)
        storedAuth.getOAuth2Request() >> pendingOAuth2Request

        when:
        lessStrictRedirectUriAuthorizationCodeTokenGranter.getOAuth2Authentication(new ClientEntity(), tokenRequest)

        then:
        def e = thrown(RedirectMismatchException)
        e.message == 'Redirect URI mismatch.'
    }

    def 'aborts if client_id miss matches'() {
        given:
        TokenRequest tokenRequest = new TokenRequest([code: '12345', redirect_uri: 'localhost:5000/oauth'], 'clientId',
                [], 'authorization_code')
        OAuth2Authentication storedAuth = Mock(OAuth2Authentication)
        authorizationCodeServices.consumeAuthorizationCode('12345') >> storedAuth
        OAuth2Request pendingOAuth2Request = new OAuth2Request(
                [redirect_uri: 'localhost:5000/oauth'],
                'otherClientId',
                null,
                true,
                ['GET'] as Set,
                null,
                'localhost:5000/oauth',
                null,
                null)
        storedAuth.getOAuth2Request() >> pendingOAuth2Request

        when:
        lessStrictRedirectUriAuthorizationCodeTokenGranter.getOAuth2Authentication(new ClientEntity(), tokenRequest)

        then:
        def e = thrown(InvalidClientException)
        e.message == 'Client ID mismatch'
    }

    def 'aborts if no authorization code got submitted'() {
        given:
        TokenRequest tokenRequest = new TokenRequest([redirect_uri: 'localhost:5000/oauth'], 'clientId',
                [], 'authorization_code')

        when:
        lessStrictRedirectUriAuthorizationCodeTokenGranter.getOAuth2Authentication(new ClientEntity(), tokenRequest)

        then:
        def e = thrown(OAuth2Exception)
        e.message == 'An authorization code must be supplied.'
    }

    def 'aborts if no storedAuth has been found'() {
        given:
        TokenRequest tokenRequest = new TokenRequest([code: '12345', redirect_uri: 'localhost:5000/oauth'], 'clientId',
                [], 'authorization_code')
        authorizationCodeServices.consumeAuthorizationCode('12345') >> null

        when:
        lessStrictRedirectUriAuthorizationCodeTokenGranter.getOAuth2Authentication(new ClientEntity(), tokenRequest)

        then:
        def e = thrown(InvalidGrantException)
        e.message == 'Invalid authorization code: 12345'
    }

}
