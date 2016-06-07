/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.osiam.auth.exception.ClientAlreadyExistsException;
import org.osiam.auth.exception.ClientNotFoundException;
import org.osiam.auth.exception.JsonErrorResult;
import org.osiam.resources.scim.ErrorResponse;
import org.osiam.security.controller.TokenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(
        basePackages = {"org.osiam.auth.oauth_client", "org.osiam.metrics.controller", "org.osiam.resources.controller" },
        assignableTypes = TokenController.class
)
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse defaultExceptionHandler(Exception ex) {
        logger.warn("An unexpected exception occurred", ex);
        return produceErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OsiamException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleOsiamException(OsiamException e) {
        return produceErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({IllegalArgumentException.class, InvalidFilterException.class, InvalidConstraintException.class,
            InvalidTokenException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidRequests(Exception e) {
        return produceErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getFieldError().getDefaultMessage();
        return produceErrorResponse(message, HttpStatus.BAD_REQUEST);
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
    public ErrorResponse handleBackendFailure() {
        return produceErrorResponse("An internal error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ResourceNotFoundException.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleResourceNotFoundException(Exception e) {
        return produceErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected JsonErrorResult handleClientNotFound(ClientNotFoundException e) {
        logger.warn(e.getMessage());
        return new JsonErrorResult(HttpStatus.NOT_FOUND.name(), e.getMessage());
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected JsonErrorResult handleClientAlreadyExists(ClientAlreadyExistsException e) {
        logger.warn(e.getMessage());
        return new JsonErrorResult(HttpStatus.CONFLICT.name(), e.getMessage());
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
    public ErrorResponse handleUnrecognizedProperty(UnrecognizedPropertyException e) {
        logger.error("Unknown property", e);
        return produceErrorResponse(e.getMessage(), HttpStatus.CONFLICT, new JsonPropertyMessageTransformer());
    }

    @ExceptionHandler(JsonMappingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleJsonMapping(JsonMappingException e) {
        logger.error("Unable to deserialize", e);
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
