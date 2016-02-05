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
import org.osiam.resources.exception.SCIMDataValidationException;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a email attribute.
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section 3.2</a>
 * </p>
 */
public final class Email extends MultiValuedAttribute implements Serializable {

    private static final long serialVersionUID = 5595340465249831012L;

    private final Type type;

    /**
     * Constructor for deserialization, it is not intended for general use.
     */
    @JsonCreator
    private Email(@JsonProperty("operation") String operation,
                  @JsonProperty("value") String value,
                  @JsonProperty("display") String display,
                  @JsonProperty("primary") boolean primary,
                  @JsonProperty("$ref") String reference,
                  @JsonProperty("type") Type type) {
        super(operation, value, display, primary, reference);
        this.type = type;
    }

    private Email(Builder builder) {
        super(builder);
        this.type = builder.type;
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

    @Override
    public boolean isPrimary() {
        return super.isPrimary();
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
        Email other = (Email) obj;
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
        return "Email [value=" + getValue() + ", type=" + type + ", primary=" + isPrimary()
                + ", operation=" + getOperation() + "]";
    }

    /**
     * Builder class that is used to build {@link Email} instances
     */
    public static class Builder extends MultiValuedAttribute.Builder {

        /**
         * Pattern comes from: http://www.w3.org/TR/html5/forms.html#valid-e-mail-address
         */
        public static final Pattern VALIDATION_PATTERN = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@" +
                "[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

        private Type type;

        public Builder() {
        }

        /**
         * builds an Builder based of the given Attribute
         *
         * @param email existing Attribute
         */
        public Builder(Email email) {
            super(email);
            type = email.type;
        }

        @Override
        public Builder setOperation(String operation) {
            super.setOperation(operation);
            return this;
        }

        @Override
        public Builder setDisplay(String display) {
            super.setDisplay(display);
            return this;
        }

        /**
         * Sets the email value.
         *
         * @param value the email attribute
         * @return the builder itself
         * @throws SCIMDataValidationException in case the value is not a well-formed email
         */
        @Override
        public Builder setValue(String value) {
            Matcher matcher = VALIDATION_PATTERN.matcher(value);
            if (!matcher.matches()) {
                throw new SCIMDataValidationException("The value '" + value + "' is not a well-formed email.");
            }
            super.setValue(value);
            return this;
        }

        /**
         * Sets the label indicating the attribute's function (See {@link Email#getType()}).
         *
         * @param type the type of the attribute
         * @return the builder itself
         */
        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        @Override
        public Builder setPrimary(boolean primary) {
            super.setPrimary(primary);
            return this;
        }

        @Override
        public Email build() {
            return new Email(this);
        }
    }

    /**
     * Represents an email type. Canonical values are available as static constants.
     */
    public static class Type extends MultiValuedAttributeType {
        public static final Type WORK = new Type("work");
        public static final Type HOME = new Type("home");
        public static final Type OTHER = new Type("other");

        public Type(String value) {
            super(value);
        }
    }

}
