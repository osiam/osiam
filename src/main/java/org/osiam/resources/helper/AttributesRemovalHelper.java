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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.osiam.resources.exception.OsiamBackendFailureException;
import org.osiam.resources.scim.Resource;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class AttributesRemovalHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttributesRemovalHelper.class);

    public <T extends Resource> SCIMSearchResult<T> removeSpecifiedAttributes(SCIMSearchResult<T> resultList,
                                                                              Map<String, Object> parameterMap) {
        return getJsonResponseWithAdditionalFields(resultList, parameterMap);
    }

    public SCIMSearchResult<User> removeSpecifiedUserAttributes(SCIMSearchResult<User> resultList,
                                                                Map<String, Object> parameterMap) {
        return getUserJsonResponseWithAdditionalFields(resultList, parameterMap);
    }

    private SCIMSearchResult<User> getUserJsonResponseWithAdditionalFields(
            SCIMSearchResult<User> scimSearchResult, Map<String, Object> parameterMap) {

        ObjectMapper mapper = new ObjectMapper();

        String[] fieldsToReturn = (String[]) parameterMap.get("attributes");
        if (fieldsToReturn.length == 0) {
            return scimSearchResult;
        }

        for (int i = 0; i < fieldsToReturn.length; i++) {
            fieldsToReturn[i] = fieldsToReturn[i].trim();
        }

        ObjectWriter writer = getObjectWriter(mapper, fieldsToReturn);

        try {
            List<User> users = scimSearchResult.getResources();
            users = filterUserSchemas(users, fieldsToReturn);
            String resourcesString = writer.writeValueAsString(users);
            JsonNode resourcesNode = mapper.readTree(resourcesString);

            String schemasString = writer.writeValueAsString(scimSearchResult.getSchemas());
            JsonNode schemasNode = mapper.readTree(schemasString);

            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("totalResults", scimSearchResult.getTotalResults());
            rootNode.put("itemsPerPage", scimSearchResult.getItemsPerPage());
            rootNode.put("startIndex", scimSearchResult.getStartIndex());
            rootNode.put("schemas", schemasNode);
            rootNode.put("Resources", resourcesNode);

            return mapper.readValue(rootNode.toString(), SCIMSearchResult.class);
        } catch (IOException e) {
            LOGGER.warn("Unable to serialize search result", e);
            throw new OsiamBackendFailureException();
        }
    }

    private <T extends Resource> SCIMSearchResult<T> getJsonResponseWithAdditionalFields(
            SCIMSearchResult<T> scimSearchResult, Map<String, Object> parameterMap) {

        ObjectMapper mapper = new ObjectMapper();

        String[] fieldsToReturn = (String[]) parameterMap.get("attributes");
        ObjectWriter writer = getObjectWriter(mapper, fieldsToReturn);

        try {
            String resourcesString = writer.writeValueAsString(scimSearchResult.getResources());
            JsonNode resourcesNode = mapper.readTree(resourcesString);

            String schemasString = writer.writeValueAsString(scimSearchResult.getSchemas());
            JsonNode schemasNode = mapper.readTree(schemasString);

            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("totalResults", scimSearchResult.getTotalResults());
            rootNode.put("itemsPerPage", scimSearchResult.getItemsPerPage());
            rootNode.put("startIndex", scimSearchResult.getStartIndex());
            rootNode.put("schemas", schemasNode);
            rootNode.put("Resources", resourcesNode);

            return mapper.readValue(rootNode.toString(), SCIMSearchResult.class);
        } catch (IOException e) {
            LOGGER.warn("Unable to serialize search result", e);
            throw new OsiamBackendFailureException();
        }
    }

    private List<User> filterUserSchemas(List<User> resourceList, String[] fieldsToReturn) {

        if (resourceList.size() == 0) {
            return resourceList;
        }
        List<User> modifiedResourceList = new ArrayList<>();
        Set<String> newSchema = new HashSet<>();
        List<String> returnFields = Arrays.asList(fieldsToReturn);
        for (User resource : resourceList) {
            for (String schema : resource.getSchemas()) {
                if (schema.equals(User.SCHEMA) || returnFields.contains(schema)) {
                    newSchema.add(schema);
                }
            }
            User modifiedUser = new User.Builder(resource).setSchemas(newSchema).build();
            modifiedResourceList.add(modifiedUser);
        }
        return modifiedResourceList;
    }

    private ObjectWriter getObjectWriter(ObjectMapper mapper, String[] fieldsToReturn) {

        if (fieldsToReturn.length != 0) {
            mapper.addMixInAnnotations(
                    Object.class, PropertyFilterMixIn.class);

            HashSet<String> givenFields = new HashSet<>();
            givenFields.add("schemas");
            Collections.addAll(givenFields, fieldsToReturn);
            String[] finalFieldsToReturn = givenFields.toArray(new String[givenFields.size()]);

            FilterProvider filters = new SimpleFilterProvider()
                    .addFilter("filter properties by name",
                            SimpleBeanPropertyFilter.filterOutAllExcept(
                                    finalFieldsToReturn));
            return mapper.writer(filters);
        }
        return mapper.writer();
    }
}
