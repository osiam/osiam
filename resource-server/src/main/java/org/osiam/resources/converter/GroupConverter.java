package org.osiam.resources.converter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MemberRef;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.dao.GroupDAO;
import org.osiam.storage.dao.UserDAO;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.springframework.stereotype.Service;

@Service
public class GroupConverter implements Converter<Group, GroupEntity> {

    @Inject
    private GroupDAO groupDao;
    
    @Inject
    private UserDAO userDao;
    
    @Override
    public GroupEntity fromScim(Group group) {
        if(group == null){
            return null;
        }
        GroupEntity groupEntity = new GroupEntity();
        try {
            GroupEntity existingEntity = groupDao.getById(group.getId());
            groupEntity.setInternalId(existingEntity.getInternalId());
            groupEntity.setMeta(existingEntity.getMeta());
        } catch (ResourceNotFoundException ex) {
            // Safe to ignore - it's a new group
        }
        groupEntity.setId(UUID.fromString(group.getId()));
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
        } catch (ResourceNotFoundException e) {
        }
        
        if(resource == null) {
            try {
                resource = groupDao.getById(uuid);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Group member with UUID " + uuid + " not found");
            }
        }
        
        groupEntity.addMember(resource);
    }

    @Override
    public Group toScim(GroupEntity group) {     
        if(group == null){
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
