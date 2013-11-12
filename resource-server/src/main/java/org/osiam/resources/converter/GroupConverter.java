package org.osiam.resources.converter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.dao.GroupDAO;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.entities.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class GroupConverter implements Converter<Group, GroupEntity> {

    @Inject
    private GroupDAO groupDao;
    
    @Override
    public GroupEntity fromScim(Group scim) {
        if(scim == null){
            return null;
        }
        GroupEntity groupEntity = new GroupEntity();
        try {
            GroupEntity existingEntity = groupDao.getById(scim.getId());
            groupEntity.setInternalId(existingEntity.getInternalId());
            groupEntity.setMeta(existingEntity.getMeta());
        } catch (NoResultException ex) {
            // Just It's a new one.
        }
        groupEntity.setId(UUID.fromString(scim.getId()));
        groupEntity.setDisplayName(scim.getDisplayName());
        groupEntity.setExternalId(scim.getExternalId());
        
        return groupEntity;
    }

    @Override
    public Group toScim(GroupEntity entity) {     
        if(entity == null){
            return null;
        }
        Group.Builder groupBuilder = new Group.Builder()
                .setDisplayName(entity.getDisplayName())
                .setId(entity.getId().toString())
                .setMeta(entity.getMeta().toScim())
                .setExternalId(entity.getExternalId());

        return groupBuilder.build();
    }


}
