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

import org.osiam.auth.login.ResourceServerConnector
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
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
    ResourceServerConnector resourceServerConnectorMock = Mock()
    TokenController tokenController = new TokenController(tokenServices: defaultTokenServicesMock, 
        resourceServerConnector: resourceServerConnectorMock)

    def 'The TokenController should return an accesstoken with all attributes set'() {
        given:
        OAuth2Authentication auth = Mock()
        OAuth2AccessToken accessToken = Mock()
        AuthorizationRequest authReq = Mock()
        Authentication authentication = Mock()
        User user = new User.Builder('username').setId('userId').build()
        Date date = new Date()
        
        when:
        AccessToken result = tokenController.tokenValidation('accessToken')

        then:
        1 * defaultTokenServicesMock.loadAuthentication('accessToken') >> auth
        1 * defaultTokenServicesMock.getAccessToken(auth) >> accessToken
        1 * auth.authorizationRequest >> authReq
        1 * authReq.clientId >> 'clientId'
        1 * auth.userAuthentication >> authentication
        2 * auth.principal >> user
        1 * authReq.scope >> ['GET']
        1 * accessToken.expiration >> date
        result.clientId == 'clientId'
        result.userId == 'userId'
        result.userName == 'username'
        result.scopes.contains(new Scope('GET'))
        result.expiresAt == date
    }
    
    def 'OSNG-444: A request to revoke a token should be delegated to the TokenService'() {
        when:
        tokenController.tokenRevocation('prefix accessToken')

        then:
        1 * defaultTokenServicesMock.revokeToken('accessToken')
    }

    def 'OSNG-444/OSNG-467: A request to revoke tokens for a given user should revoke all tokens of the user'() {
        given:
        def userId = "User Id"
        def userName = "User name"
        User user = new User.Builder(userName).setId(userId).build()
        OAuth2AccessToken token1 = new DefaultOAuth2AccessToken('token1')
        OAuth2AccessToken token2 = new DefaultOAuth2AccessToken('token2')
        OAuth2AccessToken token3 = new DefaultOAuth2AccessToken('token3')

        when:
        tokenController.tokenRevocation(userId, 'prefix accessToken')

        then:
        1 * resourceServerConnectorMock.getUserById(userId) >> user
        1 * defaultTokenServicesMock.findTokensByUserName(user.toString()) >> [token1, token2, token3]
        1 * defaultTokenServicesMock.revokeToken(token1.getValue())
        1 * defaultTokenServicesMock.revokeToken(token2.getValue())
        1 * defaultTokenServicesMock.revokeToken(token3.getValue())
    }


    def "OSNG-444/OSNG-467: A request to revoke tokens of a user that doesn't have any tokens shouldn't have any effect"() {
        given:
        def userId = "User Id"
        def userName = "User name"
        User user = new User.Builder(userName).setId(userId).build()

        when:
        tokenController.tokenRevocation(userId, 'prefix accessToken')

        then:
        1 * resourceServerConnectorMock.getUserById(userId) >> user
        1 * defaultTokenServicesMock.findTokensByUserName(user.toString())
        0 * defaultTokenServicesMock.revokeToken(_)
    }
}