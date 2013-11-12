package org.osiam.storage.entities

import spock.lang.Specification


class GroupMembershipSpec extends Specification {

    UserEntity user
    GroupEntity containingGroup
    GroupEntity memberGroup

    def NAME = 'irrelevant'

    def setup() {
        containingGroup = new GroupEntity(displayName: NAME)
        memberGroup = new GroupEntity(displayName: "other $NAME")
        user = new UserEntity(userName: NAME)
    }

    def 'Adding User to a Group updates both sides of the relation'() {
        when:
        user.addToGroup(containingGroup)

        then:
        containingGroup.members.contains(user)
        user.groups.contains(containingGroup)

    }

    def 'Adding Group to a Group updates both sides of the relation'() {
        when:
        memberGroup.addToGroup(containingGroup)

        then:
        containingGroup.members.contains(memberGroup)
        memberGroup.groups.contains(containingGroup)
    }

    def 'Removing User from a Group updates both sides of the relation'() {
        given:
        user.addToGroup(containingGroup)

        when:
        user.removeFromGroup(containingGroup)

        then:
        !containingGroup.members.contains(user)
        !user.groups.contains(containingGroup)
    }


    def 'Removing Group from a Group updates both sides of the relation'() {
        given:
        memberGroup.addToGroup(containingGroup)

        when:
        memberGroup.removeFromGroup(containingGroup)

        then:
        !containingGroup.members.contains(memberGroup)
        !memberGroup.groups.contains(containingGroup)
    }
}
