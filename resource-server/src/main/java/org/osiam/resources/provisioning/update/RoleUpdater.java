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

import com.google.common.base.Strings;
import org.osiam.resources.converter.RoleConverter;
import org.osiam.resources.scim.Role;
import org.osiam.storage.entities.RoleEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * The RoleUpdater provides the functionality to update the {@link RoleEntity} of a UserEntity
 */
@Service
class RoleUpdater {

    private RoleConverter roleConverter;

    @Autowired
    public RoleUpdater(RoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    /**
     * updates (adds new, delete, updates) the {@link RoleEntity}'s of the given {@link UserEntity} based on the given
     * List of Role's
     *
     * @param roles      list of Role's to be deleted, updated or added
     * @param userEntity user who needs to be updated
     * @param attributes all {@link RoleEntity}'s will be deleted if this Set contains 'roles'
     */
    void update(List<Role> roles, UserEntity userEntity, Set<String> attributes) {

        if (attributes.contains("roles")) {
            userEntity.removeAllRoles();
        }

        if (roles != null) {
            for (Role scimRole : roles) {
                RoleEntity roleEntity = roleConverter.fromScim(scimRole);
                userEntity.removeRole(roleEntity); // we always have to remove the role in case
                // the primary attribute has changed
                if (Strings.isNullOrEmpty(scimRole.getOperation())
                        || !scimRole.getOperation().equalsIgnoreCase("delete")) {

                    ensureOnlyOnePrimaryRoleExists(roleEntity, userEntity.getRoles());
                    userEntity.addRole(roleEntity);
                }
            }
        }
    }

    /**
     * if the given newRole is set to primary the primary attribute of all existing role's in the {@link UserEntity}
     * will be removed
     *
     * @param newRole to be checked if it is primary
     * @param roles   all existing role's of the {@link UserEntity}
     */
    private void ensureOnlyOnePrimaryRoleExists(RoleEntity newRole, Set<RoleEntity> roles) {
        if (newRole.isPrimary()) {
            for (RoleEntity exisitngRoleEntity : roles) {
                if (exisitngRoleEntity.isPrimary()) {
                    exisitngRoleEntity.setPrimary(false);
                }
            }
        }
    }

}
