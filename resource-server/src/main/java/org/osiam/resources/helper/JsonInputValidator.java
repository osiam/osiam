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

package org.osiam.resources.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osiam.resources.helper.validators.PatchValidator;
import org.osiam.resources.helper.validators.PostValidator;
import org.osiam.resources.helper.validators.PutValidator;
import org.osiam.resources.helper.validators.Validator;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Service
public class JsonInputValidator {

    private Map<RequestMethod, Validator> validators;

    public JsonInputValidator() {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule testModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null, "org.osiam", "scim-schema"))
                .addDeserializer(User.class, new UserDeserializer(User.class));
        mapper.registerModule(testModule);

        validators = new HashMap<>();
        validators.put(RequestMethod.PATCH, new PatchValidator(mapper));
        validators.put(RequestMethod.POST, new PostValidator(mapper));
        validators.put(RequestMethod.PUT, new PutValidator(mapper));
    }

    public User validateJsonUser(HttpServletRequest request) throws IOException {
        String jsonInput = getRequestBody(request);
        Validator validator = validators.get(RequestMethod.valueOf(request.getMethod()));
        User user;
        try {
            user = validator.validateJsonUser(jsonInput);
        } catch (JsonParseException ex) {
            throw new IllegalArgumentException("The JSON structure is invalid", ex);
        }
        if (user.getId() != null && !user.getId().isEmpty()) {
            user = new User.Builder(user).setId(null).build();
        }
        return user;
    }

    public Group validateJsonGroup(HttpServletRequest request) throws IOException {
        String jsonInput = getRequestBody(request);
        Validator validator = validators.get(RequestMethod.valueOf(request.getMethod()));
        try {
            return validator.validateGroup(jsonInput);
        } catch (JsonParseException ex) {
            throw new IllegalArgumentException("The JSON structure is invalid", ex);
        }
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

}