/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.storage.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Defines a value of a field of a scim-extension. It's user-dependent!
 */
@Entity
@Table(name = "scim_extension_field_value",
    indexes = {
        @Index(columnList = UserEntity.JOIN_COLUMN_NAME + ", extension_field_internal_id"),
    }
)
public class ExtensionFieldValueEntity {

    @Id
    @GeneratedValue
    @Column(name = "internal_id")
    private long internalId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "extension_field_internal_id")
    private ExtensionFieldEntity extensionField;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(nullable = false)
    private String value;

    @ManyToOne(optional = false)
    @JoinColumn(name = UserEntity.JOIN_COLUMN_NAME, nullable = false, insertable = false, updatable = false)
    private UserEntity user;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((extensionField == null) ? 0 : extensionField.hashCode());
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
        ExtensionFieldValueEntity other = (ExtensionFieldValueEntity) obj;
        if (extensionField == null) {
            if (other.extensionField != null) {
                return false;
            }
        } else if (!extensionField.equals(other.extensionField)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExtensionFieldValueEntity [extensionField=").append(extensionField).append(", value=")
                .append(value).append("]");
        return builder.toString();
    }

}
