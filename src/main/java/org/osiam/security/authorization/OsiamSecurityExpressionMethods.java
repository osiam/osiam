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
