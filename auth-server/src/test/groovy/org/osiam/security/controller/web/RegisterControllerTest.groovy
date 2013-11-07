package org.osiam.security.controller.web

import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse

/**
 * CHANGE THIS TEXT TO SOMETHING USEFUL, DESCRIBING THE CLASS.
 * User: Jochen Todea
 * Date: 07.11.13
 * Time: 14:08
 * Created: with Intellij IDEA
 */
class RegisterControllerTest extends Specification {

    def context = Mock(ServletContext)
    def registerController = new RegisterController(context: context)

    def "The registration controller should return a HTML file as stream"() {
        given:
        def httpServletResponseMock = Mock(HttpServletResponse)
        def inputStreamMock = Mock(InputStream)
        def outputStreamMock = Mock(ServletOutputStream)

        when:
        registerController.index("Bearer ACCESS_TOKEN", httpServletResponseMock)

        then:
        1 * httpServletResponseMock.setContentType("text/html")
        1 * context.getResourceAsStream("/WEB-INF/registration/registration.html") >> inputStreamMock
        1 * httpServletResponseMock.getOutputStream() >> outputStreamMock
    }
}
