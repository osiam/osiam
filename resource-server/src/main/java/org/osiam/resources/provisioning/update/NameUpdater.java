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

package org.osiam.resources.provisioning.update;

import java.util.Set;

import org.osiam.resources.scim.Name;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.NameEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

/**
 * The NameUpdater provides the functionality to update the {@link NameEntity} of a UserEntity
 */
@Service
class NameUpdater {

    /**
     * updates (adds new, delete, updates) the {@link EmailEntity}'s of the given {@link UserEntity} based on the given
     * Name
     *
     * @param name
     *            {@link Name} to be deleted or updated
     * @param userEntity
     *            user who needs to be updated
     * @param attributes
     *            the complete {@link NameEntity}'s will be deleted if this Set contains 'name'
     */
    void update(Name name, UserEntity userEntity, Set<String> attributes) {

        if (attributes.contains("name")) {
            userEntity.setName(null);
        }

        ensureNameEntityIsNotNull(name, userEntity);

        updateFormatted(name, userEntity, attributes);
        updateFamilyName(name, userEntity, attributes);
        updateGivenName(name, userEntity, attributes);
        updateMiddleName(name, userEntity, attributes);
        updateHonorificPrefix(name, userEntity, attributes);
        updateHonorificSuffix(name, userEntity, attributes);
    }

    private void updateFormatted(Name name, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("name.formatted") && userEntity.getName() != null) {
            userEntity.getName().setFormatted(null);
        }

        if (name != null) {
            String formatted = name.getFormatted();
            if (!Strings.isNullOrEmpty(formatted)) {
                userEntity.getName().setFormatted(name.getFormatted());
            }
        }
    }

    private void updateFamilyName(Name name, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("name.familyName") && userEntity.getName() != null) {
            userEntity.getName().setFamilyName(null);
        }

        if (name != null) {
            String familyName = name.getFamilyName();
            if (!Strings.isNullOrEmpty(familyName)) {
                userEntity.getName().setFamilyName(name.getFamilyName());
            }
        }
    }

    private void updateGivenName(Name name, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("name.givenName") && userEntity.getName() != null) {
            userEntity.getName().setGivenName(null);
        }

        if (name != null) {
            String givenName = name.getGivenName();
            if (!Strings.isNullOrEmpty(givenName)) {
                userEntity.getName().setGivenName(name.getGivenName());
            }
        }
    }

    private void updateMiddleName(Name name, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("name.middleName") && userEntity.getName() != null) {
            userEntity.getName().setMiddleName(null);
        }

        if (name != null) {
            String middleName = name.getMiddleName();
            if (!Strings.isNullOrEmpty(middleName)) {
                userEntity.getName().setMiddleName(name.getMiddleName());
            }
        }
    }

    private void updateHonorificPrefix(Name name, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("name.honorificPrefix") && userEntity.getName() != null) {
            userEntity.getName().setHonorificPrefix(null);
        }

        if (name != null) {
            String honorificPrefix = name.getHonorificPrefix();
            if (!Strings.isNullOrEmpty(honorificPrefix)) {
                userEntity.getName().setHonorificPrefix(name.getHonorificPrefix());
            }
        }
    }

    private void updateHonorificSuffix(Name name, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("name.honorificSuffix") && userEntity.getName() != null) {
            userEntity.getName().setHonorificSuffix(null);
        }

        if (name != null) {
            String honorificSuffix = name.getHonorificSuffix();
            if (!Strings.isNullOrEmpty(honorificSuffix)) {
                userEntity.getName().setHonorificSuffix(name.getHonorificSuffix());
            }
        }
    }

    private void ensureNameEntityIsNotNull(Name name, UserEntity userEntity) {
        if (userEntity.getName() == null &&
                name != null &&
                (!Strings.isNullOrEmpty(name.getFormatted())
                        || !Strings.isNullOrEmpty(name.getFamilyName())
                        || !Strings.isNullOrEmpty(name.getGivenName())
                        || !Strings.isNullOrEmpty(name.getMiddleName())
                        || !Strings.isNullOrEmpty(name.getHonorificPrefix())
                        || !Strings.isNullOrEmpty(name.getHonorificSuffix())
                )) {

            userEntity.setName(new NameEntity());
        }
    }

}
