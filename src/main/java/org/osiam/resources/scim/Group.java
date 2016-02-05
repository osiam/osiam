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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.osiam.resources.exception.SCIMDataValidationException;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represent a Group resource.
 * <p>
 * For more detailed information please look at the
 * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections 8</a>
 * </p>
 * <p>
 * client info: The scim schema is mainly meant as a connection link between the OSIAM server and by a client like the
 * connector4Java. Some values will be not accepted by the OSIAM server. These specific values have an own client info
 * documentation section.
 * </p>
 */
@JsonInclude(Include.NON_EMPTY)
public final class Group extends Resource implements Serializable {

    public static final String SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:Group";
    private static final long serialVersionUID = -2995603177584656028L;

    private final String displayName;
    private final Set<MemberRef> members;

    @JsonCreator
    private Group(@JsonProperty("id") String id,
                  @JsonProperty("externalId") String externalId,
                  @JsonProperty("meta") Meta meta,
                  @JsonProperty(value = "schemas", required = true) Set<String> schemas,
                  @JsonProperty("displayName") String displayName,
                  @JsonProperty("members") Set<MemberRef> members) {
        super(id, externalId, meta, schemas);
        this.displayName = displayName;
        this.members = (members != null ? members : Collections.<MemberRef>emptySet());
    }

    private Group(Builder builder) {
        super(builder);
        this.displayName = builder.displayName;
        this.members = builder.members;
    }

    /**
     * Gets the human readable name of this {@link Group}.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the list of members of this Group.
     * <p/>
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-8">SCIM core schema 2.0, sections
     * 8</a>
     * </p>
     *
     * @return the list of Members as a Set
     */
    public Set<MemberRef> getMembers() {
        return ImmutableSet.copyOf(members);
    }

    @Override
    public String toString() {
        return "Group [displayName=" + displayName + ", members=" + members + ", getId()=" + getId()
                + ", getExternalId()=" + getExternalId() + ", getMeta()=" + getMeta() + ", getSchemas()="
                + getSchemas() + "]";
    }

    /**
     * Builder class that is used to build {@link Group} instances
     */
    public static class Builder extends Resource.Builder {

        private String displayName;
        private Set<MemberRef> members = new HashSet<>();

        /**
         * creates a new Group.Builder based on the given displayName and group. All values of the given group will be
         * copied expect the displayName will be be overridden by the given one
         *
         * @param displayName the new displayName of the group
         * @param group       a existing group
         */
        public Builder(String displayName, Group group) {
            super(group);
            addSchema(SCHEMA);
            if (group != null) {
                this.displayName = group.displayName;
                members = group.members;
            }
            if (!Strings.isNullOrEmpty(displayName)) {
                this.displayName = displayName;
            }
        }

        /**
         * creates a new Group without a displayName
         */
        public Builder() {
            this(null, null);
        }

        /**
         * Constructs a new builder by copying all values from the given {@link Group}
         *
         * @param group {@link Group} to be copied from
         * @throws SCIMDataValidationException if the given group is null
         */
        public Builder(Group group) {
            this(null, group);
            if (group == null) {
                throw new SCIMDataValidationException("The given group can't be null.");
            }
        }

        /**
         * Constructs a new builder and sets the display name (See {@link Group#getDisplayName()}).
         *
         * @param displayName the display name
         * @throws SCIMDataValidationException if the displayName is null or empty
         */
        public Builder(String displayName) {
            this(displayName, null);
            if (displayName == null) {
                throw new SCIMDataValidationException("The given resource can't be null");
            }
        }

        @Override
        public Builder setId(String id) {
            super.setId(id);
            return this;
        }

        @Override
        public Builder setMeta(Meta meta) {
            super.setMeta(meta);
            return this;
        }

        @Override
        public Builder setExternalId(String externalId) {
            super.setExternalId(externalId);
            return this;
        }

        /**
         * @deprecated Don't use this method - let the extensions add their schema themselves. Will be removed in
         * version 1.8 or 2.0
         */
        @Override
        @Deprecated
        public Builder setSchemas(Set<String> schemas) {
            super.setSchemas(schemas);
            return this;
        }

        /**
         * Sets the list of members as {@link Set} (See {@link Group#getMembers()}).
         *
         * @param members the set of members
         * @return the builder itself
         */
        public Builder setMembers(Set<MemberRef> members) {
            this.members = members;
            return this;
        }

        /**
         * Add the given member to the set of members.
         *
         * @param member The member to add.
         * @return The builder itself
         */
        public Builder addMember(MemberRef member) {
            members.add(member);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Group build() {
            return new Group(this);
        }
    }
}
