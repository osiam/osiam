package org.osiam.resources.controller

import org.osiam.resources.controller.RootController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 07.05.13
 * Time: 09:48
 * To change this template use File | Settings | File Templates.
 */
class RootControllerSpec extends Specification{

    def underTest = new RootController()

    def "should throw Unsupported exception on / URI with GET method" () {
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

    def "should throw Unsupported exception on /.search URI with POST method" () {
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