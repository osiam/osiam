/*
 * Copyright (C) 2015 tarent AG
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

package org.osiam.security.authorization;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.osiam.security.authentication.OsiamClientDetails;
import org.osiam.security.authentication.OsiamClientDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;

@Named("userApprovalHandler")
public class OsiamUserApprovalHandler extends DefaultUserApprovalHandler {

    @Inject
    private HttpSession httpSession;

    @Inject
    private OsiamClientDetailsService osiamClientDetailsService;

    @Override
    public AuthorizationRequest updateBeforeApproval(final AuthorizationRequest authorizationRequest,
            final Authentication userAuthentication) {

        if (super.isApproved(authorizationRequest, userAuthentication)) {

            @SuppressWarnings("unchecked")
            Map<String, Long> approvals = (Map<String, Long>) httpSession.getAttribute("approvals");
            if (approvals == null) {
                approvals = new HashMap<>();
                httpSession.setAttribute("approvals", approvals);
            }
            approvals.put(authorizationRequest.getClientId(), System.currentTimeMillis());
        }
        return authorizationRequest;
    }

    @Override
    public boolean isApproved(final AuthorizationRequest authorizationRequest, final Authentication userAuthentication) {

        if (!userAuthentication.isAuthenticated()) {
            return false;
        }

        if (super.isApproved(authorizationRequest, userAuthentication)) {
            return true;
        }

        final OsiamClientDetails client = osiamClientDetailsService.loadClientByClientId(authorizationRequest
                .getClientId());

        if (client.isImplicit()) {
            return true;
        }

        @SuppressWarnings("unchecked")
        Map<String, Long> approvals = (Map<String, Long>) httpSession.getAttribute("approvals");
        if (approvals == null) {
            return false;
        }

        final Long approvalTime = approvals.get(authorizationRequest.getClientId());

        if (approvalTime == null) {
            return false;
        }

        final long validityInSeconds = client.getValidityInSeconds();

        if (System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(validityInSeconds) > approvalTime) {
            approvals.remove(authorizationRequest.getClientId());
            return false;
        }

        return true;
    }
}
