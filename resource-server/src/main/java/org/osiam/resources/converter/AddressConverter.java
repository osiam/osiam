package org.osiam.resources.converter;

import org.osiam.resources.scim.Address;
import org.osiam.storage.entities.AddressEntity;
import org.springframework.stereotype.Service;

@Service
public class AddressConverter implements Converter<Address, AddressEntity> {

    @Override
    public AddressEntity fromScim(Address scim) {
        if (scim == null) {
            return null;
        }
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCountry(scim.getCountry());
        addressEntity.setFormatted(scim.getFormatted());
        addressEntity.setLocality(scim.getLocality());
        addressEntity.setPostalCode(scim.getPostalCode());
        addressEntity.setPrimary((scim.isPrimary() == null ? false : scim.isPrimary()));
        addressEntity.setRegion(scim.getRegion());
        addressEntity.setStreetAddress(scim.getStreetAddress());
        addressEntity.setType(scim.getType());
        return addressEntity;
    }

    @Override
    public Address toScim(AddressEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Address.Builder().
                setCountry(entity.getCountry()).
                setFormatted(entity.getFormatted()).
                setLocality(entity.getLocality()).
                setPostalCode(String.valueOf(entity.getPostalCode())).
                setRegion(entity.getRegion()).
                setStreetAddress(entity.getStreetAddress()).
                setPrimary(entity.isPrimary()).
                build();
    }

}
