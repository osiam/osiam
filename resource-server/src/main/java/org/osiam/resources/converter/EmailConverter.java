package org.osiam.resources.converter;

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.EmailEntity;
import org.springframework.stereotype.Service;

@Service
public class EmailConverter implements Converter<MultiValuedAttribute, EmailEntity> {

    @Override
    public EmailEntity fromScim(MultiValuedAttribute scim) {
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setType(scim.getType());
        emailEntity.setValue(String.valueOf(scim.getValue()));
        emailEntity.setPrimary((scim.isPrimary() == null ? false : scim.isPrimary()));
        return emailEntity;
    }

    @Override
    public MultiValuedAttribute toScim(EmailEntity entity) {
        return new MultiValuedAttribute.Builder().
                setPrimary(entity.isPrimary()).
                setType(entity.getType()).
                setValue(entity.getValue()).
                build();
    }

}
