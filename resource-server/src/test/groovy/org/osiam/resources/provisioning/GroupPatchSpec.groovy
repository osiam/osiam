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

package org.osiam.resources.provisioning

import org.osiam.resources.converter.GroupConverter
import org.osiam.resources.scim.Group
import org.osiam.storage.dao.GroupDao
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.MetaEntity
import spock.lang.Specification

class GroupPatchSpec extends Specification {

    def groupDao = Mock(GroupDao)
    def groupConverter = Mock(GroupConverter)
    SCIMGroupProvisioningBean groupProvisioningBean = new SCIMGroupProvisioningBean(groupDao: groupDao, groupConverter: groupConverter)

    def uId = UUID.randomUUID()
    def id = uId.toString()
    def groupId = UUID.randomUUID()

    def "only checking call hierarchy due to implementation logic encapsulation in th super class. therefor added integration tests"() {
        given:
        def group = new Group.Builder().setDisplayName("test").build()
        def entityMock = Mock(GroupEntity)

        when:
        groupProvisioningBean.update(id, group)

        then:
        1 * groupConverter.fromScim(_) >> entityMock
        2 * groupDao.getById(id) >> entityMock
        1 * entityMock.getId() >> groupId
        1 * entityMock.getInternalId() >> 123
        1 * entityMock.getMeta() >> Mock(MetaEntity)
        1 * groupDao.update(_) >> entityMock
        2 * groupConverter.toScim(_) >> Mock(Group)
    }
}