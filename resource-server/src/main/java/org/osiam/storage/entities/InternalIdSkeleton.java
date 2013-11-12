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

import org.osiam.resources.scim.MultiValuedAttribute;

import javax.persistence.*;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "scim_id")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({@NamedQuery(name = "getById", query = "SELECT i FROM scim_id i WHERE i.id= :id")})
public abstract class InternalIdSkeleton implements ChildOfMultiValueAttribute, Serializable {

    private static final long serialVersionUID = -5890750191971717942L;

    @Column(unique = true, nullable = false)
    private String id;

    @Id
    @GeneratedValue
    @Column(name = "internal_id")
    private long internalId;

    @Column(unique = true)
    private String externalId;

    @ManyToOne(cascade = CascadeType.ALL)
    private MetaEntity meta = new MetaEntity(GregorianCalendar.getInstance());

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "members")
    private Set<GroupEntity> groups = new HashSet<>();

    public UUID getId() {
        return UUID.fromString(id);
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

    public MultiValuedAttribute toMultiValueScim() {
        return new MultiValuedAttribute.Builder().setDisplay(getDisplayName()).setValue(id).build();
    }

    /**
     * Update the last modified date for this entity.
     */
    public void touch() {
        getMeta().setLastModified(GregorianCalendar.getInstance().getTime());
    }

    public Set<GroupEntity> getGroups() {
        return groups;
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

    @Override
    @Transient
    public String getValue() {
        return id;
    }

    @Override
    public void setValue(String value) {
        id = value;
    }

    public MetaEntity getMeta() {
        return meta;
    }

    public void setMeta(MetaEntity meta) {
        this.meta = meta;
    }

    public abstract <T> T toScim();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalIdSkeleton)) {
            return false;
        }

        InternalIdSkeleton that = (InternalIdSkeleton) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
