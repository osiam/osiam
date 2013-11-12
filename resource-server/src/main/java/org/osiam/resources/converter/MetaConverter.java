package org.osiam.resources.converter;

import org.osiam.resources.scim.Meta;
import org.osiam.storage.entities.MetaEntity;
import org.springframework.stereotype.Service;

@Service
public class MetaConverter implements Converter<Meta, MetaEntity>{

    @Override
    public MetaEntity fromScim(Meta scim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meta toScim(MetaEntity entity) {
        if(entity == null){
            return null;
        }
        return new Meta.Builder(entity.getCreated(), entity.getLastModified())
                         .setResourceType(entity.getResourceType())
                         .build();
    }

   

}
