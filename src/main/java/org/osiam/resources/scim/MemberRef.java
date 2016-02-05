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
 * This class represents a {@link User} or a {@link Group} which are members of an actual {@link Group}
 * <p>
 * For more detailed information please look at the
 * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections 8</a>
 * </p>
 */
public final class MemberRef extends MultiValuedAttribute implements Serializable {

    private static final long serialVersionUID = 2965422671682767189L;

    private final Type type;

    /**
     * Constructor for deserialization, it is not intended for general use.
     */
    @JsonCreator
    private MemberRef(@JsonProperty("operation") String operation,
                     @JsonProperty("value") String value,
                     @JsonProperty("display") String display,
                     @JsonProperty("primary") boolean primary,
                     @JsonProperty("$ref") String reference,
                     @JsonProperty("type") Type type) {
        super(operation, value, display, primary, reference);
        this.type = type;
    }

    private MemberRef(Builder builder) {
        super(builder);
        type = builder.type;
    }

    @Override
    public String getReference() {
        return super.getReference();
    }

    @Override
    public String getOperation() {
        return super.getOperation();
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
     * For more detailed information please look at the
     * <a href= "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2" >SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the actual type
     */
    public Type getType() {
        return type;
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
        MemberRef other = (MemberRef) obj;
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
        return "MemberRef [display=" + getDisplay() + ", value=" + getValue() + ", type=" + type + ", primary="
                + isPrimary()
                + ", operation=" + getOperation() + ", ref=" + getReference() + "]";
    }

    /**
     * The Builder class is used to construct instances of the {@link MemberRef}
     */
    public static class Builder extends MultiValuedAttribute.Builder {

        private Type type;

        public Builder() {
        }

        /**
         * builds an Builder based of the given Attribute
         *
         * @param member existing Attribute
         */
        public Builder(MemberRef member) {
            super(member);
            type = member.type;
        }

        /**
         * builds an Builder based of the given {@link User} or {@link Group}
         *
         * @param resource existing {@link User} or {@link Group}
         */
        public Builder(Resource resource) {
            setValue(resource.getId());
        }

        @Override
        public Builder setReference(String reference) {
            super.setReference(reference);
            return this;
        }

        @Override
        public Builder setValue(String value) {
            super.setValue(value);
            return this;
        }

        @Override
        public Builder setDisplay(String display) {
            super.setDisplay(display);
            return this;
        }

        /**
         * Sets the label indicating the attribute's function (See {@link MemberRef#getType()}).
         *
         * @param type the type of the attribute
         * @return the builder itself
         */
        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        @Override
        public Builder setOperation(String operation) {
            super.setOperation(operation);
            return this;
        }

        @Override
        public MemberRef build() {
            return new MemberRef(this);
        }
    }

    /**
     * Represents an Member reference type.
     */
    public static class Type extends MultiValuedAttributeType {
        /**
         * indicates that the member is a {@link User}
         */
        public static final Type USER = new Type("User");
        /**
         * indicates that the member is a {@link Group}
         */
        public static final Type GROUP = new Type("Group");

        private Type(String value) {
            super(value);
        }
    }
}
