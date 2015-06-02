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

import org.osiam.resources.scim.Name;
import org.osiam.storage.entities.NameEntity;
import org.springframework.stereotype.Service;

@Service
public class NameConverter implements Converter<Name, NameEntity> {

    @Override
    public NameEntity fromScim(Name scim) {
        if (scim == null) {
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
        if (entity == null) {
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
