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

package org.osiam.resources.provisioning.update;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MemberRef;
import org.osiam.storage.dao.ResourceDao;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.ResourceEntity;
import org.springframework.stereotype.Service;

@Service
public class GroupUpdater {

    @Inject
    private ResourceUpdater resourceUpdater;

    @Inject
    private ResourceDao resourceDao;

    public void update(Group group, GroupEntity groupEntity) {
        resourceUpdater.update(group, groupEntity);

        Set<String> attributes = new HashSet<>();
        if (group.getMeta() != null && group.getMeta().getAttributes() != null) {
            attributes = group.getMeta().getAttributes();
        }

        if (group.getDisplayName() != null && !group.getDisplayName().isEmpty()) {
            groupEntity.setDisplayName(group.getDisplayName());
        }

        updateMembers(group, groupEntity, attributes);

    }

    private void updateMembers(Group group, GroupEntity groupEntity, Set<String> attributes) {
        String attributeName = "members";

        for (String attribute : attributes) {
            if (attribute.equalsIgnoreCase(attributeName)) {
                groupEntity.removeAllMembers();
            }
        }

        if (group.getMembers() != null) {
            for (MemberRef memberRef : group.getMembers()) {
                String memberId = memberRef.getValue();

                if (memberRef.getOperation() != null && memberRef.getOperation().equalsIgnoreCase("delete")) {
                    ResourceEntity member = getMember(memberId, groupEntity);

                    if(member != null) {
                        groupEntity.removeMember(member);
                    }
                } else {
                    // TODO: what to do if member does not exist?
                    ResourceEntity member = resourceDao.getById(memberId, ResourceEntity.class);

                    groupEntity.addMember(member);
                }
            }
        }

    }

    private ResourceEntity getMember(String memberId, GroupEntity groupEntity) {
        Set<ResourceEntity> members = groupEntity.getMembers();

        for (ResourceEntity member : members) {
            if(member.getId().toString().equals(memberId)) {
                return member;
            }
        }

        return null;
    }

}
