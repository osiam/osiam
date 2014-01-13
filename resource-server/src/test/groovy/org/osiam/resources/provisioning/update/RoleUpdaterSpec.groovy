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

package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.RoleConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.RoleEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class RoleUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    RoleEntity roleEntity = Mock()
    RoleConverter roleConverter = Mock()
    RoleUpdater roleUpdater = new RoleUpdater(roleConverter : roleConverter)

    def 'removing all roles is possible'() {
        when:
        roleUpdater.update(null, userEntity, ['roles'] as Set)

        then:
        1 * userEntity.removeAllRoles()
        userEntity.getRoles() >> ([
            new RoleEntity(value : IRRELEVANT),
            new RoleEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an role is possible'(){
        given:
        MultiValuedAttribute role01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        RoleEntity roleEntity01 = new RoleEntity(value : IRRELEVANT)

        when:
        roleUpdater.update([role01] as List, userEntity, [] as Set)

        then:
        1 * roleConverter.fromScim(role01) >> roleEntity01
        1 * userEntity.removeRole(roleEntity01)
    }

    def 'adding a new role is possible'(){
        given:
        MultiValuedAttribute role = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        RoleEntity roleEntity = new RoleEntity(value : IRRELEVANT)

        when:
        roleUpdater.update([role] as List, userEntity, [] as Set)

        then:
        1 * roleConverter.fromScim(role) >> roleEntity
        1 * userEntity.addRole(roleEntity)
    }
}
