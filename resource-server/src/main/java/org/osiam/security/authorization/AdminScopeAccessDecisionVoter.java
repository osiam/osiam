package org.osiam.security.authorization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;
import org.springframework.security.web.FilterInvocation;

public class AdminScopeAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {

    public static final SecurityConfig SCOPE_DYNAMIC = new SecurityConfig("SCOPE_DYNAMIC");
    public static final SecurityConfig SCOPE_ADMIN = new SecurityConfig("SCOPE_ADMIN");

    private final ScopeVoter scopeVoter;

    public AdminScopeAccessDecisionVoter(ScopeVoter scopeVoter) {
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

        if (!(authentication instanceof OAuth2Authentication)) {
            return ACCESS_ABSTAIN;
        }

        Set<ConfigAttribute> enhancedAttributes = new HashSet<>(attributes);
        if (enhancedAttributes.contains(SCOPE_DYNAMIC)) {
            enhancedAttributes.remove(SCOPE_DYNAMIC);
            enhancedAttributes.add(SCOPE_ADMIN);
        }

        return scopeVoter.vote(authentication, filterInvocation, enhancedAttributes);
    }

}
