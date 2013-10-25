package org.osiam.storage.entities.extension;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.osiam.storage.entities.UserEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Defines a value of a field of a scim-extension. It's user-dependent!
 */
@Entity(name = "scim_extension_field_value")
public class ExtensionFieldValueEntity implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    @Column(name = "internal_id")
    private long internalId;

    @Column(name = "extension_field")
    private ExtensionFieldEntity extensionField;

    @ManyToOne
    private UserEntity user;

    @Column
    private String value;

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public ExtensionFieldEntity getExtensionField() {
        return extensionField;
    }

    public void setExtensionField(ExtensionFieldEntity extensionField) {
        this.extensionField = extensionField;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


