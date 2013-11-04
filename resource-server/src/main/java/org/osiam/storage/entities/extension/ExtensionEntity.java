package org.osiam.storage.entities.extension;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.osiam.resources.scim.Extension;
import org.osiam.storage.entities.UserEntity; //NOSONAR - Needed due to bidirectional relation

/**
 * Defines a SCIM-Extension.
 */
@Entity(name = "scim_extension")
public class ExtensionEntity implements Serializable {

    private static final long serialVersionUID = 2192284482983111198L;

    @Id
    @GeneratedValue
    @Column(name = "internal_id")
    private long internalId;

    @Column(name = "urn", nullable = false, unique = true)
    private String urn;

    @OneToMany(mappedBy = "extension")
    private Set<ExtensionFieldEntity> fields;

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String extensionUrn) {
        this.urn = extensionUrn;
    }

    public Set<ExtensionFieldEntity> getFields() {
        if (fields == null) {
            fields = new HashSet<>();
        }
        return fields;
    }

    public void setFields(Set<ExtensionFieldEntity> extensionFields) {
        if (extensionFields != null) {
            for (ExtensionFieldEntity extensionFieldEntity : extensionFields) {
                extensionFieldEntity.setExtension(this);
            }
        }
        this.fields = extensionFields;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((urn == null) ? 0 : urn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExtensionEntity other = (ExtensionEntity) obj;
        if (urn == null) {
            if (other.urn != null) {
                return false;
            }
        } else if (!urn.equals(other.urn)) {
            return false;
        }
        return true;
    }


    /**
     * Converting all ExtensionFields of the User into a Scim-Extensions representation.
     *
     * @param userEntity
     *          getting the extensions for conversion
     * @return A Map of extensions with corresponding urn's. Never null
     */
    public static Map<String, Extension> toScim(UserEntity userEntity) {
        // Sorting all fields by extension
        Map<String, Map<String, String>> sortedValues = new HashMap<>();
        for (ExtensionFieldValueEntity extFieldValue : userEntity.getUserExtensions()) {
            String urn = extFieldValue.getExtensionField().getExtension().getUrn();
            String fieldName = extFieldValue.getExtensionField().getName();
            String value = extFieldValue.getValue();

            Map<String, String> extMap = sortedValues.get(urn);
            if (extMap == null) {
                extMap = new HashMap<String, String>();
                sortedValues.put(urn, extMap);
            }
            extMap.put(fieldName, value);
        }

        // Convert Extensions to scim Extensions
        Map<String, Extension> res = new HashMap<>();
        for(Map.Entry<String, Map<String, String>> extensionEntry : sortedValues.entrySet()) {
            Extension ext = new Extension(extensionEntry.getKey(), extensionEntry.getValue());
            String urn = extensionEntry.getKey();
            res.put(urn, ext);
        }
        return res;
    }   
}