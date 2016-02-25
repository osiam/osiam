/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.scim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import org.osiam.resources.exception.SCIMDataValidationException;

import java.io.Serializable;

/**
 * This class represents a multi valued attribute.
 * <p>
 * For more detailed information please look at the
 * <a href= "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section
 * 3.2</a>
 * </p>
 */
@JsonInclude(Include.NON_EMPTY)
public abstract class MultiValuedAttribute implements Serializable {

    private static final long serialVersionUID = 5910207539638462247L;

    private final String operation;
    private final String value;
    private final String display;
    private final boolean primary;
    @JsonProperty("$ref")
    private final String reference;

    /**
     * This constructor is present for serialization purposes not for general use. Please don't use it.
     */
    @JsonCreator
    protected MultiValuedAttribute(@JsonProperty("operation") String operation,
                                   @JsonProperty("value") String value,
                                   @JsonProperty("display") String display,
                                   @JsonProperty("primary") boolean primary,
                                   @JsonProperty("$ref") String reference) {
        this.operation = operation;
        this.value = value;
        this.display = display;
        this.primary = primary;
        this.reference = reference;
    }


    protected MultiValuedAttribute(Builder builder) {
        this.value = builder.value;
        this.display = builder.display;
        this.primary = builder.primary;
        this.operation = builder.operation;
        this.reference = builder.reference;
    }

    /**
     * Gets the attribute's significant value; e.g., the e-mail address, phone number etc.
     * <p>
     * For more detailed information please look at the
     * <a href= "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2" >SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the value of the actual multi value attribute
     */
    protected String getValue() {
        return value;
    }

    /**
     * Gets the human readable name, primarily used for display purposes.
     * <p>
     * For more detailed information please look at the
     * <a href= "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2" >SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the display attribute
     */
    protected String getDisplay() {
        return display;
    }

    /**
     * Gets a Boolean value indicating the 'primary' or preferred attribute value for this attribute.
     * <p>
     * For more detailed information please look at the
     * <a href= "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2" >SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the primary attribute
     */
    protected boolean isPrimary() {
        return primary;
    }

    /**
     * Gets the operation applied during a PATCH request.
     * <p>
     * For more detailed information please look at the
     * <a href= "http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2" >SCIM core schema 2.0, section
     * 3.2</a>
     * </p>
     *
     * @return the operation
     */
    protected String getOperation() {
        return operation;
    }

    /**
     * Gets the reference to the actual SCIM Resource.
     * <p/>
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections
     * 8</a>
     * </p>
     *
     * @return the reference of the actual resource
     */
    protected String getReference() {
        return reference;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((display == null) ? 0 : display.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + (primary ? 1231 : 1237);
        result = prime * result + ((reference == null) ? 0 : reference.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        MultiValuedAttribute other = (MultiValuedAttribute) obj;
        if (display == null) {
            if (other.display != null) {
                return false;
            }
        } else if (!display.equals(other.display)) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (primary != other.primary) {
            return false;
        }
        if (reference == null) {
            if (other.reference != null) {
                return false;
            }
        } else if (!reference.equals(other.reference)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MultiValuedAttribute [operation=" + operation + ", value=" + value + ", display=" + display
                + ", primary=" + primary + ", reference=" + reference + "]";
    }

    /**
     * Builder class that is used to build {@link MultiValuedAttribute} instances
     */
    public abstract static class Builder {

        private String operation;
        private String value;
        private String display;
        private boolean primary;
        private String reference;

        protected Builder() {
        }

        protected Builder(MultiValuedAttribute multiValuedAttribute) {
            if (multiValuedAttribute == null) {
                throw new IllegalArgumentException("The given attribute can't be null.");
            }
            this.operation = multiValuedAttribute.getOperation();
            this.value = multiValuedAttribute.value;
            this.display = multiValuedAttribute.display;
            this.primary = multiValuedAttribute.primary;
        }

        /**
         * Sets the attribute's significant value (See {@link MultiValuedAttribute#getValue()}).
         *
         * @param value the value attribute
         * @return the builder itself
         */
        protected Builder setValue(String value) {
            if (Strings.isNullOrEmpty(value)) {
                throw new SCIMDataValidationException("The given value can't be null or empty.");
            }
            this.value = value;
            return this;
        }

        /**
         * Sets the human readable name (See {@link MultiValuedAttribute#getDisplay()}).
         * <p>
         * client info: the Display value is set by the OSIAM server. If a MultiValuedAttribute which is send to the
         * OSIAM server has this value filled, the value will be ignored or the action will be rejected.
         * </p>
         *
         * @param display a human readable name
         * @return the builder itself
         */
        protected Builder setDisplay(String display) {
            this.display = display;
            return this;
        }

        /**
         * Sets the primary attribute (See {@link MultiValuedAttribute#isPrimary()}).
         *
         * @param primary indicates if this is the primary attribute
         * @return the builder itself
         */
        protected Builder setPrimary(boolean primary) {
            this.primary = primary;
            return this;
        }

        /**
         * Sets the operation (See {@link MultiValuedAttribute#getOperation()}).
         * <p>
         * Will be automatically set by the {@link UpdateUser}
         * </p>
         *
         * @param operation only "delete" is supported at the moment
         * @return the builder itself
         */
        protected Builder setOperation(String operation) {
            this.operation = operation;
            return this;
        }

        /**
         * Sets the reference (See {@link MemberRef#getReference()}).
         * <p>
         * client info: the Reference value is set by the OSIAM server. If a MultiValuedAttribute which is send to the
         * OSIAM server has this value filled, the value will be ignored or the action will be rejected.
         * </p>
         *
         * @param reference the scim conform reference to the member
         * @return the builder itself
         */
        protected Builder setReference(String reference) {
            this.reference = reference;
            return this;
        }

        /**
         * Builds a new Attribute with the given parameters
         *
         * @return a new MultiValuedAttribute Object
         */
        protected abstract MultiValuedAttribute build();
    }
}
