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

import org.osiam.storage.dao.GroupDAO
import org.osiam.resources.provisioning.SCIMGroupProvisioningBean
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.User
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

class GroupPatchTest extends Specification {
    def groupDAO = Mock(GroupDAO)
    SCIMGroupProvisioningBean bean = new SCIMGroupProvisioningBean(groupDAO: groupDAO)
    def uId = UUID.randomUUID()
    def id = uId.toString()
    def groupId = UUID.randomUUID()
    def userId = UUID.randomUUID()

    def "should delete a single group in members"() {
        def members = new HashSet()
        members.add(new MultiValuedAttribute.Builder().setValue(groupId.toString()).setOperation("delete").build())
        def group = new Group.Builder()
                .setMembers(members)
                .setDisplayName("hi")
                .build()
        def entity = createEntityWithInternalId()
        addListsToEntity(entity)
        when:
        bean.update(id, group)
        then:
        1 * groupDAO.getById(id) >> entity
        entity.members.size() == 1
        entity.members.first() instanceof UserEntity
        1 * groupDAO.update(entity) >> entity
    }

    def "should delete a single user in members"() {
        def members = new HashSet()
        members.add(new MultiValuedAttribute.Builder().setValue(userId.toString()).setOperation("delete").build())
        def group = new Group.Builder()
                .setMembers(members)
                .setDisplayName("hi")
                .build()
        def entity = createEntityWithInternalId()
        addListsToEntity(entity)
        when:
        bean.update(id, group)
        then:
        1 * groupDAO.getById(id) >> entity
        entity.members.size() == 1
        entity.members.first() instanceof GroupEntity
        1 * groupDAO.update(entity) >> entity
    }


    private void addListsToEntity(GroupEntity entity) {
        entity.getMembers().add(new GroupEntity(id: groupId, displayName: "group"))
        entity.getMembers().add(new UserEntity(id: userId, displayName: "user"))
    }


    def "should delete all attributes of a multi-value-attribute list"() {
        def meta = new Meta.Builder(null, null).setAttributes(["members"] as Set).build()
        def group = new Group.Builder()
                .setMeta(meta)
                .build()

        def entity = createEntityWithInternalId()
        addListsToEntity(entity)
        when:
        bean.update(id, group)
        then:
        1 * groupDAO.getById(id) >> entity
        entity.members.empty
        1 * groupDAO.update(entity) >> entity
    }


    def "should add a multi-value to a attribute list"() {
        def members = new HashSet()
        def newUuid = UUID.randomUUID().toString()
        members.add(new MultiValuedAttribute.Builder().setValue(newUuid).setDisplay("narf").build())
        def group = new Group.Builder()
                .setMembers(members)
                .setDisplayName("hi")
                .build()
        def entity = createEntityWithInternalId()
        addListsToEntity(entity)
        when:
        bean.update(id, group)
        then:
        1 * groupDAO.getById(id) >> entity
        1 * groupDAO.update(entity) >> entity
        entity.members.size() == 3
    }

    def "should delete and add a value to a multi-value-attribute list"() {
        def members = new HashSet()
        def newUuid = UUID.randomUUID().toString()
        members.add(new MultiValuedAttribute.Builder().setValue(newUuid).setDisplay("narf").build())
        members.add(new MultiValuedAttribute.Builder().setValue(userId).setOperation("delete").build())
        def group = new Group.Builder()
                .setMembers(members)
                .setDisplayName("hi")
                .build()
        def entity = createEntityWithInternalId()
        addListsToEntity(entity)
        when:
        bean.update(id, group)
        then:
        1 * groupDAO.getById(id) >> entity
        1 * groupDAO.update(entity) >> entity
        entity.members.size() == 2
        entity.members.last() instanceof GroupEntity

    }


    def "should update a single attribute"() {
        given:
        def group = new Group.Builder().setDisplayName("hallo").build()
        def entity = createEntityWithInternalId()

        when:
        bean.update(id, group)

        then:
        1 * groupDAO.getById(id) >> entity
        1 * groupDAO.update(entity) >> entity
        entity.displayName == "hallo"
    }

    def createEntityWithInternalId() {
        def entity = new GroupEntity()
        entity.displayName = "master of the universe"
        entity.id = uId
        entity
    }



    def "should ignore when trying to delete required parameters"() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["displayName"] as Set).build()
        def user = new User.Builder().setMeta(meta).build()
        def entity = createEntityWithInternalId()

        when:
        bean.update(id, user)
        then:
        1 * groupDAO.getById(id) >> entity
        entity.displayName == "master of the universe"
        1 * groupDAO.update(entity) >> entity
    }

    def "should delete and update simple attributes"() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["members"] as Set).build()

        def group = new Group.Builder().setDisplayName("harald").setMeta(meta).build()
        def entity = createEntityWithInternalId()
        addListsToEntity(entity)
        when:
        bean.update(id, group)
        then:
        1 * groupDAO.getById(id) >> entity
        entity.displayName == "harald"
        entity.members.empty
        1 * groupDAO.update(entity) >> entity

    }

    def "should ignore update attribute is in meta"() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["members"] as Set).build()

        def members = new HashSet()
        members.add(new MultiValuedAttribute.Builder().setValue(UUID.randomUUID().toString()).build())

        def user = new Group.Builder().setMembers(members).setMeta(meta).build()
        def entity = createEntityWithInternalId()
        entity.setDisplayName("display it")
        addListsToEntity(entity)
        when:
        bean.update(id, user)
        then:
        1 * groupDAO.getById(id) >> entity
        entity.getMembers().empty
        1 * groupDAO.update(entity) >> entity
    }


    def "should ignore read-only attributes on modify"() {
        given:
        def group = new Group.Builder().setId(UUID.randomUUID().toString()).build()
        def entity = createEntityWithInternalId()
        def oldUuid = entity.getId()
        when:
        def result = bean.update(id, group)
        then:
        1 * groupDAO.getById(id) >> entity
        result.id == oldUuid.toString()
        1 * groupDAO.update(entity) >> entity
    }

    def "should ignore read-only attributes on delete"() {
        given:
        def meta = new Meta.Builder().setAttributes(["id"] as Set).build()
        def group = new Group.Builder().setMeta(meta).build()
        def entity = createEntityWithInternalId()
        def oldUuid = entity.getId()
        when:
        def result = bean.update(id, group)
        then:
        1 * groupDAO.getById(id) >> entity
        result.id == oldUuid.toString()
        1 * groupDAO.update(entity) >> entity

    }

    def "should set Meta.lastModified to actual date on update resource"() {
        given:
        def group = new Group.Builder().setDisplayName("hallo").build()
        def entity = createEntityWithInternalId()

        def lastModified = entity.getMeta().getLastModified()

        when:
        bean.update(id, group)

        then:
        1 * groupDAO.getById(id) >> entity
        1 * groupDAO.update(entity) >> entity
        entity.displayName == "hallo"
        lastModified <= entity.getMeta().getLastModified()
    }

    def "should set Meta.lastModified to actual date on replace resource"() {
        given:
        def group = new Group.Builder().setDisplayName("hallo").build()
        def entity = createEntityWithInternalId()

        def lastModified = entity.getMeta().getLastModified()

        when:
        bean.replace(id, group)

        then:
        1 * groupDAO.getById(id) >> entity
        1 * groupDAO.update(entity) >> entity
        entity.displayName == "hallo"
        lastModified <= entity.getMeta().getLastModified()
    }
}
