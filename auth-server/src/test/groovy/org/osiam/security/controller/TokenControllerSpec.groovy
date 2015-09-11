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

package org.osiam.security.controller

import org.osiam.auth.login.ResourceServerConnector
import org.osiam.auth.oauth_client.ClientRepository
import org.osiam.client.oauth.AccessToken
import org.osiam.client.oauth.Scope
import org.osiam.resources.scim.User
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.TokenStore
import spock.lang.Specification

class TokenControllerSpec extends Specification {

    TokenStore tokenStore = Mock(TokenStore)
    ResourceServerConnector resourceServerConnector = Mock(ResourceServerConnector)
    ClientRepository clientRepository = Mock(ClientRepository)

    TokenController tokenController = new TokenController(
            tokenStore: tokenStore,
            resourceServerConnector: resourceServerConnector,
            clientRepository: clientRepository
    )

    def 'The TokenController should return an access token with all attributes set'() {
        given:
        OAuth2Authentication auth = Mock(OAuth2Authentication)
        OAuth2AccessToken accessToken = Mock(OAuth2AccessToken)
        OAuth2Request authReq = new OAuth2Request(null, 'clientId', null, true, ['GET'] as Set, null, null, null, null)
        Authentication authentication = Mock(Authentication)
        User user = new User.Builder('username').setId('userId').build()
        Date date = new Date()

        when:
        AccessToken result = tokenController.validateToken('accessToken')

        then:
        1 * auth.getOAuth2Request() >> authReq
        1 * auth.getUserAuthentication() >> authentication
        2 * auth.getPrincipal() >> user
        1 * tokenStore.readAuthentication('accessToken') >> auth
        1 * accessToken.getExpiration() >> date
        1 * tokenStore.getAccessToken(auth) >> accessToken

        result.clientId == 'clientId'
        result.userId == 'userId'
        result.userName == 'username'
        result.scopes.contains(new Scope('GET'))
        result.expiresAt == date
    }

    def 'A request to revoke a token should be delegated to the TokenService'() {
        when:
        tokenController.revokeToken('prefix accessToken')

        then:
        1 * tokenStore.removeAccessToken(new DefaultOAuth2AccessToken('accessToken'));
    }

    def 'A request to revoke tokens for a given user should revoke all tokens of the user'() {
        given:
        def userId = 'User Id'
        def userName = 'User name'
        User user = new User.Builder(userName).setId(userId).build()
        OAuth2AccessToken token1 = new DefaultOAuth2AccessToken('token1')
        OAuth2AccessToken token2 = new DefaultOAuth2AccessToken('token2')
        OAuth2AccessToken token3 = new DefaultOAuth2AccessToken('token3')

        when:
        tokenController.revokeAllTokensOfUser(userId)

        then:
        1 * resourceServerConnector.getUserById(userId) >> user
        1 * clientRepository.findAllClientIds() >> ['clientId']
        1 * tokenStore.findTokensByClientIdAndUserName('clientId', user.toString()) >> [token1, token2, token3]
        1 * tokenStore.removeAccessToken(token1.getValue())
        1 * tokenStore.removeAccessToken(token2.getValue())
        1 * tokenStore.removeAccessToken(token3.getValue())
    }

    def 'A request to revoke tokens of a user that does not have any tokens should not have any effect'() {
        given:
        def userId = 'User Id'
        def userName = 'User name'
        User user = new User.Builder(userName).setId(userId).build()

        when:
        tokenController.revokeAllTokensOfUser(userId)

        then:
        1 * resourceServerConnector.getUserById(userId) >> user
        1 * clientRepository.findAllClientIds() >> ['clientId']
        1 * tokenStore.findTokensByClientIdAndUserName('clientId', user.toString()) >> []
        0 * tokenStore.removeAccessToken(_)
    }
}
