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

import org.osiam.resources.converter.EntitlementConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.EntitlementEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class EntitlementUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    EntitlementEntity entitlementEntity = Mock()
    EntitlementConverter entitlementConverter = Mock()
    EntitlementsUpdater entitlementUpdater = new EntitlementsUpdater(entitlementConverter : entitlementConverter)

    def 'removing all entitlements is possible'() {
        when:
        entitlementUpdater.update(null, userEntity, ['entitlements'] as Set)

        then:
        1 * userEntity.removeAllEntitlements()
        userEntity.getEntitlements() >> ([
            new EntitlementEntity(value : IRRELEVANT),
            new EntitlementEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an entitlement is possible'(){
        given:
        MultiValuedAttribute entitlement01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        EntitlementEntity entitlementEntity01 = new EntitlementEntity(value : IRRELEVANT)

        when:
        entitlementUpdater.update([entitlement01] as List, userEntity, [] as Set)

        then:
        1 * entitlementConverter.fromScim(entitlement01) >> entitlementEntity01
        1 * userEntity.removeEntitlement(entitlementEntity01)
    }

    def 'adding a new entitlement is possible'(){
        given:
        MultiValuedAttribute entitlement = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        EntitlementEntity entitlementEntity = new EntitlementEntity(value : IRRELEVANT)

        when:
        entitlementUpdater.update([entitlement] as List, userEntity, [] as Set)

        then:
        1 * entitlementConverter.fromScim(entitlement) >> entitlementEntity
        1 * userEntity.addEntitlement(entitlementEntity)
    }
}
