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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.google.common.collect.ImmutableSet;

/**
 * Group Entity
 */
@Entity(name = "scim_group")
public class GroupEntity extends InternalIdSkeleton {

    @ManyToMany
    private Set<InternalIdSkeleton> members = new HashSet<>();

    @Column(unique = true, nullable = false)
    private String displayName;

    public GroupEntity() {
        getMeta().setResourceType("Group");
    }

    public Set<InternalIdSkeleton> getMembers() {
        return ImmutableSet.copyOf(members);
    }

    public void addMember(InternalIdSkeleton member) {
        if (members.contains(member)) {
            return;
        }
        this.members.add(member);
        member.addToGroup(this);
    }

    public void removeMember(InternalIdSkeleton member) {
        if (!members.contains(member)) {
            return;
        }
        members.remove(member);
        member.removeFromGroup(this);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GroupEntity that = (GroupEntity) o;

        return displayName.equals(that.displayName);
    }

    @Override
    public int hashCode() {
        return displayName.hashCode();
    }
}