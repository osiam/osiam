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

import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MemberRef;
import org.osiam.storage.dao.ResourceDao;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.ResourceEntity;
import org.springframework.stereotype.Service;

@Service
public abstract class GroupUpdater {

    @Inject
    private ResourceUpdater resourceUpdater;

    @Inject
    private ResourceDao resourceDao;

    public void update(Group group, GroupEntity groupEntity) {
        resourceUpdater.update(group, groupEntity);

        if (group.getMeta() != null && group.getMeta().getAttributes() != null) {
            removeAttributes(group.getMeta().getAttributes(), groupEntity);
        }

        if (group.getDisplayName() != null && !group.getDisplayName().isEmpty()) {
            groupEntity.setDisplayName(group.getDisplayName());
        }

        if (group.getMembers() != null && !group.getMembers().isEmpty()) {
            updateMembers(group.getMembers(), groupEntity);
        }
    }

    private void updateMembers(Set<MemberRef> members, GroupEntity groupEntity) {

        // first remove all members that have to be removed
        for (MemberRef memberRef : members) {
            if(memberRef.getOperation() == null || !memberRef.getOperation().equalsIgnoreCase("delete")) {
                continue;
            }

            String memberId = memberRef.getValue();
            ResourceEntity member = resourceDao.getById(memberId);
            groupEntity.removeMember(member);
            // TODO: delete memberRef from group
        }

        // second add all new members
        for (MemberRef memberRef : members) {
            if(memberRef.getOperation() != null && memberRef.getOperation().equalsIgnoreCase("delete")) {
                continue;
            }

            // TODO: add memberRef to Group
        }
    }

    private void removeAttributes(Set<String> attributes, GroupEntity groupEntity) {
        for (String attribute : attributes) {
            if(attribute.equalsIgnoreCase("displayName")) {
                groupEntity.setDisplayName(null);
            }
        }
    }

}
