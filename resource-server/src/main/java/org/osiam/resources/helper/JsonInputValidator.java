package org.osiam.resources.helper;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.06.13
 * Time: 09:43
 * To change this template use File | Settings | File Templates.
 */
@Service
public class JsonInputValidator {

    public User validateJsonUser(HttpServletRequest request) throws IOException {
        String jsonInput = getRequestBody(request);

        if(jsonInput.contains("userName") || request.getMethod().equals("PATCH")) {
            return validateResource(jsonInput, User.class);
        }
        throw new IllegalArgumentException("The attribute userName is mandatory and MUST NOT be null");
    }

    public Group validateJsonGroup(HttpServletRequest request) throws IOException {
        String jsonInput = getRequestBody(request);

        if(jsonInput.contains("displayName") || request.getMethod().equals("PATCH")) {
            return validateResource(jsonInput, Group.class);
        }
        throw new IllegalArgumentException("The attribute displayName is mandatory and MUST NOT be null.");
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        String line;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }

        return stringBuffer.toString();
    }

    private <T> T validateResource(String jsonInput, Class<T> clazz) throws IOException {
        T resource;
        ObjectMapper mapper = new ObjectMapper();
        try {
            resource = mapper.readValue(jsonInput, clazz);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("The JSON structure is incorrect", e);
        }
        return resource;
    }
}