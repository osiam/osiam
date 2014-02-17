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

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.collect.ImmutableSet;

@Entity
@Table(name = "scim_id")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ResourceEntity {
    
    @Column(unique = true, nullable = false)
    private String id;

    @Id
    @GeneratedValue
    @Column(name = "internal_id")
    private long internalId;

    @Column(unique = true)
    private String externalId;

    @OneToOne(cascade = CascadeType.ALL)
    private MetaEntity meta = new MetaEntity(GregorianCalendar.getInstance());

    @ManyToMany(mappedBy = "members")
    private Set<GroupEntity> groups = new HashSet<>();

    public UUID getId() {
        return UUID.fromString(id != null ? id : "");
    }

    public void setId(UUID id) {
        this.id = id.toString();
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    /**
     * @return the external id
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId the external id
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public abstract String getDisplayName();

    /**
     * Update the last modified date for this entity.
     */
    public void touch() {
        getMeta().setLastModified(GregorianCalendar.getInstance().getTime());
    }

    public Set<GroupEntity> getGroups() {
        return ImmutableSet.copyOf(groups);
    }

    public void addToGroup(GroupEntity group) {
        if (groups.contains(group)) {
            return;
        }
        groups.add(group);
        group.addMember(this);
    }

    public void removeFromGroup(GroupEntity group) {
        if (!groups.contains(group)) {
            return;
        }
        groups.remove(group);
        group.removeMember(this);
    }

    @Transient
    public String getValue() {
        return id;
    }

    public void setValue(String value) {
        id = value;
    }

    public MetaEntity getMeta() {
        return meta;
    }

    public void setMeta(MetaEntity meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "ResourceEntity [id=" + id + ", internalId=" + internalId + ", getClass()=" + getClass() + "]";
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
        ResourceEntity other = (ResourceEntity) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
