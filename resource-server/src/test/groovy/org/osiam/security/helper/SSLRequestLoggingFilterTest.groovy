package org.osiam.security.helper

import org.apache.log4j.Logger
import org.osiam.security.helper.SSLRequestLoggingFilter
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 25.06.13
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
class SSLRequestLoggingFilterTest extends Specification {

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