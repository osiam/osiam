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

package org.osiam.auth.exception;

import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.handler.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.*;

@ControllerAdvice
public class OsiamExceptionHandler extends SimpleMappingExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsiamExceptionHandler.class.getName());

    @ExceptionHandler(Exception.class)
    protected ModelAndView handleErrors(Exception e) {
        LOGGER.error("Showing error page, because of the following exception", e);
        return new ModelAndView("oauth_error");
    }

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected JsonErrorResult handleClientNotFound(ClientNotFoundException e) {
        LOGGER.warn(e.getMessage());
        return new JsonErrorResult(HttpStatus.NOT_FOUND.name(), e.getMessage());
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected JsonErrorResult handleClientAlreadyExists(ClientAlreadyExistsException e) {
        LOGGER.warn(e.getMessage());
        return new JsonErrorResult(HttpStatus.CONFLICT.name(), e.getMessage());
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    static class JsonErrorResult {

        @JsonProperty("error_code")
        private String errorCode;
        private String description;

        public JsonErrorResult(String name, String message) {
            errorCode = name;
            description = message;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getDescription() {
            return description;
        }
    }
}
