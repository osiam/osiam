package org.osiam.resources.provisioning.model;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains meta information about a {@link Extension}.
 */
public class ExtensionDefinition {

    private String urn;

    private Map<String, String> namedTypePairs = new HashMap<>();

    public ExtensionDefinition(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public Map<String, String> getNamedTypePairs() {
        return namedTypePairs;
    }

    public void setNamedTypePairs(Map<String, String> namedTypePairs) {
        this.namedTypePairs = namedTypePairs;
    }

    public void addPair(String fieldName, ExtensionFieldType<?> type) {
        namedTypePairs.put(fieldName, type.getName());
    }
}
