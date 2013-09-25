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

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This AccessDecisionVoter is based upon an other AccessDecisionVoter given in the constructor.
 * <p/>
 * The only purpose of this class is to enhance attributes with a SCOPE_HTTP-Method in vote when SCOPE_DYNAMIC is set.
 *
 * @author phil
 */
public class DynamicHTTPMethodScopeEnhancer implements AccessDecisionVoter<Object> {

    private final ConfigAttribute dynamic = new SecurityConfig("SCOPE_DYNAMIC");
    private final AccessDecisionVoter<Object> basedOnVoter;

    public DynamicHTTPMethodScopeEnhancer(final AccessDecisionVoter<Object> basedOnVoter) {
        this.basedOnVoter = basedOnVoter;
    }


    @Override
    public boolean supports(final ConfigAttribute attribute) {
        return basedOnVoter.supports(attribute);
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return basedOnVoter.supports(clazz);
    }

    @Override
    public int vote(final Authentication authentication, final Object object, final Collection<ConfigAttribute> attributes) {
        Set<ConfigAttribute> dynamicConfigs = ifScopeDynamicAddMethodScope(object, attributes);
        return basedOnVoter.vote(authentication, object, dynamicConfigs);
    }

    private Set<ConfigAttribute> ifScopeDynamicAddMethodScope(final Object object, final Collection<ConfigAttribute> attributes) {
        Set<ConfigAttribute> dynamicConfigs = new HashSet<>(attributes);
        if (object instanceof FilterInvocation && dynamicConfigs.remove(dynamic)) {
            addMethodScope(object, dynamicConfigs);
        }
        return dynamicConfigs;
    }

    private void addMethodScope(final Object object, final Set<ConfigAttribute> dynamicConfigs) {
        final FilterInvocation f = (FilterInvocation) object;
        dynamicConfigs.add(new SecurityConfig("SCOPE_" + f.getRequest().getMethod().toUpperCase(Locale.ENGLISH)));
    }
}

