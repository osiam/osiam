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