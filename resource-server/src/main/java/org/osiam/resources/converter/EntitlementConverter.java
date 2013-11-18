package org.osiam.resources.converter;

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.EntitlementsEntity;
import org.springframework.stereotype.Service;

@Service
public class EntitlementConverter implements Converter<MultiValuedAttribute, EntitlementsEntity> {

    @Override
    public EntitlementsEntity fromScim(MultiValuedAttribute scim) {
        EntitlementsEntity entitlementsEntity = new EntitlementsEntity();
        entitlementsEntity.setValue(String.valueOf(scim.getValue()));
        return entitlementsEntity;
    }

    @Override
    public MultiValuedAttribute toScim(EntitlementsEntity entity) {
        return new MultiValuedAttribute.Builder().
                setValue(entity.getValue()).
                build();
    }

}
