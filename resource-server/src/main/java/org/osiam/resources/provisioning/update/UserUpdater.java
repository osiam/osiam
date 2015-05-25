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

import org.osiam.resources.exceptions.OsiamException;
import org.osiam.resources.exceptions.ResourceExistsException;
import org.osiam.resources.scim.User;
import org.osiam.storage.dao.UserDao;
import org.osiam.storage.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

/**
 * The UserUpdater provides the functionality to update (patch) a {@link UserEntity}
 */
@Service
public class UserUpdater {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ResourceUpdater resourceUpdater;

    @Autowired
    private NameUpdater nameUpdater;

    @Autowired
    private EmailUpdater emailUpdater;

    @Autowired
    private PhoneNumberUpdater phoneNumberUpdater;

    @Autowired
    private ImUpdater imUpdater;

    @Autowired
    private PhotoUpdater photoUpdater;

    @Autowired
    private EntitlementsUpdater entitlementUpdater;

    @Autowired
    private RoleUpdater roleUpdater;

    @Autowired
    private X509CertificateUpdater x509CertificateUpdater;

    @Autowired
    private AddressUpdater addressUpdater;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ExtensionUpdater extensionUpdater;

    /**
     * updates the {@link UserEntity} based on the given {@link User}
     *
     * @param user
     *            {@link User} with all fields that needs to be updated
     * @param userEntity
     *            entity that needs to be updated
     */
    public void update(User user, UserEntity userEntity) {
        resourceUpdater.update(user, userEntity);

        Set<String> attributes = new HashSet<>();
        if (user.getMeta() != null && user.getMeta().getAttributes() != null) {
            attributes = user.getMeta().getAttributes();
        }

        updateUserName(user, userEntity, attributes);
        nameUpdater.update(user.getName(), userEntity, attributes);
        updateDisplayName(user, userEntity, attributes);
        updateNickName(user, userEntity, attributes);
        updateProfileUrl(user, userEntity, attributes);
        updateTitle(user, userEntity, attributes);
        updateUserType(user, userEntity, attributes);
        updatePreferredLanguage(user, userEntity, attributes);
        updateLocale(user, userEntity, attributes);
        updateTimezone(user, userEntity, attributes);
        updateActive(user, userEntity, attributes);
        updatePassword(user, userEntity, attributes);
        emailUpdater.update(user.getEmails(), userEntity, attributes);
        phoneNumberUpdater.update(user.getPhoneNumbers(), userEntity, attributes);
        imUpdater.update(user.getIms(), userEntity, attributes);
        photoUpdater.update(user.getPhotos(), userEntity, attributes);
        addressUpdater.update(user.getAddresses(), userEntity, attributes);
        updateGroups(user, attributes);
        entitlementUpdater.update(user.getEntitlements(), userEntity, attributes);
        roleUpdater.update(user.getRoles(), userEntity, attributes);
        x509CertificateUpdater.update(user.getX509Certificates(), userEntity, attributes);
        extensionUpdater.update(user.getExtensions(), userEntity, attributes);
    }

    private void updateUserName(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("userName")) {
            throw new OsiamException("Attribute 'userName' cannot be deleted.");
        }
        String userName = user.getUserName();

        if (!Strings.isNullOrEmpty(userName)) {

            if (userDao.isUserNameAlreadyTaken(userName, userEntity.getId().toString())) {
                throw new ResourceExistsException(String.format("User with userName '%s' already exists", userName));
            }
            userEntity.setUserName(userName);
        }
    }

    private void updateDisplayName(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("displayName")) {
            userEntity.setDisplayName(null);
        }

        if (!Strings.isNullOrEmpty(user.getDisplayName())) {
            userEntity.setDisplayName(user.getDisplayName());
        }
    }

    private void updateNickName(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("nickName")) {
            userEntity.setNickName(null);
        }

        if (!Strings.isNullOrEmpty(user.getNickName())) {
            userEntity.setNickName(user.getNickName());
        }
    }

    private void updateProfileUrl(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("profileUrl")) {
            userEntity.setProfileUrl(null);
        }

        if (!Strings.isNullOrEmpty(user.getProfileUrl())) {
            userEntity.setProfileUrl(user.getProfileUrl());
        }
    }

    private void updateTitle(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("title")) {
            userEntity.setTitle(null);
        }

        if (!Strings.isNullOrEmpty(user.getTitle())) {
            userEntity.setTitle(user.getTitle());
        }
    }

    private void updateUserType(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("userType")) {
            userEntity.setUserType(null);
        }

        if (!Strings.isNullOrEmpty(user.getUserType())) {
            userEntity.setUserType(user.getUserType());
        }
    }

    private void updatePreferredLanguage(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("preferredLanguage")) {
            userEntity.setPreferredLanguage(null);
        }

        if (!Strings.isNullOrEmpty(user.getPreferredLanguage())) {
            userEntity.setPreferredLanguage(user.getPreferredLanguage());
        }
    }

    private void updateLocale(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("locale")) {
            userEntity.setLocale(null);
        }

        if (!Strings.isNullOrEmpty(user.getLocale())) {
            userEntity.setLocale(user.getLocale());
        }
    }

    private void updateTimezone(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("timezone")) {
            userEntity.setTimezone(null);
        }

        if (!Strings.isNullOrEmpty(user.getTimezone())) {
            userEntity.setTimezone(user.getTimezone());
        }
    }

    private void updateActive(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("active")) {
            userEntity.setActive(null);
        }

        if (user.isActive() != null) {
            userEntity.setActive(user.isActive());
        }
    }

    private void updatePassword(User user, UserEntity userEntity, Set<String> attributes) {
        if (attributes.contains("password")) {
            throw new OsiamException("Attribute 'password' cannot be deleted.");
        }

        if (!Strings.isNullOrEmpty(user.getPassword())) {
            String hashedPassword = passwordEncoder.encodePassword(user.getPassword(), userEntity.getId());
            userEntity.setPassword(hashedPassword);
        }
    }

    /**
     * the update of the relationship of a user to his group will be done through the group. If the User provides any
     * information about patching the group we will throw an exception
     *
     * @param user
     *            the {@link User} is not allowed to contain any groups
     * @param attributes
     *            the list is not allowed to contain 'groups'
     */
    private void updateGroups(User user, Set<String> attributes) {
        if (attributes.contains("groups")
                || user.getGroups().size() > 0) {
            throw new OsiamException("The membership to a group can't be modified. Please use the group update.");
        }
    }

}
