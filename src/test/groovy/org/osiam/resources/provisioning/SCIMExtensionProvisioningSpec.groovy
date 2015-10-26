package org.osiam.resources.provisioning

import org.osiam.resources.provisioning.model.ExtensionDefinition
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.storage.dao.ExtensionDao
import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import spock.lang.Specification

class SCIMExtensionProvisioningSpec extends Specification {

    ExtensionDao extensionDao = Mock()
    SCIMExtensionProvisioning provisioning = new SCIMExtensionProvisioning(extensionDao)

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
        1 * extensionDao.getAllExtensions() >> [extensionEntity]
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
        1 * extensionDao.getAllExtensions() >> []
        extensionDefinitions.size() == 0
    }
}
