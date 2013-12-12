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

package org.osiam.storage.dao;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.query.GroupFilterParser;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDao implements GenericDao<GroupEntity> {

    private static final Logger LOGGER = Logger.getLogger(UserDao.class.getName());

    @Inject
    private GroupFilterParser filterParser;

    @Inject
    private ResourceDao resourceDao;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(GroupEntity group) {
        resourceDao.create(group);
    }

    @Override
    public GroupEntity getById(String id) {
        return resourceDao.getById(id, GroupEntity.class);
    }

    @Override
    public void delete(String id) {
        resourceDao.delete(id);
    }

    @Override
    public GroupEntity update(GroupEntity entity) {
        return resourceDao.update(entity);
    }

    @Override
    public SearchResult<GroupEntity> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        return resourceDao.search(GroupEntity.class, filter, count, startIndex, sortBy, sortOrder, filterParser);
    }

}