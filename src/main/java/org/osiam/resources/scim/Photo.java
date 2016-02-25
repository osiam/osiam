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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.osiam.resources.data.ImageDataURI;
import org.osiam.resources.data.PhotoValueType;
import org.osiam.resources.exception.SCIMDataValidationException;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class represents a photo attribute.
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-3.2">SCIM core schema 2.0, section 3.2</a>
 * </p>
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public final class Photo extends MultiValuedAttribute implements Serializable {

    private static final long serialVersionUID = -3801047382575408796L;

    private Type type;

    /**
     * Constructor for deserialization, it is not intended for general use.
     */
    @JsonCreator
    public Photo(@JsonProperty("operation") String operation,
                 @JsonProperty("value") String value,
                 @JsonProperty("display") String display,
                 @JsonProperty("primary") boolean primary,
                 @JsonProperty("$ref") String reference,
                 @JsonProperty("type") Type type) {
        super(operation, value, display, primary, reference);
        this.type = type;
    }

    private Photo(Builder builder) {
        super(builder);
        this.type = builder.type;
    }

    @Override
    public String getOperation() {
        return super.getOperation();
    }

    /**
     * the value of the photo as URI. Check first with {@link Photo#getValueType()} if the type is
     * {@link PhotoValueType#URI}
     *
     * @return returns the value of the photo as URI
     */
    public URI getValueAsURI() {
        URI uri;
        try {
            uri = new URI(super.getValue());
        } catch (URISyntaxException e) {
            throw new SCIMDataValidationException(e.getMessage(), e);
        }
        return uri;
    }

    /**
     * the value of the photo as {@link ImageDataURI}. Check first with {@link Photo#getValueType()} if the type is
     * {@link PhotoValueType#IMAGE_DATA_URI}
     *
     * @return the value of the photo as {@link ImageDataURI}
     */
    @JsonIgnore
    public ImageDataURI getValueAsImageDataURI() {
        return new ImageDataURI(super.getValue());
    }

    /**
     * @return the type of the saved photo value
     */
    @JsonIgnore
    public PhotoValueType getValueType() {
        if (super.getValue().startsWith("data:image/") && super.getValue().contains(";base64,")) {
            try {
                getValueAsImageDataURI();
                return PhotoValueType.IMAGE_DATA_URI;
            } catch (Exception e) {
            }
        }
        try {
            getValueAsURI();
            return PhotoValueType.URI;
        } catch (Exception e) {
        }
        return PhotoValueType.UNKNOWN;
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
        Photo other = (Photo) obj;
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
        return "Photo [value=" + getValue() + ", type=" + type + ", primary=" + isPrimary()
                + ", operation=" + getOperation() + "]";
    }

    /**
     * Builder class that is used to build {@link Photo} instances
     */
    public static class Builder extends MultiValuedAttribute.Builder {

        private Type type;

        public Builder() {
        }

        /**
         * builds an Builder based of the given Attribute
         *
         * @param photo existing Attribute
         */
        public Builder(Photo photo) {
            super(photo);
            type = photo.type;
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
         * an URI pointing to an image
         *
         * @param uri a image URI
         * @return the Builder itself
         */
        public Builder setValue(URI uri) {
            super.setValue(uri.toString());
            return this;
        }

        /**
         * an imageDataURI which contains a small in data image. For performance issues it is recommend to to store big
         * pictures as ImageDataURI
         *
         * @param image a image
         * @return the Builder itself
         */
        public Builder setValue(ImageDataURI image) {
            super.setValue(image.toString());
            return this;
        }

        /**
         * Sets the label indicating the attribute's function (See {@link Photo#getType()}).
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
        public Photo build() {
            return new Photo(this);
        }
    }

    /**
     * Represents a photo type. Canonical values are available as static constants.
     */
    public static class Type extends MultiValuedAttributeType {
        public static final Type PHOTO = new Type("photo");
        public static final Type THUMBNAIL = new Type("thumbnail");

        public Type(String value) {
            super(value);
        }
    }

}
