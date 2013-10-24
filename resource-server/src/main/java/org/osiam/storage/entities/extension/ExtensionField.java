package org.osiam.storage.entities.extension;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Defines a field in a scim-extension.
 */
@Entity(name = "scim_extension_field")
public class ExtensionField implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    @Column(name = "internal_id")
    private long internalId;

    @Column
    private String name;

    @Column
    private ExtensionFieldType type;

    @Column
    private boolean isRequired;

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

    public ExtensionFieldType getType() {
        return type;
    }

    public void setType(ExtensionFieldType type) {
        this.type = type;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }
}
