package org.osiam.resources.converter;

import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MemberRef;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.dao.GroupDao;
import org.osiam.storage.dao.UserDao;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Service
public class GroupConverter implements Converter<Group, GroupEntity> {

    @Inject
    private GroupDao groupDao;

    @Inject
    private UserDao userDao;

    @Override
    public GroupEntity fromScim(Group group) {
        if (group == null) {
            return null;
        }
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setDisplayName(group.getDisplayName());
        groupEntity.setExternalId(group.getExternalId());

        for (MultiValuedAttribute member : group.getMembers()) {
            addMember(member, groupEntity);
        }

        return groupEntity;
    }

    private void addMember(MultiValuedAttribute member, GroupEntity groupEntity) {
        String uuid = member.getValue();

        InternalIdSkeleton resource = null;

        try {
            resource = userDao.getById(uuid);
        } catch (ResourceNotFoundException ignored) {
            // this exception is safe to ignore
        }

        if (resource == null) {
            try {
                resource = groupDao.getById(uuid);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Group member with UUID " + uuid + " not found"); // NOSONAR : Don't preserve stack trace here
            }
        }

        groupEntity.addMember(resource);
    }

    @Override
    public Group toScim(GroupEntity group) {
        if (group == null) {
            return null;
        }
        Group.Builder groupBuilder = new Group.Builder()
                .setDisplayName(group.getDisplayName())
                .setId(group.getId().toString())
                .setMeta(group.getMeta().toScim())
                .setExternalId(group.getExternalId());

        Set<MemberRef> members = new HashSet<>();
        for (InternalIdSkeleton member : group.getMembers()) {
            MemberRef memberRef = new MemberRef.Builder()
                    .setValue(member.getId().toString())
                    .setReference(member.getMeta().getLocation())
                    .setDisplay(member.getDisplayName() != null ? member.getDisplayName() : null)
                    .build();
            members.add(memberRef);
        }
        groupBuilder.setMembers(members);

        return groupBuilder.build();
    }
}
