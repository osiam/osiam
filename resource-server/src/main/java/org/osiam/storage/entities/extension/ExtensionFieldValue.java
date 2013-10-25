package org.osiam.storage.entities.extension;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.osiam.storage.entities.UserEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Defines a value of a field of a scim-extension. It's user-dependent!
 */
@Entity(name = "scim_extension_field_value")
public class ExtensionFieldValue implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    @Column(name = "internal_id")
    private long internalId;

    @ManyToOne
    @Column(name = "extension_field")
    private ExtensionField extensionField;

    @ManyToOne
    @Column
    private UserEntity user;

    @Column
    private String value;

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public ExtensionField getExtensionField() {
        return extensionField;
    }

    public void setExtensionField(ExtensionField extensionField) {
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


