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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.exceptions.OsiamException;
import org.osiam.resources.scim.User;
import org.osiam.storage.entities.UserEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Service
public class UserUpdater {

    @Inject
    private ResourceUpdater resourceUpdater;

    @Inject
    private NameUpdater nameUpdater;

    public void update(User user, UserEntity userEntity) {
        resourceUpdater.update(user, userEntity);

        Set<String> attributes = new HashSet<>();
        if (user.getMeta() != null && user.getMeta().getAttributes() != null) {
            attributes = user.getMeta().getAttributes();
        }

        updateUserName(user.getUserName(), userEntity, attributes);
        nameUpdater.update(user.getName(), userEntity, attributes);

        if (user.isActive() != null) {
            userEntity.setActive(user.isActive());
        }
    }

    private void updateUserName(String userName, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("userName")) {
            throw new OsiamException("Attribute 'userName' cannot be deleted.");
        }

        if (!Strings.isNullOrEmpty(userName)) {
            // TODO: check if userName already taken?
            userEntity.setUserName(userName);
        }
    }

}
