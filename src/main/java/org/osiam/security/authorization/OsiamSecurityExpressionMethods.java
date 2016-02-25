/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.security.authorization;

import com.google.common.base.Splitter;
import org.osiam.resources.scim.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class OsiamSecurityExpressionMethods {

    private final Authentication authentication;
    private final FilterInvocation filterInvocation;

    public OsiamSecurityExpressionMethods(Authentication authentication, FilterInvocation filterInvocation) {
        this.authentication = authentication;
        this.filterInvocation = filterInvocation;
    }

    public boolean isOwnerOfResource() {
        if (!(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        final String userId = ((User) authentication.getPrincipal()).getId();
        final String requestUrl = filterInvocation.getRequestUrl();

        int uuidIndex;
        if (requestUrl.startsWith("/token/revocation")) {
            uuidIndex = 2;
        } else if (requestUrl.startsWith("/Users")) {
            uuidIndex = 1;
        } else {
            return false;
        }

        try {
            String path = new URI(requestUrl).getPath();
            List<String> pathSegments = Splitter.on('/')
                    .omitEmptyStrings()
                    .trimResults()
                    .splitToList(path);

            if (pathSegments.size() < uuidIndex + 1) {
                return false;
            }

            String resourceId = pathSegments.get(uuidIndex);
            if (userId.equals(resourceId)) {
                return true;
            }
        } catch (URISyntaxException e) {
            return false;
        }

        return false;
    }
}
