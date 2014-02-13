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

import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

import org.apache.log4j.Logger

import spock.lang.Specification

class SSLRequestLoggingFilterSpec extends Specification {

    def logger = Mock(Logger)
    SSLRequestLoggingFilter sslRequestLoggingFilter = new SSLRequestLoggingFilter(logger: logger)
    def servletRequestMock = Mock(ServletRequest)
    def servletResponseMock = Mock(ServletResponse)
    def filterChainMock = Mock(FilterChain)
    def filterConfigMock = Mock(FilterConfig)

    def "should log message that TLS is not enabled"() {
        given:
        servletRequestMock.getScheme() >> "http"

        when:
        sslRequestLoggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChainMock)

        then:
        1 * logger.warn("SSL/TLS should be used")
    }

    def "should not log message if TLS is enabled"() {
        given:
        servletRequestMock.getScheme() >> "https"

        when:
        sslRequestLoggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChainMock)

        then:
        0 * logger.warn("SSL/TLS should be used")
    }

    def "init method should be present"() {
        when:
        sslRequestLoggingFilter.init(filterConfigMock)

        then:
        true
    }

    def "destroy method should be present"() {
        when:
        sslRequestLoggingFilter.destroy()

        then:
        true
    }
}