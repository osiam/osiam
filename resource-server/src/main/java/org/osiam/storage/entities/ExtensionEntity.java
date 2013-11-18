package org.osiam.storage.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines a SCIM-Extension.
 */
@Entity(name = "scim_extension")
public class ExtensionEntity {

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

}