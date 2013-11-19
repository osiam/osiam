package org.osiam.resources.provisioning

import org.osiam.resources.converter.GroupConverter
import org.osiam.resources.exceptions.ResourceExistsException
import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MemberRef
import org.osiam.storage.dao.GroupDao
import org.osiam.storage.entities.GroupEntity
import org.springframework.dao.DataIntegrityViolationException
import spock.lang.Specification

class GroupCreateSpec extends Specification {

    GroupDao groupDao = Mock()
    GroupConverter groupConverter = Mock()

    SCIMGroupProvisioningBean underTest = new SCIMGroupProvisioningBean(groupDAO: groupDao, groupConverter: groupConverter)

    Group group
    GroupEntity groupEntity = Mock()

    String groupUuid = UUID.randomUUID().toString()
    String memberId = UUID.randomUUID().toString()

    def setup() {
        group = new Group.Builder()
                .setDisplayName('irrelevant')
                .setId(groupUuid)
                .build();
    }

    def 'create a group with a non-existant member raises exception'() {
        given:
        MemberRef member = new MemberRef.Builder()
                .setValue(memberId)
                .build()
        group = new Group.Builder(group)
                .setMembers([member] as Set)
                .build();

        when:
        underTest.create(group)

        then:
        1 * groupConverter.fromScim(_) >> { throw new ResourceNotFoundException('') }
        thrown(ResourceNotFoundException)
    }

    def 'creating an existant group raises exception'() {
        when:
        underTest.create(group)

        then:
        1 * groupConverter.fromScim(group) >> groupEntity
        1 * groupDao.create(groupEntity) >> { throw new DataIntegrityViolationException('') }
        thrown(ResourceExistsException)
    }

    def 'creating a group works as expected'() {
        when:
        underTest.create(group)

        then:
        1 * groupConverter.fromScim(group) >> groupEntity
        1 * groupEntity.setId(_)
        1 * groupDao.create(groupEntity)
        1 * groupConverter.toScim(groupEntity)
    }

}
