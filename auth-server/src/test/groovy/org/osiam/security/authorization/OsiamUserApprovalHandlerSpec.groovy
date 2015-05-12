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

import org.osiam.security.authentication.OsiamClientDetails
import org.osiam.security.authentication.OsiamClientDetailsService
import org.osiam.util.TestAuthentication
import org.osiam.util.TestHttpSession
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest
import spock.lang.Specification

import javax.servlet.http.HttpSession

class OsiamUserApprovalHandlerSpec extends Specification {

    HttpSession httpSession = new TestHttpSession()
    OsiamClientDetailsService osiamClientDetailsService = Mock()
    OsiamUserApprovalHandler osiamUserApprovalHandler =
            new OsiamUserApprovalHandler(osiamClientDetailsService: osiamClientDetailsService, httpSession: httpSession)

    def 'The session is empty when the user is not authenticated'() {
        given:
        Authentication authentication = new TestAuthentication(false)
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:])

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequest, authentication)

        then:
        httpSession.getAttribute('approvals') == null
    }

    def 'The session is empty when the user is authenticated but did not approve'() {
        given:
        Authentication authentication = new TestAuthentication(true)
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:])

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequest, authentication)

        then:
        httpSession.getAttribute('approvals') == null
    }

    def 'The session is not empty when the user is authenticated and the approvalParameter is set'() {
        given:
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:], [user_oauth_approval: 'true'],
                'test-client', null)
        Authentication authentication = new TestAuthentication(true)

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequest, authentication)
        Map approvals = httpSession.getAttribute('approvals')

        then:
        approvals != null
        approvals.containsKey('test-client')
        approvals.get('test-client') != null
    }

    def 'The client is not approved if the user is not authenticated'() {
        given:
        Authentication authentication = new TestAuthentication(false)

        expect:
        osiamUserApprovalHandler.isApproved(null, authentication) == false
    }

    def 'The client is approved if the user is authenticated and has already authorized the client'() {
        given:
        Authentication authentication = new TestAuthentication(true)
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:], [user_oauth_approval: 'true'],
                null, null)

        expect:
        osiamUserApprovalHandler.isApproved(authorizationRequest, authentication) == true
    }

    def 'The client is approved if approval is implicit'() {
        given:
        Authentication authentication = new TestAuthentication(true)
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:], [:], 'test-client', null)

        when:
        boolean approved = osiamUserApprovalHandler.isApproved(authorizationRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new OsiamClientDetails(implicit: true)
        approved == true
    }

    def 'The client is not approved if the session is empty'() {
        given:
        Authentication authentication = new TestAuthentication(true)
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:], [:], 'test-client', null)

        when:
        boolean approved = osiamUserApprovalHandler.isApproved(authorizationRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new OsiamClientDetails()
        approved == false
    }

    def 'The client is not approved if the session has no approval time for the client'() {
        given:
        Authentication authentication = new TestAuthentication(true)
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:], [:], 'test-client', null)
        httpSession.setAttribute('approvals', [someOtherClient: 1L])

        when:
        boolean approved = osiamUserApprovalHandler.isApproved(authorizationRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >> new OsiamClientDetails()
        approved == false
    }

    def 'The client is not approved and the expired approval is deleted from the session if the approval is expired'() {
        given:
        Authentication authentication = new TestAuthentication(true)
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:], [:], 'test-client', null)
        httpSession.setAttribute('approvals', ['test-client': 1L])

        when:
        boolean approved = osiamUserApprovalHandler.isApproved(authorizationRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >>
                new OsiamClientDetails(validityInSeconds: 1)
        httpSession.getAttribute('approvals').isEmpty()
        approved == false
    }

    def 'The client is approved if there is a non-expired approval stored in the session'() {
        given:
        Authentication authentication = new TestAuthentication(true)
        AuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest([:], [:], 'test-client', null)
        httpSession.setAttribute('approvals', ['test-client': System.currentTimeMillis()])

        when:
        boolean approved = osiamUserApprovalHandler.isApproved(authorizationRequest, authentication)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('test-client') >>
                new OsiamClientDetails(validityInSeconds: 10)
        approved == true
    }
}
