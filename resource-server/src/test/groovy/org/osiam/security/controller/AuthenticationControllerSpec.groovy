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

import org.osiam.resources.ClientSpring
import org.osiam.resources.UserSpring
import org.osiam.storage.dao.ClientDao
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.ClientEntity
import org.osiam.storage.entities.RolesEntity
import org.osiam.storage.entities.UserEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

import spock.lang.Specification

class AuthenticationControllerSpec extends Specification {

    def userDaoMock = Mock(UserDao)
    def clientDaoMock = Mock(ClientDao)
    def authenticationController = new AuthenticationController(userDao: userDaoMock, clientDao: clientDaoMock)

    def "Should be able to load an User by his name an getting a UserSpring representation"() {
        given:
        def userEntityMock = Mock(UserEntity)
        def role = new RolesEntity()
        role.setValue("TestRole")
        def roles = [role] as Set
        def userId = UUID.randomUUID()

        when:
        def result = authenticationController.getUser("userName")

        then:
        1 * userDaoMock.getByUsername("userName") >> userEntityMock
        1 * userEntityMock.getRoles() >> roles
        1 * userEntityMock.getUserName() >> "userName"
        1 * userEntityMock.getPassword() >> "password"
        1 * userEntityMock.getId() >> userId
        1 * userEntityMock.getActive() >> true
        result instanceof UserSpring
        result.getUsername() == "userName"
        result.getPassword() == "password"
        result.getId() == userId.toString()
        result.isActive()
    }

    def "The getClient method annotations should be present with appropriate configuration for RequestMapping and Response Body"() {
        given:
        Method method = AuthenticationController.class.getDeclaredMethod("getClient", String)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)

        then:
        mapping.value() == ["/client/{id}"]
        mapping.method() == [RequestMethod.GET]
        body
    }

    def "Should be able to load an Client by his Id an getting a ClientSpring representation"() {
        given:
        def clientEntityMock = Mock(ClientEntity)
        def date = new Date()
        def scopes = ["scope"] as Set
        def grants = ["grant"] as Set

        when:
        def result = authenticationController.getClient("clientId")

        then:
        1 * clientDaoMock.getClient("clientId") >> clientEntityMock
        1 * clientEntityMock.getId() >> "clientId"
        1 * clientEntityMock.getClientSecret() >> "clientSecret"
        1 * clientEntityMock.getScope() >> scopes
        1 * clientEntityMock.getGrants() >> grants
        1 * clientEntityMock.getRedirectUri() >> "redirectURI"
        1 * clientEntityMock.getAccessTokenValiditySeconds() >> 1234
        1 * clientEntityMock.isImplicit() >> true
        1 * clientEntityMock.getExpiry() >> date
        1 * clientEntityMock.getValidityInSeconds() >> 1234
        result instanceof ClientSpring
        result.getId() == "clientId"
        result.getClientSecret() == "clientSecret"
        result.getScope() == scopes
        result.getGrants() == grants
        result.getRedirectUri() == "redirectURI"
        result.getAccessTokenValiditySeconds() == 1234
        result.isImplicit()
        result.getExpiry() == date
        result.getValidityInSeconds() == 1234
    }

    def "The updateClientExpiry method annotations should be present with appropriate configuration for RequestMapping and Response Body"() {
        given:
        Method method = AuthenticationController.class.getDeclaredMethod("updateClientExpiry", String, String)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseStatus status = method.getAnnotation(ResponseStatus)

        then:
        mapping.value() == ["/client/{id}"]
        mapping.method() == [RequestMethod.PUT]
        status.value() == HttpStatus.OK
    }

    def "Should be able to update the client expiry date"() {
        given:
        def dateAsString = new Date(System.currentTimeMillis()).toString()
        def expiryBody = "expiry=" + URLEncoder.encode(dateAsString, "UTF-8")
        def clientEntityMock = Mock(ClientEntity)

        when:
        authenticationController.updateClientExpiry("clientId", expiryBody)

        then:
        1 * clientDaoMock.getClient("clientId") >> clientEntityMock
        1 * clientDaoMock.update(clientEntityMock, "clientId")
    }
}