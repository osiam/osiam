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

package org.osiam.resources.helper

import javax.servlet.http.HttpServletRequest

import org.osiam.storage.dao.ResourceDao

import spock.lang.Ignore
import spock.lang.Specification


class RequestParamHelperSpec extends Specification {

    def servletRequestMock = Mock(HttpServletRequest)
    def requestParamHelper = new RequestParamHelper()

    def "should not use default value for sortBy if not null"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> "someSortOrder"
        servletRequestMock.getParameter("startIndex") >> "1"
        servletRequestMock.getParameter("count") >> "10"
        servletRequestMock.getParameter("attributes") >> ["userName"]

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("sortBy") == "userName"
    }

    def "should exist default value if sortOrder is null"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> null
        servletRequestMock.getParameter("startIndex") >> "1"
        servletRequestMock.getParameter("count") >> "10"
        servletRequestMock.getParameter("attributes") >> ["userName"]

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("sortOrder") == "ascending"
    }

    def "should not use default value for sortOrder if not null"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> "descending"
        servletRequestMock.getParameter("startIndex") >> "1"
        servletRequestMock.getParameter("count") >> "10"
        servletRequestMock.getParameter("attributes") >> ["userName"]

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("sortOrder") == "descending"
    }

    def "should exist default value if startIndex is null"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> "descending"
        servletRequestMock.getParameter("startIndex") >> null
        servletRequestMock.getParameter("count") >> "10"
        servletRequestMock.getParameter("attributes") >> ["userName"]

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("startIndex") == 1
    }

    def "should not use default value for startIndex if not null"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> "descending"
        servletRequestMock.getParameter("startIndex") >> "11"
        servletRequestMock.getParameter("count") >> "10"
        servletRequestMock.getParameter("attributes") >> ["userName"]

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("startIndex") == 11
    }

    def "should exist default value if count is null"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> "descending"
        servletRequestMock.getParameter("startIndex") >> "11"
        servletRequestMock.getParameter("count") >> null
        servletRequestMock.getParameter("attributes") >> ["userName"]

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("count") == 100
    }

    def "should not use default value for count if not null"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> "descending"
        servletRequestMock.getParameter("startIndex") >> "11"
        servletRequestMock.getParameter("count") >> "10"
        servletRequestMock.getParameter("attributes") >> ["userName"]

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("count") == 10
    }

    def "should throw exception if count values are negative"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> "descending"
        servletRequestMock.getParameter("startIndex") >> "11"
        servletRequestMock.getParameter("count") >> "-10"
        servletRequestMock.getParameter("attributes") >> ["userName"]

        when:
        requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        def e = thrown(RuntimeException)
        e.getMessage() == "Negative count values are not allowed"
    }

    def "should exist default value if attributes are null"() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> "userName"
        servletRequestMock.getParameter("sortOrder") >> "descending"
        servletRequestMock.getParameter("startIndex") >> "11"
        servletRequestMock.getParameter("count") >> "10"
        servletRequestMock.getParameter("attributes") >> null

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("attributes") == []
    }

    def "should split parameter by , and ."() {
        given:
        servletRequestMock.getParameter("filter") >> "someFilter"
        servletRequestMock.getParameter("sortBy") >> null
        servletRequestMock.getParameter("sortOrder") >> "someSortOrder"
        servletRequestMock.getParameter("startIndex") >> "1"
        servletRequestMock.getParameter("count") >> "10"
        servletRequestMock.getParameter("attributes") >> "userName,title,meta.created"

        when:
        Map result = requestParamHelper.getRequestParameterValues(servletRequestMock)

        then:
        result.get("attributes") == [
            "userName",
            "title",
            "meta",
            "created"
        ]
    }
}
