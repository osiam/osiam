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

import org.osiam.storage.entities.GroupEntity

import spock.lang.Specification

class GroupDaoSpec extends Specification {

    static def IRRELEVANT = 'irrelevant'

    ResourceDao resourceDao = Mock()
    GroupDao groupDao = new GroupDao(resourceDao: resourceDao)

    def 'retrieving a group by id calls resourceDao.getById()'() {
        when:
        groupDao.getById(IRRELEVANT)

        then:
        1 * resourceDao.getById(IRRELEVANT, GroupEntity)
    }

    def 'creating a group calls resourceDao.create()'() {
        given:
        GroupEntity groupEntity = new GroupEntity()

        when:
        groupDao.create(groupEntity)

        then:
        1 * resourceDao.create(groupEntity)
    }

    def 'deleting a group calls resourceDao.delete()'() {
        when:
        groupDao.delete(IRRELEVANT)

        then:
        1 * resourceDao.delete(IRRELEVANT)
    }

}
