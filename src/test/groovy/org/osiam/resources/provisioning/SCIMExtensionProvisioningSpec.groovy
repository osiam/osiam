/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.provisioning

import org.osiam.resources.provisioning.model.ExtensionDefinition
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.storage.ExtensionRepository
import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import spock.lang.Specification

class SCIMExtensionProvisioningSpec extends Specification {

    ExtensionRepository extensionRepository = Mock()
    SCIMExtensionProvisioning provisioning = new SCIMExtensionProvisioning(extensionRepository)

    def 'get extension definitions'() {
        given:
        ExtensionEntity extensionEntity = new ExtensionEntity()
        extensionEntity.setUrn('urn')

        ExtensionFieldEntity extensionFieldEntity = new ExtensionFieldEntity()
        extensionFieldEntity.setExtension(extensionEntity)
        extensionFieldEntity.setName('test1')
        extensionFieldEntity.setType(ExtensionFieldType.STRING)

        ExtensionFieldEntity extensionFieldEntity2 = new ExtensionFieldEntity()
        extensionFieldEntity2.setExtension(extensionEntity)
        extensionFieldEntity2.setName('test2')
        extensionFieldEntity2.setType(ExtensionFieldType.BOOLEAN)

        Set<ExtensionFieldEntity> extensionFieldEntities = [extensionFieldEntity, extensionFieldEntity2]
        extensionEntity.setFields(extensionFieldEntities)

        when:
        List<ExtensionDefinition> extensionDefinitions = provisioning.getAllExtensionDefinitions()

        then:
        1 * extensionRepository.findAll() >> [extensionEntity]
        extensionDefinitions.size() == 1
        extensionDefinitions.get(0).urn == 'urn'
        extensionDefinitions.get(0).getNamedTypePairs().containsKey('test1')
        extensionDefinitions.get(0).getNamedTypePairs().containsValue('STRING')
        extensionDefinitions.get(0).getNamedTypePairs().containsKey('test2')
        extensionDefinitions.get(0).getNamedTypePairs().containsValue('BOOLEAN')
    }

    def 'get empty extension definitions when no entry in database'() {
        when:
        List<ExtensionDefinition> extensionDefinitions = provisioning.getAllExtensionDefinitions()

        then:
        1 * extensionRepository.findAll() >> []
        extensionDefinitions.size() == 0
    }
}
