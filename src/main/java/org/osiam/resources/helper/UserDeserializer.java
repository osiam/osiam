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
package org.osiam.resources.helper;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.resources.scim.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

public class UserDeserializer extends StdDeserializer<User> {

    private static final long serialVersionUID = 1L;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final String schema;

    /**
     * Create a {@link UserDeserializer} that validates the {@link User} against {@link User#SCHEMA}.
     */
    public UserDeserializer() {
        this(User.SCHEMA);
    }

    /**
     * Create a {@link UserDeserializer} that validates the {@link User} against the given schema
     * instead of {@link User#SCHEMA}.
     */
    public UserDeserializer(String schema) {
        super(User.class);
        this.schema = schema;
    }

    /**
     * @deprecated Use {@link UserDeserializer#UserDeserializer()} or
     * {@link UserDeserializer#UserDeserializer(String)}. Will be removed in 1.9 or 2.0.
     */
    public UserDeserializer(Class<?> valueClass) {
        super(valueClass);
        this.schema = User.SCHEMA;
    }

    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode rootNode = jp.readValueAsTree();

        User user = MAPPER.readValue(rootNode.traverse(), User.class);
        if (user.getSchemas() == null) {
            throw new JsonMappingException("Required field 'schemas' is missing");
        }
        if (user.getSchemas().size() == 1) {
            return user;
        }

        User.Builder builder = new User.Builder(user);

        for (String urn : user.getSchemas()) {
            if (urn.equals(schema)) {
                continue;
            }

            JsonNode extensionNode = rootNode.get(urn);
            if (extensionNode == null) {
                throw new JsonParseException("Registered extension not present: " + urn, JsonLocation.NA);
            }

            builder.addExtension(deserializeExtension(extensionNode, urn));

        }
        return builder.build();
    }

    public Extension deserializeExtension(JsonNode rootNode, String urn) throws IOException {
        if (urn == null || urn.isEmpty()) {
            throw new IllegalStateException("The URN cannot be null or empty");
        }
        if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new JsonMappingException("Extension is of wrong JSON type");
        }
        Extension.Builder extensionBuilder = new Extension.Builder(urn);
        Iterator<Map.Entry<String, JsonNode>> fieldIterator = rootNode.fields();
        while (fieldIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldIterator.next();
            switch (entry.getValue().getNodeType()) {
                case BOOLEAN:
                    Boolean boolValue = ExtensionFieldType.BOOLEAN.fromString(entry.getValue().asText());
                    extensionBuilder.setField(entry.getKey(), boolValue);
                    break;
                case STRING:
                    String stringValue = ExtensionFieldType.STRING.fromString(entry.getValue().asText());
                    extensionBuilder.setField(entry.getKey(), stringValue);
                    break;
                case NUMBER:
                    String numberValueAsString = entry.getValue().asText();
                    if (numberValueAsString.contains(".")) {
                        BigDecimal decimalValue = ExtensionFieldType.DECIMAL.fromString(numberValueAsString);
                        extensionBuilder.setField(entry.getKey(), decimalValue);
                    } else {
                        BigInteger integerValue = ExtensionFieldType.INTEGER.fromString(numberValueAsString);
                        extensionBuilder.setField(entry.getKey(), integerValue);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("JSON type not supported: " + entry.getValue().getNodeType());
            }
        }

        return extensionBuilder.build();
    }
}
