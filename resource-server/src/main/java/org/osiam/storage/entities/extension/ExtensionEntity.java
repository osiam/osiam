package org.osiam.storage.entities.extension;

import org.osiam.resources.scim.Extension;
import org.osiam.storage.entities.UserEntity;

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

/**
 * Defines a SCIM-Extension.
 */
@Entity(name = "scim_extension")
public class ExtensionEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "internal_id")
    private long internalId;

    @Column(name = "urn", nullable = false, unique = true)
    private String extensionUrn;

    @OneToMany(mappedBy = "extension")
    private Set<ExtensionFieldEntity> extensionFields;

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getExtensionUrn() {
        return extensionUrn;
    }

    public void setExtensionUrn(String extensionUrn) {
        this.extensionUrn = extensionUrn;
    }

    public Set<ExtensionFieldEntity> getExtensionFields() {
        if (extensionFields == null) {
            extensionFields = new HashSet<>();
        }
        return extensionFields;
    }

    public void setExtensionFields(Set<ExtensionFieldEntity> extensionFields) {
        if (extensionFields != null) {
            for (ExtensionFieldEntity extensionFieldEntity : extensionFields) {
                extensionFieldEntity.setExtension(this);
            }
        }
        this.extensionFields = extensionFields;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((extensionUrn == null) ? 0 : extensionUrn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExtensionEntity other = (ExtensionEntity) obj;
        if (extensionUrn == null) {
            if (other.extensionUrn != null)
                return false;
        } else if (!extensionUrn.equals(other.extensionUrn))
            return false;
        return true;
    }
    
    


    /**
     * Konvertiert alle ExtensionFields des Users zu Scim-Extensions.

     *
     * @param userEntity
     * @return never null
     */
    public static Map<String, Extension> toScim(UserEntity userEntity) {
        // Alle Felder nach Extensions sortieren
        Map<String, Map<String, String>> sortedValues = new HashMap<>();
        for (ExtensionFieldValueEntity extFieldValue : userEntity.getUserExtensions()) {
            String urn = extFieldValue.getExtensionField().getExtension().getExtensionUrn();
            String fieldName = extFieldValue.getExtensionField().getName();
            String value = extFieldValue.getValue();

            Map<String, String> extMap = sortedValues.get(urn);
            if (extMap == null) {
                extMap = new HashMap<String, String>();
                sortedValues.put(urn, extMap);
            }
            extMap.put(fieldName, value);
        }

        // Extensions in ScimExtensions umwandeln
        Map<String, Extension> res = new HashMap<>();
        for(Map.Entry<String, Map<String, String>> extensionEntry : sortedValues.entrySet()) {
            Extension ext = new Extension(extensionEntry.getValue());
            String urn = extensionEntry.getKey();
            res.put(urn, ext);
        }
        return res;
    }   
}