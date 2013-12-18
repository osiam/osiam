package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.EntitlementConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.EntitlementsEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class EntitlementUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    EntitlementsEntity entitlementEntity = Mock()
    EntitlementConverter entitlementConverter = Mock()
    EntitlementsUpdater entitlementUpdater = new EntitlementsUpdater(entitlementConverter : entitlementConverter)

    def 'removing all entitlements is possible'() {
        when:
        entitlementUpdater.update(null, userEntity, ['entitlements'] as Set)

        then:
        1 * userEntity.removeAllEntitlements()
        userEntity.getEntitlements() >> ([
            new EntitlementsEntity(value : IRRELEVANT),
            new EntitlementsEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an entitlement is possible'(){
        given:
        MultiValuedAttribute entitlement01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        EntitlementsEntity entitlementEntity01 = new EntitlementsEntity(value : IRRELEVANT)

        when:
        entitlementUpdater.update([entitlement01] as List, userEntity, [] as Set)

        then:
        1 * entitlementConverter.fromScim(entitlement01) >> entitlementEntity01
        1 * userEntity.removeEntitlement(entitlementEntity01)
    }

    def 'adding a new entitlement is possible'(){
        given:
        MultiValuedAttribute entitlement = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        EntitlementsEntity entitlementEntity = new EntitlementsEntity(value : IRRELEVANT)

        when:
        entitlementUpdater.update([entitlement] as List, userEntity, [] as Set)

        then:
        1 * entitlementConverter.fromScim(entitlement) >> entitlementEntity
        1 * userEntity.addEntitlement(entitlementEntity)
    }

}
