package org.osiam.storage.entities.extension;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Defines a SCIM-Extension.
 */
@Entity(name = "scim_extension")
public class Extension implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    @Column(name = "internal_id")
    private long internalId;

    @Column(name = "urn", nullable = false, unique = true)
    private String extensionUrn;

    @OneToMany
    @JoinColumn(name="internal_id")
    private Set<ExtensionField> extensionFields;

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

    public Set<ExtensionField> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(Set<ExtensionField> extensionFields) {
        this.extensionFields = extensionFields;
    }
}
