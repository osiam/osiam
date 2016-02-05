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
package org.osiam.resources.scim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * This class represents a Reference of a Group
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section 3.2</a>
 * </p>
 */
public final class GroupRef extends MultiValuedAttribute implements Serializable {

    private static final long serialVersionUID = -3765452569557089013L;

    private final Type type;

    @JsonCreator
    private GroupRef(@JsonProperty("operation") String operation,
                     @JsonProperty("value") String value,
                     @JsonProperty("display") String display,
                     @JsonProperty("primary") boolean primary,
                     @JsonProperty("$ref") String reference,
                     @JsonProperty("type") Type type) {
        super(operation, value, display, primary, reference);
        this.type = type;
    }

    private GroupRef(Builder builder) {
        super(builder);
        this.type = builder.type;
    }

    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public String getDisplay() {
        return super.getDisplay();
    }

    /**
     * Gets the type of the attribute.
     * <p>
     * For more detailed information please look at the <a href=
     * "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2" >SCIM core schema 2.0, section 3.2</a>
     * </p>
     *
     * @return the actual type
     */
    public Type getType() {
        return type;
    }

    @Override
    public String getReference() {
        return super.getReference();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GroupRef other = (GroupRef) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GroupRef [display=" + getDisplay() + ", value=" + getValue() + ", type=" + type + ", primary="
                + isPrimary() + ", operation=" + getOperation() + ", ref=" + getReference() + "]";
    }

    /**
     * Builder class that is used to build {@link GroupRef} instances
     */
    public static class Builder extends MultiValuedAttribute.Builder {

        private Type type;

        @Override
        public Builder setDisplay(String display) {
            super.setDisplay(display);
            return this;
        }

        @Override
        public Builder setValue(String value) {
            super.setValue(value);
            return this;
        }

        /**
         * Sets the label indicating the attribute's function (See {@link GroupRef#getType()}).
         *
         * @param type the type of the attribute
         * @return the builder itself
         */
        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        @Override
        public Builder setReference(String reference) {
            super.setReference(reference);
            return this;
        }

        @Override
        public GroupRef build() {
            return new GroupRef(this);
        }

    }

    /**
     * Represents an Group reference type.
     */
    public static class Type extends MultiValuedAttributeType {
        /**
         * The User is in the Group directly.
         */
        public static final Type DIRECT = new Type("direct");
        /**
         * The User is not directly in the  Group but in another Group that is in the  Group
         */
        public static final Type INDIRECT = new Type("indirect");

        private Type(String value) {
            super(value);
        }
    }

}
