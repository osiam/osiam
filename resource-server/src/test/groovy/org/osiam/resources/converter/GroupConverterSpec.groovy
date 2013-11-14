package org.osiam.resources.converter

import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.dao.GroupDAO
import org.osiam.storage.dao.UserDAO
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

class GroupConverterSpec extends Specification {

    Map fixtures = [displayName: 'displayName',
            externalId: 'externalId'
    ]

    def memberUuidFixtures = [
            user: 'c2b4f6d0-7028-4f8d-b78a-bb4d3eadc5bd',
            group: '0c60179c-0b9c-4cad-a918-4ec59bbab173',
            'non-existant': 'e75522d8-2b4d-4b60-bb92-6c6cda8faaea']

    private GroupDAO groupDao = Mock()
    private UserDAO userDao = Mock()

    GroupConverter groupConverter = new GroupConverter(groupDao: groupDao, userDao: userDao)

    def 'mapping a user as member works'() {
        given:
        def uuid = UUID.randomUUID()
        Group group = getFilledGroupWithMember(uuid, memberUuidFixtures['user'])
        groupDao.getById(uuid.toString()) >> { throw new ResourceNotFoundException('') }

        when:
        GroupEntity GroupEntity = groupConverter.fromScim(group)

        then:
        1 * userDao.getById(memberUuidFixtures['user']) >> new UserEntity(id: UUID.fromString(memberUuidFixtures['user']))
        GroupEntity.getMembers().size() == 1
    }

    def 'mapping a group as a member works'() {
        given:
        def uuid = UUID.randomUUID()
        Group group = getFilledGroupWithMember(uuid, memberUuidFixtures['group'])
        groupDao.getById(uuid.toString()) >> { throw new ResourceNotFoundException('') }

        when:
        GroupEntity GroupEntity = groupConverter.fromScim(group)

        then:
        1 * userDao.getById(memberUuidFixtures['group']) >> { throw new ResourceNotFoundException('') }
        1 * groupDao.getById(memberUuidFixtures['group']) >> new GroupEntity(id: UUID.fromString(memberUuidFixtures['group']))
        GroupEntity.getMembers().size() == 1
    }

    def 'mapping a non-existant resource as a member raises exception'() {
        given:
        def uuid = UUID.randomUUID()
        Group group = getFilledGroupWithMember(uuid, memberUuidFixtures['non-existant'])
        groupDao.getById(uuid.toString()) >> { throw new ResourceNotFoundException('') }

        when:
        GroupEntity GroupEntity = groupConverter.fromScim(group)

        then:
        1 * userDao.getById(memberUuidFixtures['non-existant']) >> { throw new ResourceNotFoundException('') }
        1 * groupDao.getById(memberUuidFixtures['non-existant']) >> { throw new ResourceNotFoundException('') }
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
                meta: meta)

        group.addMember(member)

        return group
    }

    def Group getFilledGroup(UUID uuid) {

        Group.Builder groupBuilder = new Group.Builder(fixtures)

        groupBuilder.setId(uuid.toString())

        return groupBuilder.build()
    }

    def Group getFilledGroupWithMember(def uuid, def memberUuid) {
        Group group = getFilledGroup(uuid)

        MultiValuedAttribute member = new MultiValuedAttribute(value: memberUuid)

        group.members.add(member)

        return group
    }
}
