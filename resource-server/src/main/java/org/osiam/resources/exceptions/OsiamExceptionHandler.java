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

package org.osiam.resources.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@ControllerAdvice
public class OsiamExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(OsiamExceptionHandler.class.getName());

    /**
     * Contains all known ErrorMessageTransformer to validate and manipulate error messages
     */
    private static final ErrorMessageTransformer[] KNOWN_ERROR_MSG_TRANSFORMER = {
            new TypeErrorMessageTransformer(),
            new JsonPropertyMessageTransformer(),
            new JsonMappingMessageTransformer() };

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
        LOGGER.log(Level.WARNING, "An exception occurred", ex);
        HttpStatus status = setStatus(ex);
        JsonErrorResult error = new JsonErrorResult(status.name(), constructMessage(ex.getMessage()));
        return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
    }

    /**
     * This method may transform error messages to a more human readable message if the message contains some known
     * buzzwords to trigger the effect.
     *
     * @param message
     *            the message to check and transform
     * @return the original message or a transformed message
     */
    private String constructMessage(String message) {
        String result = message;
        for (ErrorMessageTransformer et : KNOWN_ERROR_MSG_TRANSFORMER) {
            result = et.transform(result);
        }
        return result;
    }

    private HttpStatus setStatus(Exception ex) {
        if (ex instanceof ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof SchemaUnknownException) {
            return HttpStatus.I_AM_A_TEAPOT;
        }
        if (ex instanceof UnsupportedOperationException) {
            return HttpStatus.NOT_IMPLEMENTED;
        }

        return HttpStatus.CONFLICT;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    static class JsonErrorResult {
        private String error_code;
        private String description;

        public JsonErrorResult(String name, String message) {
            error_code = name;
            description = message;
        }

        public String getError_code() {
            return error_code;
        }

        public String getDescription() {
            return description;
        }
    }
}