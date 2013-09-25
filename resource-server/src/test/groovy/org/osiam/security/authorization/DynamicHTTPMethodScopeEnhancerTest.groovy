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

import org.osiam.security.authorization.DynamicHTTPMethodScopeEnhancer
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.SecurityConfig
import org.springframework.security.oauth2.provider.vote.ScopeVoter
import org.springframework.security.web.FilterInvocation
import spock.lang.Specification


import javax.servlet.http.HttpServletRequest

class DynamicHTTPMethodScopeEnhancerTest extends Specification {
    def scope_haha = new SecurityConfig("haha")
    def scope_dynamic = new SecurityConfig("SCOPE_DYNAMIC");
    def basedOnVoter = Mock(ScopeVoter)
    def underTest = new DynamicHTTPMethodScopeEnhancer(basedOnVoter)
    def filter = Mock(FilterInvocation)

    def setup(){
        filter.getRequest() >> Mock(HttpServletRequest)
        filter.getRequest().getMethod() >> "get"

    }

    def "should just delegate support because we don't need to enhance something there"() {
        given:
        def basedOnVoter = Mock(ScopeVoter)
        def underTest = new DynamicHTTPMethodScopeEnhancer(basedOnVoter)
        when:
        underTest.supports(String)
        underTest.supports(scope_haha)

        then:
        1 * basedOnVoter.supports(String)
        1 * basedOnVoter.supports(scope_haha)
    }

    def "should remove SCOPE_DYNAMIC and add SCOPE_HTTP-METHOD in attributes when scope is dynamic"() {
        given:
        def attributes = [scope_dynamic, scope_haha]

        when:
        underTest.vote(null, filter, attributes)
        then:
        _ * basedOnVoter.vote(_, filter, { Collection<ConfigAttribute> a ->
            assert a.contains(scope_haha)
            assert a.contains(new SecurityConfig("SCOPE_GET"))
            assert !a.contains(scope_dynamic)
        })
    }

    def "should not touch sent attributes when scope is not dynamic"() {
        given:
        def attributes = [scope_haha]

        when:
        underTest.vote(null, filter, attributes)
        then:
        _ * basedOnVoter.vote(_, filter, { Collection<ConfigAttribute> a ->
            assert a.contains(scope_haha)
        })

    }

    def "should not touch sent attributes when object is no filter"() {
        given:
        def attributes = [scope_haha, scope_dynamic]

        when:
        underTest.vote(null, new Object(), attributes)
        then:
        _ * basedOnVoter.vote(_, filter, { Collection<ConfigAttribute> a ->
            assert a.contains(scope_haha)
            assert a.contains(scope_dynamic)
        })

    }
}
