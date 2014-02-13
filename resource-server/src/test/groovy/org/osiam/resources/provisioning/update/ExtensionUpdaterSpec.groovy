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

import org.osiam.resources.converter.ExtensionConverter
import org.osiam.resources.exceptions.NoSuchElementException
import org.osiam.resources.exceptions.OsiamException
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.storage.dao.ExtensionDao
import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import org.osiam.storage.entities.ExtensionFieldValueEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class ExtensionUpdaterSpec extends Specification {

    static String FIELD = 'field'
    static String VALUE = 'value'
    static String URN = 'extension'

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    ExtensionEntity extensionEntity = Mock()
    ExtensionConverter extensionConverter = Mock()
    ExtensionDao extensionDao = Mock()
    ExtensionUpdater extensionUpdater = new ExtensionUpdater(extensionDao : extensionDao)

    def 'removing an extension is possible'(){
        when:
        extensionUpdater.update(null, userEntity, [URN] as Set)

        then:
        1 * extensionDao.getExtensionByUrn(URN, true) >> createExtensionEntity()
        1 * userEntity.removeAllExtensionFieldValues(URN)
    }

    def 'removing an extension field is possible'(){
        when:
        extensionUpdater.update(null, userEntity, [URN + "." + FIELD] as Set)

        then:
        1 * extensionDao.getExtensionByUrn(URN, true) >> createExtensionEntity()
        1 * userEntity.getExtensionFieldValues() >> ([getExtensionValueEntity()] as Set)
        1 * userEntity.removeExtensionFieldValue(_)
    }

    def 'updating a not registered extension raises an exception'(){
        given:
        Extension extension = extensionWithValue()

        when:
        extensionUpdater.update([(URN) : extension] as Map, userEntity, [] as Set)

        then:
        1 * extensionDao.getExtensionByUrn(URN) >> { throw new OsiamException() }
        thrown(OsiamException)
    }

    def 'updating a not registered extension field raises an exception'(){
        given:
        
        Extension extension = new Extension(URN)
        extension.addOrUpdateField(IRRELEVANT, VALUE)
        ExtensionEntity extensionEntity = createExtensionEntity()

        when:
        extensionUpdater.update([(URN) : extension] as Map, userEntity, [] as Set)

        then:
        1 * extensionDao.getExtensionByUrn(URN) >> extensionEntity
        thrown(NoSuchElementException)
    }

    def 'updating an exisitng extension field is possible'(){
        given:
        Extension extension = extensionWithValue()
        ExtensionEntity extensionEntity = createExtensionEntity()

        when:
        extensionUpdater.update([(URN) : extension] as Map, userEntity, [] as Set)

        then:
        1 * extensionDao.getExtensionByUrn(URN) >> extensionEntity
        1 * userEntity.getExtensionFieldValues() >> ([getExtensionValueEntity()] as Set)
        1 * userEntity.addOrUpdateExtensionValue(_)
    }
    
    def 'updating an non exisitng extension field is possible'(){
        given:
        Extension extension = extensionWithValue()
        ExtensionEntity extensionEntity = createExtensionEntity()

        when:
        extensionUpdater.update([(URN) : extension] as Map, userEntity, [] as Set)

        then:
        1 * extensionDao.getExtensionByUrn(URN) >> extensionEntity
        1 * userEntity.getExtensionFieldValues() >> ([] as Set)
        1 * userEntity.addOrUpdateExtensionValue(_)
    }

    def createExtensionEntity() {
        ExtensionFieldEntity extensionFieldEntity = new ExtensionFieldEntity(name: FIELD, type: ExtensionFieldType.STRING)
        ExtensionFieldValueEntity extensionFieldValueEntity = getExtensionValueEntity()
        extensionFieldValueEntity.extensionField = extensionFieldEntity
        ExtensionEntity extensionEntity = new ExtensionEntity()
        extensionEntity.fields = [extensionFieldEntity] as Set
        extensionEntity.urn = URN
        return extensionEntity
    }

    private extensionWithValue() {
        Extension extension = new Extension(URN)
        extension.addOrUpdateField(FIELD, VALUE)
        return extension
    }

    private getExtensionValueEntity(){
        ExtensionEntity extensionEntity = new ExtensionEntity()
        extensionEntity.urn = URN
        ExtensionFieldEntity extensionField = new ExtensionFieldEntity(extension : extensionEntity, name: FIELD, type: ExtensionFieldType.STRING)
        new ExtensionFieldValueEntity(extensionField : extensionField, value : VALUE)
    }
}
