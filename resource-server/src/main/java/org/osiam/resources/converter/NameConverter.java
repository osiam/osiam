package org.osiam.resources.converter;

import org.osiam.resources.scim.Name;
import org.osiam.storage.entities.NameEntity;
import org.springframework.stereotype.Service;

@Service
public class NameConverter implements Converter<Name, NameEntity>{

    @Override
    public NameEntity fromScim(Name scim) {
        if(scim == null){
            return null;
        }
        NameEntity nameEntity = new NameEntity();
        nameEntity.setFamilyName(scim.getFamilyName());
        nameEntity.setFormatted(scim.getFormatted());
        nameEntity.setGivenName(scim.getGivenName());
        nameEntity.setHonorificPrefix(scim.getHonorificPrefix());
        nameEntity.setHonorificSuffix(scim.getHonorificSuffix());
        nameEntity.setMiddleName(scim.getMiddleName());
        return nameEntity;
    }

    @Override
    public Name toScim(NameEntity entity) {
        if(entity == null){
            return null;
        }
        return new Name.Builder().
                setFamilyName(entity.getFamilyName()).
                setFormatted(entity.getFormatted()).
                setGivenName(entity.getGivenName()).
                setHonorificPrefix(entity.getHonorificPrefix()).
                setHonorificSuffix(entity.getHonorificSuffix()).
                setMiddleName(entity.getMiddleName()).
                build();
    }

}
