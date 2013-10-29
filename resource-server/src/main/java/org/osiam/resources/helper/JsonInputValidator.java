package org.osiam.resources.helper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Service
public class JsonInputValidator {

    private ObjectMapper mapper;

    public JsonInputValidator() {
        mapper = new ObjectMapper();
        SimpleModule testModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null, "org.osiam", "scim-schema"))
                .addDeserializer(User.class, new UserDeserializer(User.class));
        mapper.registerModule(testModule);
    }

    public User validateJsonUser(HttpServletRequest request) throws IOException {
        String jsonInput = getRequestBody(request);

        if (jsonInput.contains("userName") || request.getMethod().equals("PATCH")) {
            return validateResource(jsonInput, User.class);
        }
        throw new IllegalArgumentException("The attribute userName is mandatory and MUST NOT be null");
    }

    public Group validateJsonGroup(HttpServletRequest request) throws IOException {
        String jsonInput = getRequestBody(request);

        if (jsonInput.contains("displayName") || request.getMethod().equals("PATCH")) {
            return validateResource(jsonInput, Group.class);
        }
        throw new IllegalArgumentException("The attribute displayName is mandatory and MUST NOT be null.");
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

    private <T> T validateResource(String jsonInput, Class<T> clazz) throws IOException {
        T resource;
        try {
            resource = mapper.readValue(jsonInput, clazz);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("The JSON structure is incorrect", e);
        }
        return resource;
    }
}