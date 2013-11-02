package org.osiam.resources.helper;

import java.io.IOException;
import java.util.Map;

import org.osiam.resources.scim.SCIMSearchResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class AttributesRemovalHelper {


    public SCIMSearchResult removeSpecifiedAttributes(SCIMSearchResult resultList, Map<String, Object> parameterMap) {
        return getJsonResponseWithAdditionalFields(resultList, parameterMap);
    }

    private SCIMSearchResult getJsonResponseWithAdditionalFields(SCIMSearchResult scimSearchResult, Map<String, Object> parameterMap) {

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
            throw new IllegalArgumentException(e);
        }
    }

    private ObjectWriter getObjectWriter(ObjectMapper mapper, String[] fieldsToReturn) {

        if (fieldsToReturn.length != 0) {
            mapper.addMixInAnnotations(
                    Object.class, PropertyFilterMixIn.class);

            FilterProvider filters = new SimpleFilterProvider()
                    .addFilter("filter properties by name",
                            SimpleBeanPropertyFilter.filterOutAllExcept(
                                    fieldsToReturn));
            return mapper.writer(filters);
        }
        return mapper.writer();
    }
}