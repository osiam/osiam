package org.osiam.resources.helper.validators;

import java.io.IOException;

import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PatchValidator implements Validator {

    private ObjectMapper mapper;

    public PatchValidator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public User validateJsonUser(String json) throws IOException {
        return mapper.readValue(json, User.class);
    }

    @Override
    public Group validateGroup(String json) throws IOException {
        return mapper.readValue(json, Group.class);
    }
}
