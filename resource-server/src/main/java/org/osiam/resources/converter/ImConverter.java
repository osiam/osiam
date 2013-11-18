package org.osiam.resources.converter;

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.ImEntity;
import org.springframework.stereotype.Service;

@Service
public class ImConverter implements Converter<MultiValuedAttribute, ImEntity> {

    @Override
    public ImEntity fromScim(MultiValuedAttribute scim) {
        ImEntity imEntity = new ImEntity();
        imEntity.setType(scim.getType());
        imEntity.setValue(String.valueOf(scim.getValue()));
        return imEntity;
    }

    @Override
    public MultiValuedAttribute toScim(ImEntity entity) {
        return new MultiValuedAttribute.Builder().
                setType(entity.getType()).
                setValue(entity.getValue()).
                build();
    }

}
