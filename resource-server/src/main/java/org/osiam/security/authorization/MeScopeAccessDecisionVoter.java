package org.osiam.security.authorization;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osiam.resources.scim.User;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;
import org.springframework.security.web.FilterInvocation;

import com.google.common.base.Splitter;

public class MeScopeAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {

    public static final SecurityConfig SCOPE_DYNAMIC = new SecurityConfig("SCOPE_DYNAMIC");
    public static final SecurityConfig SCOPE_ME = new SecurityConfig("SCOPE_ME");

    private final ScopeVoter scopeVoter;

    public MeScopeAccessDecisionVoter(ScopeVoter scopeVoter) {
        this.scopeVoter = scopeVoter;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute.equals(SCOPE_DYNAMIC);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation filterInvocation,
            Collection<ConfigAttribute> attributes) {

        String requestUrl = filterInvocation.getRequestUrl();
        String requestMethod = filterInvocation.getHttpRequest().getMethod();

        if (!canVoteOn(authentication, requestUrl, requestMethod)) {
            return ACCESS_ABSTAIN;
        }

        User user = (User) authentication.getPrincipal();

        if (!isOwnerOfResource(user.getId(), requestUrl)) {
            return ACCESS_ABSTAIN;
        }

        return scopeVoter.vote(authentication, filterInvocation, enhanceConfigAttributes(attributes));
    }

    private Set<ConfigAttribute> enhanceConfigAttributes(Collection<ConfigAttribute> attributes) {
        Set<ConfigAttribute> enhancedAttributes = new HashSet<>(attributes);
        if (enhancedAttributes.contains(SCOPE_DYNAMIC)) {
            enhancedAttributes.remove(SCOPE_DYNAMIC);
            enhancedAttributes.add(SCOPE_ME);
        }
        return enhancedAttributes;
    }

    private boolean canVoteOn(Authentication authentication, String url, String method) {
        if (!(authentication instanceof OAuth2Authentication)) {
            return false;
        }

        OAuth2Authentication oAuth2Authentication = ((OAuth2Authentication) authentication);

        if (!(oAuth2Authentication.getPrincipal() instanceof User)) {
            return false;
        }

        if (!url.startsWith("/Users") && !url.startsWith("/me")) {
            return false;
        }

        if (url.startsWith("/Users") && method.equalsIgnoreCase("post")) {
            return false;
        }

        return true;
    }

    private boolean isOwnerOfResource(String userId, String url) {
        if (url.startsWith("/Users")) {
            try {
                String path = new URI(url).getPath();
                List<String> pathSegments = Splitter.on('/')
                        .omitEmptyStrings()
                        .trimResults()
                        .splitToList(path);

                if (pathSegments.size() < 2) {
                    return false;
                }

                String resourceId = pathSegments.get(1);
                if (userId.equals(resourceId)) {
                    return true;
                }
            } catch (URISyntaxException e) {
                return false;
            }

            return false;
        } else if (url.startsWith("/me")) {
            return true;
        }

        return false;
    }
}
