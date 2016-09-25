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
package org.osiam.storage.entities;

import com.google.common.collect.ImmutableSet;
import org.hibernate.annotations.BatchSize;
import org.osiam.resources.scim.MemberRef.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for {@link org.osiam.resources.scim.Group} resources
 */
@Entity
@Table(name = "scim_group")
public class GroupEntity extends ResourceEntity {

    private static final int BATCH_SIZE = 100;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "groups", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "members", nullable = false)
    )
    @BatchSize(size = BATCH_SIZE)
    private Set<ResourceEntity> members = new HashSet<>();

    @Column(unique = true, nullable = false)
    private String displayName;

    public GroupEntity() {
        getMeta().setResourceType("Group");
    }

    public Set<ResourceEntity> getMembers() {
        return ImmutableSet.copyOf(members);
    }

    public void addMember(ResourceEntity member) {
        if (members.contains(member)) {
            return;
        }
        members.add(member);
        member.addToGroup(this);
    }

    public void removeMember(ResourceEntity member) {
        if (!members.contains(member)) {
            return;
        }
        members.remove(member);
        member.removeFromGroup(this);
    }

    /**
     * Removes all members from this group.
     */
    public void removeAllMembers() {
        Set<ResourceEntity> membersToRemove = ImmutableSet.copyOf(members);

        for (ResourceEntity member : membersToRemove) {
            removeMember(member);
        }
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "GroupEntity [displayName=" + displayName + ", getId()=" + getId() + "]";
    }

    @Override
    public Type getType() {
        return Type.GROUP;
    }

}
