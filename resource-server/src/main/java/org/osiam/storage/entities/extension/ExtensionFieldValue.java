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
    private ExtensionField extensionField;

    @ManyToOne
    private UserEntity userEntity;

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

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


