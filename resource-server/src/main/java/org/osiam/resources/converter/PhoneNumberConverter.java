package org.osiam.resources.converter;

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberConverter implements Converter<MultiValuedAttribute, PhoneNumberEntity> {

    @Override
    public PhoneNumberEntity fromScim(MultiValuedAttribute scim) {
        PhoneNumberEntity phoneNumberEntity = new PhoneNumberEntity();
        phoneNumberEntity.setType(scim.getType());
        phoneNumberEntity.setValue(String.valueOf(scim.getValue()));
        return phoneNumberEntity;
    }

    @Override
    public MultiValuedAttribute toScim(PhoneNumberEntity entity) {
        return new MultiValuedAttribute.Builder().
                setType(entity.getType()).
                setValue(entity.getValue()).
                build();
    }

}
