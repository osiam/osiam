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

package org.osiam.security.authorization;

import org.osiam.security.authentication.OsiamClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * A default user approval handler that doesn't remember any decisions.
 *
 * @author Dave Syer
 */
@Component
public class OsiamUserApprovalHandler extends DefaultUserApprovalHandler {

    private static final String APPROVALS_SESSION_KEY = "osiam_oauth_approvals";
    private static final String IS_PRE_APPROVED_PARAMETER = "osiam_is_pre_approved";

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private OsiamClientDetailsService osiamClientDetailsService;

    @Override
    public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest,
            Authentication userAuthentication) {
        ClientDetails client = osiamClientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
        if (client.isAutoApprove("") || hasRememberedApprovalForClient(authorizationRequest, client)) {
            authorizationRequest.setApproved(true);
            HashMap<String, String> newApprovalParameters = new HashMap<>(authorizationRequest.getApprovalParameters());
            newApprovalParameters.put(IS_PRE_APPROVED_PARAMETER, "true");
            authorizationRequest.setApprovalParameters(Collections.unmodifiableMap(newApprovalParameters));
        }
        return authorizationRequest;
    }

    @Override
    public boolean isApproved(
            AuthorizationRequest authorizationRequest, Authentication userAuthentication
    ) {
        boolean approved = super.isApproved(authorizationRequest, userAuthentication);

        if (!approved) {
            return false;
        }

        if ("true".equals(authorizationRequest.getApprovalParameters().get(IS_PRE_APPROVED_PARAMETER))) {
            return true;
        }

        @SuppressWarnings("unchecked")
        Map<String, Long> approvals = (Map<String, Long>) httpSession.getAttribute(APPROVALS_SESSION_KEY);
        if (approvals == null) {
            approvals = new ConcurrentHashMap<>();
            httpSession.setAttribute(APPROVALS_SESSION_KEY, approvals);
        }

        if (!approvals.containsKey(authorizationRequest.getClientId())) {
            approvals.put(authorizationRequest.getClientId(), System.currentTimeMillis());
        }

        return true;
    }

    private boolean hasRememberedApprovalForClient(AuthorizationRequest authorizationRequest, ClientDetails client) {
        @SuppressWarnings("unchecked")
        Map<String, Long> approvals = (Map<String, Long>) httpSession.getAttribute(APPROVALS_SESSION_KEY);

        if (approvals == null) {
            return false;
        }

        final Long approvalTime = approvals.get(authorizationRequest.getClientId());

        if (approvalTime == null) {
            return false;
        }

        final long validityInSeconds = (Long) client.getAdditionalInformation().get("validityInSeconds");

        if (System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(validityInSeconds) > approvalTime) {
            approvals.remove(authorizationRequest.getClientId());
            return false;
        }

        return true;
    }
}
