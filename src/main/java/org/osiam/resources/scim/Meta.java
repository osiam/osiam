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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.osiam.resources.helper.JsonDateSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the meta data of a resource.
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02">SCIM core schema 2.0</a>
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class Meta implements Serializable {

    private static final long serialVersionUID = -4536271487921469946L;

    @JsonSerialize(using = JsonDateSerializer.class)
    private final Date created;
    @JsonSerialize(using = JsonDateSerializer.class)
    private final Date lastModified;
    private final String location;
    private final String version;
    private final Set<String> attributes;
    private final String resourceType;

    public Meta(@JsonProperty("created") @JsonSerialize(using = JsonDateSerializer.class) Date created,
                @JsonProperty("lastModified") @JsonSerialize(using = JsonDateSerializer.class) Date lastModified,
                @JsonProperty("location") String location,
                @JsonProperty("version") String version,
                @JsonProperty("attributes") Set<String> attributes,
                @JsonProperty("resourceType") String resourceType) {
        this.created = created;
        this.lastModified = lastModified;
        this.location = location;
        this.version = version;
        this.attributes = attributes;
        this.resourceType = resourceType;
    }

    private Meta(Builder builder) {
        this.created = builder.created;
        this.lastModified = builder.lastModified;
        this.attributes = builder.attributes;
        this.location = builder.location;
        this.version = builder.version;
        this.resourceType = builder.resourceType;
    }

    /**
     * Gets the URI of the Resource being returned.
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5">SCIM core schema 2.0, section 5</a>
     * </p>
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the version of the Resource being returned.
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5">SCIM core schema 2.0, section 5</a>
     * </p>
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the attributes to be deleted from the Resource
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5">SCIM core schema 2.0, section 5</a>
     * </p>
     *
     * @return a set of attributes to be deleted
     */
    public Set<String> getAttributes() {
        return attributes;
    }

    /**
     * Gets the date when the {@link Resource} was created
     *
     * @return the creation date
     */
    public Date getCreated() {
        if (created != null) {
            return new Date(created.getTime());
        }
        return null;
    }

    /**
     * Gets the date when the {@link Resource} was last modified
     *
     * @return the last modified date
     */
    public Date getLastModified() {
        if (lastModified != null) {
            return new Date(lastModified.getTime());
        }
        return null;
    }

    /**
     * Gets the type of the Resource (User or Group)
     *
     * @return the type of the actual resource
     */
    public String getResourceType() {
        return resourceType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        Meta other = (Meta) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (lastModified == null) {
            if (other.lastModified != null) {
                return false;
            }
        } else if (!lastModified.equals(other.lastModified)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (resourceType == null) {
            if (other.resourceType != null) {
                return false;
            }
        } else if (!resourceType.equals(other.resourceType)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    /**
     * Builder class that is used to build {@link Meta} instances
     */
    public static class Builder {
        private final Date created;
        private final Date lastModified;
        private String location;
        private String version;
        private Set<String> attributes = new HashSet<>();
        private String resourceType;

        /**
         * Constructs a new builder with the created and last modified time set to the current time
         */
        public Builder() {
            this.created = new Date(System.currentTimeMillis());
            this.lastModified = this.created;
        }

        /**
         * Will set created to given value and lastModified to System.currentTime Only be used by the server. Will be
         * ignored by PUT and PATCH operations
         */
        public Builder(Date created, Date lastModified) {
            this.created = created != null ? new Date(created.getTime()) : null;
            this.lastModified = lastModified != null ? new Date(lastModified.getTime()) : null;
        }

        /**
         * Constructs a new builder with the created and last modified time set to the given values
         *
         * @param meta the meta object to copy from
         */
        public Builder(Meta meta) {
            if (meta == null) {
                throw new IllegalArgumentException("The given Meta can't be null");
            }
            this.created = meta.created;
            this.lastModified = meta.lastModified;
            this.location = meta.location;
            this.version = meta.version;
            this.attributes = meta.attributes;
            this.resourceType = meta.resourceType;
        }

        /**
         * Set the location (See {@link Meta#getLocation()}).
         *
         * @param location the resource uri
         * @return the builder itself
         */
        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        /**
         * Sets the version of the Resource (See {@link Meta#getVersion()}).
         *
         * @param version the version of the resource
         * @return the builder itself
         */
        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Sets the type of the Resource (See {@link Meta#getResourceType()}).
         *
         * @param resourceType the type
         * @return the builder itself
         */
        public Builder setResourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        /**
         * Sets the names of the attributes to be removed from the Resource.
         *
         * @param attributes name of attributes to be deleted
         * @return the builder itself
         */
        public Builder setAttributes(Set<String> attributes) {
            this.attributes = attributes;
            return this;
        }

        /**
         * Builds a Meta Object with the given parameters
         *
         * @return a new Meta Object
         */
        public Meta build() {
            return new Meta(this);
        }
    }
}
