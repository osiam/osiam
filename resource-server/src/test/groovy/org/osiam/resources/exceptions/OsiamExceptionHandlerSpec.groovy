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
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class OsiamExceptionHandlerSpec extends Specification {
    def exceptionHandler = new OsiamExceptionHandler()
    def static IRRELEVANT = 'irrelevant'
    WebRequest request = Mock()

    def 'generated JsonErrorResult contains provided code and description'() {
        when:
        def errorResult = new OsiamExceptionHandler.JsonErrorResult('code', 'description')
        then:
        errorResult.error_code == 'code'
        errorResult.description == 'description'
    }

    def 'generating a response entity works'() {
        when:
        def result = exceptionHandler.handleConflict(new NullPointerException(IRRELEVANT), request)
        then:
        result.getStatusCode() == HttpStatus.CONFLICT
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).error_code == HttpStatus.CONFLICT.name()
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == IRRELEVANT
    }

    def 'status is set to ResourceNotFound when ResourceNotFoundException occurs'() {
        when:
        def result = exceptionHandler.handleConflict(new ResourceNotFoundException(IRRELEVANT), request)
        then:
        result.getStatusCode() == HttpStatus.NOT_FOUND
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).error_code == HttpStatus.NOT_FOUND.name()
    }

    def "status is set to NOT_IMPLEMENTED when java.lang.UnsupportedOperationException occurs"() {
        when:
        def result = exceptionHandler.handleConflict(new UnsupportedOperationException(IRRELEVANT), request)
        then:
        result.getStatusCode() == HttpStatus.NOT_IMPLEMENTED
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).error_code == HttpStatus.NOT_IMPLEMENTED.name()
    }

    def "status is set to I_AM_A_TEAPOT when org.osiam.resources.exceptions.SchemaUnknownException occurs"() {
        when:
        def result = exceptionHandler.handleConflict(new SchemaUnknownException(), request)
        then:
        result.getStatusCode() == HttpStatus.I_AM_A_TEAPOT
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).error_code == HttpStatus.I_AM_A_TEAPOT.name()
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
        def result = exceptionHandler.handleConflict(e, request)
        then:
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == 'Unrecognized field "extId"'
    }

    def generate_wrong_json_exception(String input, Class clazz) {

        try {
            new ObjectMapper().readValue(input, clazz)
        } catch (Exception e) {
            return e
        }

    }

    def "should transform json mapping error for List to a more readable response"() {
        given:
        def e = generate_wrong_json_exception('{"ims":"blaa"}', User)
        when:
        def result = exceptionHandler.handleConflict(e, request)
        then:
        (result.getBody() as OsiamExceptionHandler.JsonErrorResult).description == 'Can not deserialize instance of java.util.ArrayList out of VALUE_STRING'
    }
}
