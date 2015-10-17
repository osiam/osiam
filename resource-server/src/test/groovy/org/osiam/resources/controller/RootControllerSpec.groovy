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

package org.osiam.resources.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import spock.lang.Specification

import java.lang.reflect.Method

class RootControllerSpec extends Specification {

    def underTest = new RootController()

    def "should throw Unsupported exception on / URI with GET method"() {
        given:
        Method method = RootController.class.getDeclaredMethod("searchWithGet")

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        underTest.searchWithGet()

        then:
        mapping.value() == []
        mapping.method() == [RequestMethod.GET]
        body
        def e = thrown(UnsupportedOperationException)
        e.getMessage() == "We do not support search on the root endpoint. If you have an use case " +
                "that requires this search implementation please file a bug report with our bugtracker " +
                "(see https://github.com/osiam/osiam/blob/master/README.md for details on the bug tracker)."
    }

    def "should throw Unsupported exception on /.search URI with POST method"() {
        given:
        Method method = RootController.class.getDeclaredMethod("searchWithPost")

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        underTest.searchWithPost()

        then:
        mapping.value() == [".search"]
        mapping.method() == [RequestMethod.POST]
        body
        def e = thrown(UnsupportedOperationException)
        e.getMessage() == "We do not support search on the root endpoint. If you have an use case " +
                "that requires this search implementation please file a bug report with our bugtracker " +
                "(see https://github.com/osiam/osiam/blob/master/README.md for details on the bug tracker)."
    }
}