package org.osiam.resources.helper;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.node.ObjectNode;
import org.osiam.resources.scim.SCIMSearchResult;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.05.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
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

        if(fieldsToReturn.length != 0) {
            mapper.getSerializationConfig().addMixInAnnotations(
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