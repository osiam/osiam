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

package org.osiam.storage.dao

import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.Query

import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.ResourceEntity
import org.osiam.storage.entities.RoleEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.query.UserFilterParser

import spock.lang.Specification

class UserDaoSpec extends Specification {

    static def IRRELEVANT = 'irrelevant'

    ResourceDao resourceDao = Mock()
    UserDao userDao = new UserDao(resourceDao: resourceDao)

    def 'retrieving a user by id calls resourceDao.getById()'() {
        when:
        userDao.getById(IRRELEVANT)

        then:
        1 * resourceDao.getById(IRRELEVANT, UserEntity)
    }

    def 'retrieving a user by userName calls resourceDao.getByAttribute()'() {
        when:
        userDao.getByUsername(IRRELEVANT)

        then:
        // The meta model attribute is null, because it is initialized by the JPA provider
        1 * resourceDao.getByAttribute(null, IRRELEVANT, UserEntity)
    }

    def 'creating a user calls resourceDao.create()'() {
        given:
        UserEntity userEntity = new UserEntity()

        when:
        userDao.create(userEntity)

        then:
        1 * resourceDao.create(userEntity)
    }

    def 'deleting a user calls resourceDao.delete()'() {
        when:
        userDao.delete(IRRELEVANT)

        then:
        1 * resourceDao.delete(IRRELEVANT)
    }

}