package org.osiam.resources.converter;

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.PhotoEntity;
import org.springframework.stereotype.Service;

@Service
public class PhotoConverter implements Converter<MultiValuedAttribute, PhotoEntity> {

    @Override
    public PhotoEntity fromScim(MultiValuedAttribute scim) {
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setType(scim.getType());
        photoEntity.setValue(String.valueOf(scim.getValue()));
        return photoEntity;
    }

    @Override
    public MultiValuedAttribute toScim(PhotoEntity entity) {
        return new MultiValuedAttribute.Builder().
                setType(entity.getType()).
                setValue(entity.getValue()).
                build();
    }

}
