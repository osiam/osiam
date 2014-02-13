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
