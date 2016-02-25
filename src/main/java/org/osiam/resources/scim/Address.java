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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * A physical mailing address for a User
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0</a>
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class Address extends MultiValuedAttribute implements Serializable {

    private static final long serialVersionUID = 2731087785568277294L;

    private final String formatted;
    private final String streetAddress;
    private final String locality;
    private final String region;
    private final String postalCode;
    private final String country;
    private final Type type;

    /**
     * Constructor for deserialization, it is not intended for general use.
     */
    @JsonCreator
    private Address(@JsonProperty("operation") String operation,
                    @JsonProperty("value") String value,
                    @JsonProperty("display") String display,
                    @JsonProperty("primary") boolean primary,
                    @JsonProperty("$ref") String reference,
                    @JsonProperty("formatted") String formatted,
                    @JsonProperty("streetAddress") String streetAddress,
                    @JsonProperty("locality") String locality,
                    @JsonProperty("region") String region,
                    @JsonProperty("postalCode") String postalCode,
                    @JsonProperty("country") String country,
                    @JsonProperty("type") Type type) {
        super(operation, value, display, primary, reference);
        this.formatted = formatted;
        this.streetAddress = streetAddress;
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
        this.type = type;
    }

    /**
     * Constructor for serialization. It is not intended for public use.
     */
    private Address(Builder builder) {
        super(builder);
        this.formatted = builder.formatted;
        this.streetAddress = builder.streetAddress;
        this.locality = builder.locality;
        this.region = builder.region;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
        this.type = builder.type;
    }

    /**
     * Gets the full mailing address, formatted for display or use with a mailing label.
     *
     * @return the formatted address
     */
    public String getFormatted() {
        return formatted;
    }

    /**
     * Gets the full street address, which may include house number, street name, etc.
     *
     * @return the street address
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Gets the city or locality
     *
     * @return the city or locality
     */
    public String getLocality() {
        return locality;
    }

    /**
     * Gets the state or region
     *
     * @return region the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Gets the postal code
     *
     * @return postalCode the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets the country name in ISO 3166-1 alpha 2 format, e.g. "DE" or "US".
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Gets the type of the attribute.
     * <p/>
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
    public String getOperation() {
        return super.getOperation();
    }

    @Override
    public boolean isPrimary() {
        return super.isPrimary();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((formatted == null) ? 0 : formatted.hashCode());
        result = prime * result + ((locality == null) ? 0 : locality.hashCode());
        result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((streetAddress == null) ? 0 : streetAddress.hashCode());
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
        Address other = (Address) obj;
        if (country == null) {
            if (other.country != null) {
                return false;
            }
        } else if (!country.equals(other.country)) {
            return false;
        }
        if (formatted == null) {
            if (other.formatted != null) {
                return false;
            }
        } else if (!formatted.equals(other.formatted)) {
            return false;
        }
        if (locality == null) {
            if (other.locality != null) {
                return false;
            }
        } else if (!locality.equals(other.locality)) {
            return false;
        }
        if (postalCode == null) {
            if (other.postalCode != null) {
                return false;
            }
        } else if (!postalCode.equals(other.postalCode)) {
            return false;
        }
        if (region == null) {
            if (other.region != null) {
                return false;
            }
        } else if (!region.equals(other.region)) {
            return false;
        }
        if (streetAddress == null) {
            if (other.streetAddress != null) {
                return false;
            }
        } else if (!streetAddress.equals(other.streetAddress)) {
            return false;
        }
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
        return "Address [formatted=" + formatted + ", streetAddress=" + streetAddress + ", locality=" + locality
                + ", region=" + region + ", postalCode=" + postalCode + ", country=" + country + ", type="
                + type + ", operation=" + getOperation() + ", primary=" + isPrimary() + "]";
    }

    /**
     * Builder class that is used to build {@link Address} instances
     */
    public static class Builder extends MultiValuedAttribute.Builder {

        private String formatted;
        private String streetAddress;
        private String locality;
        private String region;
        private String postalCode;
        private String country;
        private Type type;

        public Builder() {
        }

        /**
         * builds an Builder based of the given Attribute
         *
         * @param address existing Attribute
         */
        public Builder(Address address) {
            super(address);
            formatted = address.formatted;
            streetAddress = address.streetAddress;
            locality = address.locality;
            region = address.region;
            postalCode = address.postalCode;
            country = address.country;
            type = address.type;
        }

        /**
         * Sets the full mailing address (See {@link Address#getFormatted()}).
         *
         * @param formatted the formatted address
         * @return the builder itself
         */
        public Builder setFormatted(String formatted) {
            this.formatted = formatted;
            return this;
        }

        /**
         * Sets the full street address component, (See {@link Address#getStreetAddress()}).
         *
         * @param streetAddress the street address
         * @return the builder itself
         */
        public Builder setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        /**
         * Sets the city or locality.
         *
         * @param locality the locality
         * @return the builder itself
         */
        public Builder setLocality(String locality) {
            this.locality = locality;
            return this;
        }

        /**
         * Sets the state or region.
         *
         * @param region the region
         * @return the builder itself
         */
        public Builder setRegion(String region) {
            this.region = region;
            return this;
        }

        /**
         * Sets the postal code
         *
         * @param postalCode the postal code
         * @return the builder itself
         */
        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        /**
         * Sets the label indicating the attribute's function (See {@link Address#getType()}).
         *
         * @param type the type of the attribute
         * @return the builder itself
         */
        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the country name (See {@link Address#getCountry()}).
         *
         * @param country the country
         * @return the builder itself
         */
        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        @Override
        public Builder setPrimary(boolean primary) {
            super.setPrimary(primary);
            return this;
        }

        @Override
        public Builder setOperation(String operation) {
            super.setOperation(operation);
            return this;
        }

        @Override
        public Address build() {
            return new Address(this);
        }

    }

    /**
     * Represents an address type. Canonical values are available as static constants.
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
