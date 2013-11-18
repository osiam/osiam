package org.osiam.storage.entities

import spock.lang.Specification


class GroupMembershipSpec extends Specification {

    UserEntity user
    GroupEntity containingGroup
    GroupEntity memberGroup

    def NAME = 'irrelevant'

    def setup() {
        containingGroup = new GroupEntity(displayName: NAME, id: UUID.randomUUID())
        memberGroup = new GroupEntity(displayName: "other $NAME", id: UUID.randomUUID())
        user = new UserEntity(userName: NAME, id: UUID.randomUUID())
    }

    def 'Adding a User to a Group via the User updates both sides of the relation'() {
        when:
        user.addToGroup(containingGroup)

        then:
        containingGroup.members.contains(user)
        user.groups.contains(containingGroup)

    }

    def 'Adding User to a Group via the Group updates both sides of the relation'() {
        when:
        containingGroup.addMember(user)

        then:
        user.groups.contains(containingGroup)
        containingGroup.members.contains(user)
    }

    def 'Adding Group to a Group via the member Group updates both sides of the relation'() {
        when:
        memberGroup.addToGroup(containingGroup)

        then:
        containingGroup.members.contains(memberGroup)
        memberGroup.groups.contains(containingGroup)
    }

    def 'Adding Group to a Group via the containing Group updates both sides of the relation'() {
        when:
        containingGroup.addMember(memberGroup)

        then:
        memberGroup.groups.contains(containingGroup)
        containingGroup.members.contains(memberGroup)
    }

    def 'Removing User from a Group via the User updates both sides of the relation'() {
        given:
        user.addToGroup(containingGroup)

        when:
        user.removeFromGroup(containingGroup)

        then:
        !containingGroup.members.contains(user)
        !user.groups.contains(containingGroup)
    }

    def 'Removing User from a Group via the Group updates both sides of the relation'() {
        given:
        containingGroup.addMember(user)

        when:
        containingGroup.removeMember(user)

        then:
        !user.groups.contains(containingGroup)
        !containingGroup.members.contains(user)
    }

    def 'Removing Group from a Group via the member Group updates both sides of the relation'() {
        given:
        memberGroup.addToGroup(containingGroup)

        when:
        memberGroup.removeFromGroup(containingGroup)

        then:
        !containingGroup.members.contains(memberGroup)
        !memberGroup.groups.contains(containingGroup)
    }

    def 'Removing Group from a Group via the containing Group updates both sides of the relation'() {
        given:
        containingGroup.addMember(memberGroup)

        when:
        containingGroup.removeMember(memberGroup)

        then:
        !memberGroup.groups.contains(containingGroup)
        !containingGroup.members.contains(memberGroup)
    }
}
