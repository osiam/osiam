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

package org.osiam.resources.converter

import org.osiam.resources.exception.ResourceNotFoundException
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MemberRef
import org.osiam.storage.dao.ResourceDao
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.ResourceEntity
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

class GroupConverterSpec extends Specification {

    static def IRRELEVANT = 'irrelevant'
    Map fixtures = [displayName: 'displayName',
                    externalId : 'externalId'
    ]

    def memberUuidFixtures = [
            user          : 'c2b4f6d0-7028-4f8d-b78a-bb4d3eadc5bd',
            group         : '0c60179c-0b9c-4cad-a918-4ec59bbab173',
            'non-existant': 'e75522d8-2b4d-4b60-bb92-6c6cda8faaea']

    private ResourceDao resourceDao = Mock()

    MetaConverter metaConverter = Mock()
    GroupConverter groupConverter = new GroupConverter(resourceDao: resourceDao, metaConverter: metaConverter)

    def 'mapping a user as member works'() {
        given:
        def uuid = UUID.randomUUID()
        Group group = getFilledGroupWithMember(uuid, memberUuidFixtures['user'])

        when:
        GroupEntity GroupEntity = groupConverter.fromScim(group)

        then:
        1 * resourceDao.getById(memberUuidFixtures['user'], ResourceEntity.class) >> new UserEntity(id: UUID.fromString(memberUuidFixtures['user']), userName: IRRELEVANT)
        GroupEntity.getMembers().size() == 1
    }

    def 'mapping a group as a member works'() {
        given:
        def uuid = UUID.randomUUID()
        Group group = getFilledGroupWithMember(uuid, memberUuidFixtures['group'])

        when:
        GroupEntity GroupEntity = groupConverter.fromScim(group)

        then:
        1 * resourceDao.getById(memberUuidFixtures['group'], ResourceEntity.class) >> new GroupEntity(id: UUID.fromString(memberUuidFixtures['group']), displayName: IRRELEVANT)
        GroupEntity.getMembers().size() == 1
    }

    def 'mapping a non-existant resource as a member raises exception'() {
        given:
        def uuid = UUID.randomUUID()
        Group group = getFilledGroupWithMember(uuid, memberUuidFixtures['non-existant'])

        when:
        GroupEntity GroupEntity = groupConverter.fromScim(group)

        then:
        1 * resourceDao.getById(memberUuidFixtures['non-existant'], ResourceEntity.class) >> {
            throw new ResourceNotFoundException('')
        }
        thrown(ResourceNotFoundException)
    }

    def 'converting group entity with member to scim works'() {
        given:
        def uuid = UUID.randomUUID()
        GroupEntity groupEntity = getFilledGroupEntityWithMember(uuid, 'location')

        when:
        Group group = groupConverter.toScim(groupEntity)

        then:
        group.getMembers().size() == 1
        group.getMembers().first().getReference() == 'location'
    }

    def 'converting group entity to scim works as expected'() {
        given:
        def uuid = UUID.randomUUID()

        GroupEntity groupEntity = getFilledGroupEntity(uuid)

        when:
        def group = groupConverter.toScim(groupEntity)

        then:
        group.id == uuid.toString()
        group.displayName == fixtures["displayName"]
        group.externalId == fixtures["externalId"]
    }

    def 'converting scim group to entity works as expected'() {
        given:
        def uuid = UUID.randomUUID()

        Group group = getFilledGroup(uuid)

        when:
        def groupEntity = groupConverter.fromScim(group)

        then:
        groupEntity.displayName == fixtures["displayName"]
        groupEntity.externalId == fixtures["externalId"]
    }

    def 'converting null scim group returns null'() {
        expect:
        groupConverter.fromScim(null) == null
    }

    def 'converting null group entity returns null'() {
        expect:
        groupConverter.toScim(null) == null
    }

    def GroupEntity getFilledGroupEntity(def uuid) {

        GroupEntity groupEntity = new GroupEntity(fixtures)

        groupEntity.setId(uuid)

        return groupEntity
    }

    def GroupEntity getFilledGroupEntityWithMember(def uuid, def location) {
        GroupEntity group = getFilledGroupEntity(uuid)

        MetaEntity meta = new MetaEntity(Calendar.instance)
        meta.setLocation(location)

        UserEntity member = new UserEntity(
                id: UUID.fromString(memberUuidFixtures['user']),
                displayName: 'user',
                userName: 'irrelevant',
                meta: meta)

        group.addMember(member)

        return group
    }

    def Group getFilledGroup(UUID uuid) {

        return new Group.Builder(fixtures)
                .setId(uuid as String)
                .build()
    }

    def Group getFilledGroupWithMember(def uuid, def memberUuid) {

        return new Group.Builder(fixtures)
                .setId(uuid as String)
                .addMember(new MemberRef.Builder(value: memberUuid).build())
                .build()
    }
}
