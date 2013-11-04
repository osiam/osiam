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
