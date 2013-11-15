package org.osiam.storage.entities;

import org.osiam.resources.scim.ExtensionFieldType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Defines a field in a scim-extension.
 */
@Entity(name = "scim_extension_field")
public class ExtensionFieldEntity implements Serializable {

    private static final long serialVersionUID = 2411072287353978505L;

    @Id
    @GeneratedValue
    @Column(name = "internal_id")
    private long internalId;

    @Column
    private String name;

    @Transient
    private ExtensionFieldType<?> type;

    @Column(name = "is_required")
    private boolean isRequired;

    @ManyToOne(fetch = FetchType.EAGER)
    private ExtensionEntity extension;

    public ExtensionEntity getExtension() {
        return extension;
    }

    public void setExtension(ExtensionEntity extension) {
        this.extension = extension;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExtensionFieldType<?> getType() {
        return type;
    }

    public void setType(ExtensionFieldType<?> type) {
        this.type = type;
    }

    // @Access is necessary to provide a portable way of mapping our new extension field type in
    // anything < JPA 2.1 Should be replaced by a custom user type at some point.

    @Column(name = "type")
    @Access(AccessType.PROPERTY)
    private String getTypeAsString() {
        return type.toString(); // NOSONAR : This method is needed to serialize our type
    }

    private void setTypeAsString(String typeAsString) {
        type = ExtensionFieldType.valueOf(typeAsString); // NOSONAR : This method is needed to deserialize our type
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((extension == null) ? 0 : extension.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        ExtensionFieldEntity other = (ExtensionFieldEntity) obj;
        if (extension == null) {
            if (other.extension != null) {
                return false;
            }
        } else if (!extension.equals(other.extension)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}