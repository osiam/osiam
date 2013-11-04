package org.osiam.resources.helper.validators;

import java.io.IOException;

import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;

public interface Validator {
    User validateJsonUser(String json) throws IOException;

    Group validateGroup(String json) throws IOException;
}
