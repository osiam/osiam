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

package org.osiam.security.authorization

import org.osiam.auth.oauth_client.ClientEntity
import org.osiam.security.authentication.OsiamClientDetailsService
import org.osiam.util.TestAuthentication
import org.osiam.util.TestHttpSession
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.AuthorizationRequest
import spock.lang.Specification

import javax.servlet.http.HttpSession

class OsiamUserApprovalHandlerSpec extends Specification {

    private static final String APPROVALS_SESSION_KEY = 'osiam_oauth_approvals'
    private static final String IS_PRE_APPROVED_PARAMETER = 'osiam_is_pre_approved'

    HttpSession httpSession = new TestHttpSession()
    OsiamClientDetailsService osiamClientDetailsService = Mock()
    OsiamUserApprovalHandler osiamUserApprovalHandler = new OsiamUserApprovalHandler(
            osiamClientDetailsService: osiamClientDetailsService, httpSession: httpSession
    )
    Authentication authentication = new TestAuthentication(true)

    def 'The client is approved if the user has approved the client'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setClientId('test-client')
        authRequest.setApproved(true)

        expect:
        osiamUserApprovalHandler.isApproved(authRequest, authentication)
    }

    def 'The client is not approved if the user has denied the client'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setApproved(false)

        expect:
        !osiamUserApprovalHandler.isApproved(authRequest, authentication)
    }

    def 'The session will not be changed if the user did not approve the client'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setApproved(false)

        when:
        osiamUserApprovalHandler.isApproved(authRequest, authentication)

        then:
        httpSession.getAttribute(APPROVALS_SESSION_KEY) == null
    }

    def 'The session will not be changed if the user is pre-approved'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setClientId('test-client')
        authRequest.setApproved(true)
        authRequest.setApprovalParameters([(IS_PRE_APPROVED_PARAMETER): 'true'])

        when:
        osiamUserApprovalHandler.isApproved(authRequest, authentication)

        then:
        httpSession.getAttribute(APPROVALS_SESSION_KEY) == null
    }

    def 'The session will contain the approval if the user has approved the client'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setApproved(true)
        authRequest.setClientId('test-client')

        when:
        osiamUserApprovalHandler.isApproved(authRequest, authentication)

        then:
        Map approvals = httpSession.getAttribute(APPROVALS_SESSION_KEY)
        approvals != null
        approvals.containsKey('test-client')
        approvals.get('test-client') != null
    }

    def 'The approval data in the session will not be updated on consecutive approvals'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setApproved(true)
        authRequest.setClientId('test-client')
        long currentTime = System.currentTimeMillis()
        def existingApprovals = ['test-client': currentTime]
        httpSession.setAttribute(APPROVALS_SESSION_KEY, existingApprovals)

        when:
        osiamUserApprovalHandler.isApproved(authRequest, authentication)

        then:
        Map approvals = httpSession.getAttribute(APPROVALS_SESSION_KEY)
        approvals.is(existingApprovals)
        approvals.get('test-client') == currentTime
    }

    def 'The client is pre-approved if approval is implicit'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest(clientId: 'test-client')

        when:
        authRequest = osiamUserApprovalHandler.checkForPreApproval(authRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new ClientEntity(implicit: true)
        authRequest.getApprovalParameters().get(IS_PRE_APPROVED_PARAMETER) == 'true'
        authRequest.isApproved()
    }

    def 'The client is pre-approved if there is a non-expired approval stored in the session'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setClientId('test-client')
        httpSession.setAttribute(APPROVALS_SESSION_KEY, ['test-client': System.currentTimeMillis()])

        when:
        authRequest = osiamUserApprovalHandler.checkForPreApproval(authRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new ClientEntity(validityInSeconds: 10)
        authRequest.getApprovalParameters().get(IS_PRE_APPROVED_PARAMETER) == 'true'
        authRequest.isApproved()
    }

    def 'The client is not pre-approved if there is no approval data in their session'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setClientId('test-client')

        when:
        authRequest = osiamUserApprovalHandler.checkForPreApproval(authRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new ClientEntity()
        !authRequest.getApprovalParameters().containsKey(IS_PRE_APPROVED_PARAMETER)
        !authRequest.isApproved()
    }

    def 'The client is not pre-approved if there is no approval data for the client in their session'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setClientId('test-client')
        httpSession.setAttribute(APPROVALS_SESSION_KEY, ['other-client': System.currentTimeMillis()])

        when:
        authRequest = osiamUserApprovalHandler.checkForPreApproval(authRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new ClientEntity()
        !authRequest.getApprovalParameters().containsKey(IS_PRE_APPROVED_PARAMETER)
        !authRequest.isApproved()
    }

    def 'The client is not pre-approved if the approval data for the client in their session is expired'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setClientId('test-client')
        httpSession.setAttribute(APPROVALS_SESSION_KEY, ['test-client': 0L])

        when:
        authRequest = osiamUserApprovalHandler.checkForPreApproval(authRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new ClientEntity(validityInSeconds: 10)
        !authRequest.getApprovalParameters().containsKey(IS_PRE_APPROVED_PARAMETER)
        !authRequest.isApproved()
    }

    def 'The approval data for the client in the user session is removed if it is expired'() {
        given:
        AuthorizationRequest authRequest = new AuthorizationRequest()
        authRequest.setClientId('test-client')
        httpSession.setAttribute(APPROVALS_SESSION_KEY, ['test-client': 0L])

        when:
        osiamUserApprovalHandler.checkForPreApproval(authRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new ClientEntity(validityInSeconds: 10)
        httpSession.getAttribute(APPROVALS_SESSION_KEY).isEmpty()
    }
}
