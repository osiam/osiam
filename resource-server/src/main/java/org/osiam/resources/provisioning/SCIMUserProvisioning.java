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

package org.osiam.resources.provisioning;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.osiam.resources.converter.UserConverter;
import org.osiam.resources.exceptions.ResourceExistsException;
import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.resources.provisioning.update.UserUpdater;
import org.osiam.resources.scim.Constants;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;
import org.osiam.storage.dao.SearchResult;
import org.osiam.storage.dao.UserDao;
import org.osiam.storage.entities.UserEntity;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SCIMUserProvisioning implements SCIMProvisioning<User> {

    private static final Logger LOGGER = Logger.getLogger(SCIMUserProvisioning.class.getName());

    @Inject
    private UserConverter userConverter;

    @Inject
    private UserDao userDao;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserUpdater userUpdater;

    @Override
    public User getById(String id) {
        try {
            return removePassword(userConverter.toScim(userDao.getById(id)));
        } catch (NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);

            throw new ResourceNotFoundException(String.format("User with id '%s' not found", id), nre);
        } catch (PersistenceException pe) {
            LOGGER.log(Level.WARNING, pe.getMessage(), pe);

            throw new ResourceNotFoundException(String.format("User with id '%s' not found", id), pe);
        }
    }

    @Override
    public User create(User user) {
        if (userDao.isUserNameAlreadyTaken(user.getUserName())) {
            throw new ResourceExistsException(String.format(
                    "Can't create a user. The username \"%s\" is already taken.", user.getUserName()));
        }
        if (userDao.isExternalIdAlreadyTaken(user.getExternalId())) {
            throw new ResourceExistsException(String.format(
                    "Can't create a user. The externalId \"%s\" is already taken.", user.getExternalId()));
        }
        UserEntity userEntity = userConverter.fromScim(user);
        userEntity.setId(UUID.randomUUID());

        String hashedPassword = passwordEncoder.encodePassword(user.getPassword(), userEntity.getId());
        userEntity.setPassword(hashedPassword);

        userDao.create(userEntity);

        User result = removePassword(userConverter.toScim(userEntity));

        return result;
    }

    @Override
    public User replace(String id, User user) {
        UserEntity existingEntity = userDao.getById(id);

        if (userDao.isUserNameAlreadyTaken(user.getUserName(), id)) {
            throw new ResourceExistsException(String.format(
                    "Can't replace the user with the id \"%s\". The username \"%s\" is already taken.", id,
                    user.getUserName()));
        }
        if (userDao.isExternalIdAlreadyTaken(user.getExternalId(), id)) {
            throw new ResourceExistsException(String.format(
                    "Can't replace the user with the id \"%s\". The externalId \"%s\" is already taken.", id,
                    user.getExternalId()));
        }

        UserEntity userEntity = userConverter.fromScim(user);

        userEntity.setInternalId(existingEntity.getInternalId());
        userEntity.setMeta(existingEntity.getMeta());
        userEntity.setId(existingEntity.getId());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encodePassword(user.getPassword(), userEntity.getId());
            userEntity.setPassword(hashedPassword);
        } else {
            userEntity.setPassword(existingEntity.getPassword());
        }

        userEntity.touch();

        userEntity = userDao.update(userEntity);

        User result = removePassword(userConverter.toScim(userEntity));

        return result;
    }

    @Override
    public SCIMSearchResult<User> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        List<User> users = new ArrayList<>();

        // Decrease startIndex by 1 because scim pagination starts at 1 and JPA doesn't
        SearchResult<UserEntity> result = userDao.search(filter, sortBy, sortOrder, count, startIndex - 1);

        for (UserEntity userEntity : result.results) {
            User scimResultUser = userConverter.toScim(userEntity);
            users.add(removePassword(scimResultUser));
        }

        return new SCIMSearchResult<>(users, result.totalResults, count, startIndex, Constants.USER_CORE_SCHEMA);
    }

    @Override
    public User update(String id, User user) {
        UserEntity userEntity = userDao.getById(id);

        if (userDao.isUserNameAlreadyTaken(user.getUserName(), id)) {
            throw new ResourceExistsException(String.format(
                    "Can't update the user with the id \"%s\". The username \"%s\" is already taken.", id,
                    user.getUserName()));
        }
        if (userDao.isExternalIdAlreadyTaken(user.getExternalId(), id)) {
            throw new ResourceExistsException(String.format(
                    "Can't update the user with the id \"%s\". The externalId \"%s\" is already taken.", id,
                    user.getExternalId()));
        }

        userUpdater.update(user, userEntity);

        userEntity.touch();

        User result = removePassword(userConverter.toScim(userEntity));

        return result;
    }

    @Override
    public void delete(String id) {
        try {
            userDao.delete(id);
        } catch (NoResultException nre) {
            throw new ResourceNotFoundException(String.format("User with id '%s' not found", id), nre);
        }
    }

    private User removePassword(User user) {
        return new User.Builder(user).setPassword(null).build();
    }

}
