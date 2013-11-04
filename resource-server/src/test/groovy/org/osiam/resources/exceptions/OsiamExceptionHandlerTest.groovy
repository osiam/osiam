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

package org.osiam.resources.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import org.osiam.resources.scim.User
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.ImEntity
import org.osiam.storage.entities.PhoneNumberEntity
import org.osiam.storage.entities.PhotoEntity
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.WebRequest
import spock.lang.Specification

class OsiamExceptionHandlerTest extends Specification {
    def underTest = new OsiamExceptionHandler()
    WebRequest request = Mock(WebRequest)

    def "exception result should contain a code and a description"() {
        when:
        def errorResult = new OsiamExceptionHandler.JsonErrorResult("hacja", "unso")
        then:
        errorResult.error_code == "hacja"
        errorResult.description == "unso"
    }

    def "should generate a response entity"() {
        when:
        def result = underTest.handleConflict(new NullPointerException("Dunno"), request)
        then:
        result.getStatusCode() == HttpStatus.CONFLICT
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).error_code == HttpStatus.CONFLICT.name()
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == "Dunno"
    }

    def "should set status to ResourceNotFound when org.osiam.resources.exceptions.ResourceNotFoundException occurs"() {
        when:
        def result = underTest.handleConflict(new ResourceNotFoundException("Dunno"), request)
        then:
        result.getStatusCode() == HttpStatus.NOT_FOUND
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).error_code == HttpStatus.NOT_FOUND.name()
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == "Dunno"
    }

    def "should set status to NOT_IMPLEMENTED when java.lang.UnsupportedOperationException occurs"() {
        when:
        def result = underTest.handleConflict(new UnsupportedOperationException("Dunno"), request)
        then:
        result.getStatusCode() == HttpStatus.NOT_IMPLEMENTED
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).error_code == HttpStatus.NOT_IMPLEMENTED.name()
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == "Dunno"
    }

    def "should set status to I_AM_A_TEAPOT when org.osiam.resources.exceptions.SchemaUnknownException occurs"() {
        when:
        def result = underTest.handleConflict(new SchemaUnknownException(), request)
        then:
        result.getStatusCode() == HttpStatus.I_AM_A_TEAPOT
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).error_code == HttpStatus.I_AM_A_TEAPOT.name()
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == "Delivered schema is unknown."
    }

    def "should transform *Entity No enum constant error message to a more readable error response"() {
        when:
        def result = underTest.handleConflict(e, request)
        then:
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description ==
                "huch is not a valid " + name + " are allowed."
        where:
        name << ["PhoneNumber type only work, home, mobile, fax, pager, other",
                "Im type only aim, gtalk, icq, xmpp, msn, skype, qq, yahoo",
                "Email type only work, home, other",
                "Photo type only photo, thumbnail"]

        e << [get_exception { new PhoneNumberEntity().setType("huch") },
                get_exception { new ImEntity().setType("huch") },
                get_exception { new EmailEntity().setType("huch") },
                get_exception { new PhotoEntity().setType("huch") }]
    }

    def get_exception(Closure c) {
        try {
            c.call()
        } catch (IllegalArgumentException a) {
            return a
        }
    }

    def "should transform json property invalid error message to a more readable response"() {
        given:
        def e = generate_wrong_json_exception('{"extId":"blubb"}', User)
        when:
        def result = underTest.handleConflict(e, request)
        then:
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == 'Unrecognized field "extId"'
    }

    def generate_wrong_json_exception(String input, Class clazz) {

        try {
            new ObjectMapper().readValue(input, clazz);
        } catch (Exception e) {
            return e;
        }

    }

    def "should transform json mapping error for List to a more readable response"() {
        given:
        def e = generate_wrong_json_exception('{"ims":"blaa"}', User)
        when:
        def result = underTest.handleConflict(e, request)
        then:
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == 'Can not deserialize instance of java.util.ArrayList out of VALUE_STRING'
    }
}
