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

import org.osiam.resources.converter.EmailConverter
import org.osiam.resources.converter.ExtensionConverter;
import org.osiam.resources.scim.Email
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.ExtensionFieldValueEntity
import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity
import org.osiam.storage.entities.ExtensionFieldValueEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.helper.NumberPadder;

import spock.lang.Specification


class ExtensionUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    ExtensionDao extensionDao = Mock()
    NumberPadder numberPadder = Mock()
    ExtensionEntity extensionEntity = Mock()
    ExtensionConverter extensionConverter = Mock()
    ExtensionUpdater extensionUpdater = new ExtensionUpdater(extensionDao : extensionDao, numberPadder: numberPadder)
    
    static String FIELD = 'foo'
    static String VALUE = 'bar'

    static String FIELD_INJECTED = 'injected'
    static ExtensionFieldType DEFAULT_FIELD_TYPE = ExtensionFieldType.STRING
    static String URN = 'irrelevant'
    
    def extension
    
//    def 'adding a new extension with field is possible'(){
//        given:
//        extensionWithValue()
//        ExtensionFieldEntity extensionFieldEntity = new ExtensionFieldEntity(name: FIELD, type: ExtensionFieldType.STRING)
//        ExtensionFieldValueEntity extensionFieldValueEntity = new ExtensionFieldValueEntity(value : IRRELEVANT)
//        extensionFieldValueEntity.extensionField = extensionFieldEntity
//        ExtensionEntity extensionEntity = new ExtensionEntity()
//        extensionEntity.fields = [extensionFieldEntity] as Set
//
//        when:
//        extensionUpdater.update(['irrelevant': extension] as Map, userEntity, [] as Set)
//
//        then:
//        1 * extensionConverter.fromScim(extension) >> extensionEntity
//        1 * userEntity.addExtensionFieldValue(extensionFieldValueEntity)
//    }

    def 'removing all extensions is possible'() {
        when:
        extensionUpdater.update(null, userEntity, ['extensions'] as Set)

        then:
        1 * userEntity.removeAllExtensionFieldValues();
        userEntity.getExtensionFieldValues() >> ([
            new ExtensionFieldValueEntity(value : IRRELEVANT),
            new ExtensionFieldValueEntity(value : IRRELEVANT_02)] as Set)
    }

//    def 'removing an extension is possible'(){
//        given:
//        Extension email01 = new Extension.Field()
//        ExtensionFieldValueEntity extensionEntity01 = new ExtensionFieldValueEntity(value : IRRELEVANT)
//
//        when:
//        extensionUpdater.update([email01] as List, userEntity, [] as Set)
//
//        then:
//        1 * extensionConverter.fromScim(email01) >> extensionEntity01
//        1 * userEntity.removeEmail(extensionEntity01)
//    }

    private extensionWithValue() {
        extension = new Extension(URN)
        extension.addOrUpdateField(FIELD, VALUE)
    }
}
