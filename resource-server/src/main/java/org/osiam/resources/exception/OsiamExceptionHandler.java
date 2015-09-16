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

package org.osiam.resources.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.osiam.resources.scim.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class OsiamExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsiamExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse defaultExceptionHandler(Exception ex) {
        LOGGER.warn("An unexpected exception occurred", ex);
        return produceErrorResponse("An unexpected error occurred", HttpStatus.CONFLICT);
    }

    @ExceptionHandler({IllegalArgumentException.class, InvalidConstraintException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidConstraintException(Exception e) {
        return produceErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleResourceExists(ResourceExistsException e) {
        return produceErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OsiamBackendFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleBackendFailure(OsiamBackendFailureException e) {
        return produceErrorResponse("An internal error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ResourceNotFoundException.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleResourceNotFoundException(Exception e) {
        return produceErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SchemaUnknownException.class)
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    @ResponseBody
    public ErrorResponse handleSchemaUnknown(SchemaUnknownException e) {
        return produceErrorResponse(e.getMessage(), HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ResponseBody
    public ErrorResponse handleUnsupportedOperation(UnsupportedOperationException e) {
        return produceErrorResponse(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleUnrecognizedProperty(Exception e) {
        LOGGER.error("Unknown property", e);
        return produceErrorResponse(e.getMessage(), HttpStatus.CONFLICT, new JsonPropertyMessageTransformer());
    }

    @ExceptionHandler(JsonMappingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleJsonMapping(Exception e) {
        LOGGER.error("Unable to deserialize", e);
        return produceErrorResponse(e.getMessage(), HttpStatus.CONFLICT, new JsonMappingMessageTransformer());
    }

    private ErrorResponse produceErrorResponse(String message, HttpStatus status) {
        return produceErrorResponse(message, status, null);
    }

    private ErrorResponse produceErrorResponse(String message, HttpStatus status,
                                               ErrorMessageTransformer transformer) {
        if (transformer != null) {
            message = transformer.transform(message);
        }
        return new ErrorResponse(status.value(), message);
    }

}
