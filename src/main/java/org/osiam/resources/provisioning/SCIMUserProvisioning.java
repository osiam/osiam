/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.provisioning;

import org.antlr.v4.runtime.tree.ParseTree;
import org.osiam.resources.converter.UserConverter;
import org.osiam.resources.exception.ResourceExistsException;
import org.osiam.resources.provisioning.update.UserUpdater;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;
import org.osiam.storage.dao.SearchResult;
import org.osiam.storage.dao.UserDao;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.query.QueryFilterParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SCIMUserProvisioning implements SCIMProvisioning<User> {

    private final UserConverter userConverter;
    private final UserDao userDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserUpdater userUpdater;

    @Autowired
    public SCIMUserProvisioning(UserConverter userConverter,
                                UserDao userDao,
                                BCryptPasswordEncoder bCryptPasswordEncoder,
                                UserUpdater userUpdater) {
        this.userConverter = userConverter;
        this.userDao = userDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userUpdater = userUpdater;
    }

    @Override
    public User getById(String id) {
        UserEntity userEntity = userDao.getById(id);
        User user = userConverter.toScim(userEntity);
        return removePassword(user);
    }

    @Override
    public long count() {
        return userDao.count();
    }

    public User getByUsername(String username) {
        return removePassword(getByUsernameWithPassword(username));
    }

    public User getByUsernameWithPassword(String username) {
        return userConverter.toScim(userDao.getByUsername(username));
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

        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        userEntity.setPassword(hashedPassword);

        userDao.create(userEntity);

        return removePassword(userConverter.toScim(userEntity));
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
            String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            userEntity.setPassword(hashedPassword);
        } else {
            userEntity.setPassword(existingEntity.getPassword());
        }

        userEntity.touch();

        userEntity = userDao.update(userEntity);

        return removePassword(userConverter.toScim(userEntity));
    }

    @Override
    public SCIMSearchResult<User> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        QueryFilterParser queryFilterParser = new QueryFilterParser();
        List<User> users = new ArrayList<>();

        ParseTree filterTree = queryFilterParser.getParseTree(filter);

        SearchResult<UserEntity> result = userDao.search(filterTree, sortBy, sortOrder, count, startIndex - 1);

        for (UserEntity userEntity : result.results) {
            User scimResultUser = userConverter.toScim(userEntity);
            users.add(removePassword(scimResultUser));
        }

        return new SCIMSearchResult<>(users, result.totalResults, count, startIndex);
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

        return removePassword(userConverter.toScim(userEntity));
    }

    @Override
    public void delete(String id) {
        userDao.delete(id);
    }

    private User removePassword(User user) {
        return new User.Builder(user).setPassword(null).build();
    }
}
