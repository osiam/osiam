package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.RoleConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.RolesEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class RoleUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    RolesEntity roleEntity = Mock()
    RoleConverter roleConverter = Mock()
    RoleUpdater roleUpdater = new RoleUpdater(roleConverter : roleConverter)

    def 'removing all roles is possible'() {
        when:
        roleUpdater.update(null, userEntity, ['roles'] as Set)

        then:
        1 * userEntity.removeAllRoles()
        userEntity.getRoles() >> ([
            new RolesEntity(value : IRRELEVANT),
            new RolesEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an role is possible'(){
        given:
        MultiValuedAttribute role01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        RolesEntity roleEntity01 = new RolesEntity(value : IRRELEVANT)

        when:
        roleUpdater.update([role01] as List, userEntity, [] as Set)

        then:
        1 * roleConverter.fromScim(role01) >> roleEntity01
        1 * userEntity.removeRole(roleEntity01)
    }

    def 'adding a new role is possible'(){
        given:
        MultiValuedAttribute role = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        RolesEntity roleEntity = new RolesEntity(value : IRRELEVANT)

        when:
        roleUpdater.update([role] as List, userEntity, [] as Set)

        then:
        1 * roleConverter.fromScim(role) >> roleEntity
        1 * userEntity.addRole(roleEntity)
    }

}
