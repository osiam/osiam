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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.osiam.resources.scim.ExtensionFieldType;

/**
 * Defines a field in a scim-extension.
 */
@Entity
@Table(name = "scim_extension_field",
        indexes = {
                @Index(unique = true, columnList = "extension_internal_id, name")
        })
public class ExtensionFieldEntity { // NOSONAR - will be constructed by jackson

    @Id
    @SequenceGenerator(name = "sequence_scim_extension_field",
            sequenceName = "resource_server_sequence_scim_extension_field",
            allocationSize = 1,
            initialValue = 100)
    @GeneratedValue(generator = "sequence_scim_extension_field")
    @Column(name = "internal_id")
    private long internalId;

    private String name;

    /**
     * <p>
     * The type of this extension field.
     * </p>
     * 
     * <p>
     * Custom type mapping is provided by {@link org.osiam.storage.entities.jpa_converters.ExtensionFieldTypeConverter}.
     * </p>
     */
    @Basic(optional = false)
    private ExtensionFieldType<?> type;

    @Column(name = "is_required")
    private boolean required;

    @ManyToOne
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isConstrainedValid(String constraint) {
        return !invalidTypeForConstraint.contains(new ConstraintAndType(type, constraint));
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExtensionFieldEntity [name=").append(name).append(", type=").append(type)
                .append(", isRequired=").append(required).append("]");
        return builder.toString();
    }

    private static Set<ConstraintAndType> invalidTypeForConstraint = new HashSet<>(Arrays.asList(
            new ConstraintAndType(ExtensionFieldType.INTEGER, "co"),
            new ConstraintAndType(ExtensionFieldType.INTEGER, "sw"),
            new ConstraintAndType(ExtensionFieldType.DECIMAL, "co"),
            new ConstraintAndType(ExtensionFieldType.DECIMAL, "sw"),
            new ConstraintAndType(ExtensionFieldType.BOOLEAN, "co"),
            new ConstraintAndType(ExtensionFieldType.BOOLEAN, "sw"),
            new ConstraintAndType(ExtensionFieldType.BOOLEAN, "gt"),
            new ConstraintAndType(ExtensionFieldType.BOOLEAN, "ge"),
            new ConstraintAndType(ExtensionFieldType.BOOLEAN, "lt"),
            new ConstraintAndType(ExtensionFieldType.BOOLEAN, "le"),
            new ConstraintAndType(ExtensionFieldType.DATE_TIME, "co"),
            new ConstraintAndType(ExtensionFieldType.DATE_TIME, "sw"),
            new ConstraintAndType(ExtensionFieldType.BINARY, "eq"),
            new ConstraintAndType(ExtensionFieldType.BINARY, "co"),
            new ConstraintAndType(ExtensionFieldType.BINARY, "sw"),
            new ConstraintAndType(ExtensionFieldType.BINARY, "gt"),
            new ConstraintAndType(ExtensionFieldType.BINARY, "ge"),
            new ConstraintAndType(ExtensionFieldType.BINARY, "lt"),
            new ConstraintAndType(ExtensionFieldType.BINARY, "le"),
            new ConstraintAndType(ExtensionFieldType.REFERENCE, "gt"),
            new ConstraintAndType(ExtensionFieldType.REFERENCE, "ge"),
            new ConstraintAndType(ExtensionFieldType.REFERENCE, "lt"),
            new ConstraintAndType(ExtensionFieldType.REFERENCE, "le")
            ));

    private static class ConstraintAndType { // NOSONAR - class can be private
        private final ExtensionFieldType<?> type;
        private final String constraint;

        private ConstraintAndType(ExtensionFieldType<?> type, String constraint) {
            this.type = type;
            this.constraint = constraint;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {// NOSONAR - false-positive from clover; if-expression is correct
                return true;
            }
            if (o == null || getClass() != o.getClass()) {// NOSONAR - false-positive from clover; if-expression is
                                                          // correct
                return false;
            }

            ConstraintAndType that = (ConstraintAndType) o;

            if (constraint != null ? !constraint.equals(that.constraint) : that.constraint != null) {// NOSONAR -
                // false-positive from clover; if-expression is correct
                return false;
            }
            if (type != null ? !type.equals(that.type) : that.type != null) {// NOSONAR - false-positive from clover;
                                                                             // if-expression is correct
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = type != null ? type.hashCode() : 0;
            result = prime * result + (constraint != null ? constraint.hashCode() : 0);
            return result;
        }
    }
}