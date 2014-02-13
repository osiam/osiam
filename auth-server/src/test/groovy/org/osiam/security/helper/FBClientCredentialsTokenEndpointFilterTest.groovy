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

package org.osiam.security.helper

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FBClientCredentialsTokenEndpointFilterTest extends Specification {
    def authNmanager = Mock(AuthenticationManager)

    def underTest = new FBClientCredentialsTokenEndpointFilter(authenticationManager: authNmanager)
    HttpServletRequest request = Mock(HttpServletRequest)
    HttpServletResponse response = Mock(HttpServletResponse)
    def authentication = Mock(Authentication)
    FilterChain chain = Mock(FilterChain)

    def "should set the url to /fb/oauth/access_token"() {
        when:
        def underTest = new FBClientCredentialsTokenEndpointFilter()
        then:
        underTest.filterProcessesUrl == "/fb/oauth/access_token"
    }

    def "should get the client id and secret"() {
        when:
        def result = underTest.attemptAuthentication(request, response)
        then:
        1 * request.getParameter("client_id") >> "client_id"
        1 * request.getParameter("client_secret") >> "client_secret"
        1 * authNmanager.authenticate(_) >> authentication
        result == authentication
    }

    def "should set null client_secret to empty string"() {
        when:
        def result = underTest.attemptAuthentication(request, response)
        then:
        1 * request.getParameter("client_id") >> "client_id"
        1 * request.getParameter("client_secret") >> null
        1 * authNmanager.authenticate(_) >> authentication
        result == authentication
    }

    def "should create handler"() {
        when:
        underTest.afterPropertiesSet()
        then:
        underTest.failureHandler
        underTest.successHandler

    }

    def "should return null when client_id is null"() {
        when:
        def result = underTest.attemptAuthentication(request, response)
        then:
        1 * request.getParameter("client_id") >> null
        !result
    }

    def "should call chain.doFilter on successfulAuthentication"() {
        when:
        underTest.successfulAuthentication(request, response, chain, authentication)
        then:
        1 * chain.doFilter(request, response)
    }

    def "should return false on requiresAuthentication when no client_id got submitted"() {
        when:
        def result = underTest.requiresAuthentication(request, response)
        then:
        0 * request.getRequestURI()
        1 * request.getParameter("client_id") >> null
        !result
    }

    def "should return true on requiresAuthentication when client_id got submitted"() {
        when:
        def result = underTest.requiresAuthentication(request, response)
        then:
        1 * request.getContextPath() >> ""
        1 * request.getRequestURI() >> "localhost/fb/oauth/access_token"
        1 * request.getParameter("client_id") >> "client_id"
        result
    }
}
