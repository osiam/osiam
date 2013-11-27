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

package org.osiam.resources.helper.validators;

import java.io.IOException;

import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PostValidator implements Validator {

    private ObjectMapper mapper;

    public PostValidator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public User validateJsonUser(String json) throws IOException {

        User user = mapper.readValue(json, User.class);

        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            throw new IllegalArgumentException("The attribute userName is mandatory and MUST NOT be null");
        }

        return user;

    }

    @Override
    public Group validateGroup(String json) throws IOException {
        Group group = mapper.readValue(json, Group.class);

        if (group.getDisplayName() == null || group.getDisplayName().isEmpty()) {
            throw new IllegalArgumentException("The attribute displayName is mandatory and MUST NOT be null");
        }
        return group;
    }
}
