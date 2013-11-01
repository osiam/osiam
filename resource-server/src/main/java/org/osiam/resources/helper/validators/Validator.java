package org.osiam.resources.helper.validators;

import com.fasterxml.jackson.core.JsonParseException;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;

import java.io.IOException;

public interface Validator {
    User validateJsonUser(String json) throws IOException;

    Group validateGroup(String json) throws IOException;
}
