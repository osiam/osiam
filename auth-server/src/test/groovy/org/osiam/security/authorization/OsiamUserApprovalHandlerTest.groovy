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

package org.osiam.security.authorization

import org.osiam.resources.ClientSpring
import org.osiam.security.authentication.ClientDetailsLoadingBean
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.AuthorizationRequest
import spock.lang.Specification

class OsiamUserApprovalHandlerTest extends Specification {

    def clientDetailsLoadingBean = Mock(ClientDetailsLoadingBean)
    def osiamUserApprovalHandler = new OsiamUserApprovalHandler(clientDetailsLoadingBean: clientDetailsLoadingBean)
    def authorizationRequestMock = Mock(AuthorizationRequest)
    def authenticationMock = Mock(Authentication)

    def "should only add approval date if it is the correct state and user approved successfully"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId("example-client") >> clientMock
        def approvalParams = ['user_oauth_approval':'true']
        authorizationRequestMock.getApprovalParameters() >> approvalParams
        clientMock.getValidityInSeconds() >> 1337

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)

        then:
        clientMock.getExpiry() <= new Date(System.currentTimeMillis() + (1337 * 1000))
        1 * clientDetailsLoadingBean.updateClient(clientMock, 'example-client')
        true
    }

    def "should not add approval date if approval param map is empty and not containing key user_oauth_approval"() {
        given:
        def approvalParams = [:]
        authorizationRequestMock.getApprovalParameters() >> approvalParams

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)

        then:
        0 * clientDetailsLoadingBean.updateClient(_, _)
        true
    }

    def "should not add approval date if user denies approval"() {
        given:
        def approvalParams = ['user_oauth_approval':'false']
        authorizationRequestMock.getApprovalParameters() >> approvalParams

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)

        then:
        0 * 0 * clientDetailsLoadingBean.updateClient(_, _)
        true
    }

    def "should return true if implicit is configured as true to not ask user for approval"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId('example-client') >> clientMock
        clientMock.isImplicit() >> true

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        result
    }

    def "should return true if expiry date is valid and user approved client already and must not approve it again"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId('example-client') >> clientMock
        clientMock.getExpiry() >> new Date(System.currentTimeMillis() + (1337 * 1000))

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        result
    }

    def "should return false if implicit is not configured and user never approved the client before"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId('example-client') >> clientMock
        clientMock.isImplicit() >> false
        clientMock.getExpiry() >> null

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        !result
    }

    def "should return false if implicit is not configured and user approval date is expired"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId('example-client') >> clientMock
        clientMock.isImplicit() >> false
        clientMock.getExpiry() >> new Date(System.currentTimeMillis() - 100000)

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        !result
    }
}