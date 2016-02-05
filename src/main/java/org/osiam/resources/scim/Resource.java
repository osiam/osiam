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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import org.osiam.resources.exception.SCIMDataValidationException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a SCIM Resource and is the base class for {@link User}s and {@link Group}s.
 */
public abstract class Resource implements Serializable {

    private static final long serialVersionUID = 1726103518645055449L;

    private final String id;
    private final String externalId;
    private final Meta meta;
    @JsonProperty(required = true)
    private final Set<String> schemas;

    protected Resource(@JsonProperty("id") String id,
                       @JsonProperty("externalId") String externalId,
                       @JsonProperty("meta") Meta meta,
                       @JsonProperty(value = "schemas", required = true) Set<String> schemas) {
        this.id = id;
        this.externalId = externalId;
        this.meta = meta;
        if (schemas == null || schemas.isEmpty()) {
            throw new SCIMDataValidationException("Schemas cannot be null or empty!");
        }
        this.schemas = schemas;
    }

    protected Resource(Builder builder) {
        this.id = builder.id;
        this.externalId = builder.externalId;
        this.meta = builder.meta;
        this.schemas = builder.schemas;
    }

    /**
     * Gets the Id of the resource.
     *
     * @return the id of the resource
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the external Id of the resource.
     * <p>
     * For more information please look at
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-5.1">SCIM core schema 2.0, section
     * 5.1</a>
     * </p>
     *
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Gets the meta attribute
     *
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Gets the list of defined schemas
     *
     * @return a the list of schemas as a {@link Set}
     */
    public Set<String> getSchemas() {
        return ImmutableSet.copyOf(schemas);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Resource other = (Resource) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    /**
     * The Builder class is used to construct instances of the {@link Resource}
     */
    public abstract static class Builder {
        protected String externalId;
        private String id;
        private Meta meta;
        private Set<String> schemas = new HashSet<>();

        public Builder(Resource resource) {
            if (resource != null) {
                this.id = resource.id;
                this.externalId = resource.externalId;
                this.meta = resource.meta;
                this.schemas = resource.schemas;
            }
        }

        /**
         * @deprecated Don't use this method - let the extensions add their schema themselves. Will be removed in
         * version 1.8 or 2.0
         */
        @Deprecated
        public Builder setSchemas(Set<String> schemas) {
            this.schemas = schemas;
            return this;
        }

        protected void addSchema(String schema) {
            if (schemas == null) {
                schemas = new HashSet<>();
            }
            schemas.add(schema);
        }

        /**
         * Sets the id of the resource.
         * <p>
         * client info: The id of a User will be created and set by the OSIAM server. If a {@link User} or {@link Group}
         * which is send to the OSIAM server has this value filled, the value will be ignored or the action will be
         * rejected.
         * </p>
         *
         * @param id if of the resource
         * @return the builder itself
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the external id (See {@link Resource#getExternalId()}).
         *
         * @param externalId the external id
         * @return the builder itself
         */
        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * Sets the meta data
         * <p>
         * client info: The meta information of a User will be created and set by the OSIAM server. If a {@link User} or
         * {@link Group} which is send to the OSIAM server has this value filled, the value will be ignored or the
         * action will be rejected. For an update(PATCH) the attribute value can be set by the client. In normal case
         * this should be set by the {@link UpdateUser} or {@link UpdateGroup} and not by the client directly.
         * </p>
         *
         * @param meta the meta object
         * @return the builder itself
         */
        public Builder setMeta(Meta meta) {
            this.meta = meta;
            return this;
        }

        /**
         * Builds the Object of the Builder
         *
         * @return a new main Object of the Builder
         */
        public abstract <T> T build();
    }

}
