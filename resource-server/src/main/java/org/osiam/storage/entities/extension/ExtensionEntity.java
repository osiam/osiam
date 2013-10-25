package org.osiam.storage.entities.extension;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines a SCIM-Extension.
 */
@Entity(name = "scim_extension")
public class ExtensionEntity implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
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
}