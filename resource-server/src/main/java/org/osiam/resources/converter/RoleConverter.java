package org.osiam.resources.converter;

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.RolesEntity;
import org.springframework.stereotype.Service;

@Service
public class RoleConverter implements Converter<MultiValuedAttribute, RolesEntity> {

    @Override
    public RolesEntity fromScim(MultiValuedAttribute scim) {
        RolesEntity rolesEntity = new RolesEntity();
        rolesEntity.setValue(String.valueOf(scim.getValue()));
        return rolesEntity;
    }

    @Override
    public MultiValuedAttribute toScim(RolesEntity entity) {
        return new MultiValuedAttribute.Builder().
                setValue(entity.getValue()).
                build();
    }

}
