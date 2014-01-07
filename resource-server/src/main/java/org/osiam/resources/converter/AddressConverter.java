/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
        addressEntity.setType(new Address.Type(scim.getType()));
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
