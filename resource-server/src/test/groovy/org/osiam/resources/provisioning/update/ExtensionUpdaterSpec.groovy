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
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.storage.dao.ExtensionDao
import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import org.osiam.storage.entities.ExtensionFieldValueEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.helper.NumberPadder

import spock.lang.Ignore;
import spock.lang.Specification


class ExtensionUpdaterSpec extends Specification {

    UserEntity userEntity = Mock()
    ExtensionDao extensionDao = Mock()
    NumberPadder numberPadder = Mock()
    ExtensionConverter extensionConverter = Mock()
    ExtensionUpdater extensionUpdater = new ExtensionUpdater(extensionDao : extensionDao, numberPadder: numberPadder)
    
    static String FIELD = 'field'
    static String VALUE = 'value'
    static String URN = 'extension'
    
    @Ignore('Not yet implemented')
    def 'call update triggers extensionDao and updateExtension'(){
        given:
        Extension extension = extensionWithValue()
        ExtensionEntity extensionEntity = createExtensionEntity()

        when:
        extensionUpdater.update([(URN): extension] as Map, userEntity, [] as Set)

        then:
        //1 * extensionUpdater.updateExtension([(URN): extension] as Map, userEntity)
        //1 * userEntity.addExtensionFieldValue()
        1 * extensionDao.getExtensionByUrn(URN) >> extensionEntity
    }

    def 'removing all extensions is possible'() {
        
    }

    def createExtensionEntity() {
        ExtensionFieldEntity extensionFieldEntity = new ExtensionFieldEntity(name: FIELD, type: ExtensionFieldType.STRING)
        ExtensionFieldValueEntity extensionFieldValueEntity = new ExtensionFieldValueEntity(value : VALUE)
        extensionFieldValueEntity.extensionField = extensionFieldEntity
        ExtensionEntity extensionEntity = new ExtensionEntity()
        extensionEntity.fields = [extensionFieldEntity] as Set
        extensionEntity.urn = URN;
        return extensionEntity
    }
    
    private extensionWithValue() {
        Extension extension = new Extension(URN)
        extension.addOrUpdateField(FIELD, VALUE)
        return extension
    }
}
