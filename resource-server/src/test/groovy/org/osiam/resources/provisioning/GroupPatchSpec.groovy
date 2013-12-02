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
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.dao.GroupDAO
import org.osiam.storage.dao.UserDAO
import org.osiam.storage.entities.GroupEntity
import spock.lang.Specification

class GroupPatchSpec extends Specification {
    def groupDAO = Mock(GroupDAO)
    def userDAO = Mock(UserDAO)
    def groupConverter = new GroupConverter(groupDao: groupDAO, userDao: userDAO)

    SCIMGroupProvisioningBean bean = new SCIMGroupProvisioningBean(groupDAO: groupDAO, groupConverter: groupConverter)
    def uId = UUID.randomUUID()
    def id = uId.toString()
    def groupId = UUID.randomUUID()

    def "can not test behavior due to bad architecture therefor wrote some end to end tests"() {
        given:
        def members = new HashSet()
        members.add(new MultiValuedAttribute.Builder().setValue(groupId.toString()).build())
        def group = new Group.Builder()
                .setMembers(members)
                .build()

        def entity = createEntityWithInternalId()

        when:
        def result = bean.update(id, group)

        then:
        2 * groupDAO.getById(id) >> entity
        1 * userDAO.getById(_) >> null
        1 * groupDAO.getById(_) >> new GroupEntity(id: groupId, displayName: "group")
        1 * groupDAO.update(_)
        result.getMembers().size() == 1
    }

    def createEntityWithInternalId() {
        def entity = new GroupEntity()
        entity.displayName = "master of the universe"
        entity.id = uId
        entity.internalId = 1
        entity
    }
}