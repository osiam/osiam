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

package org.osiam.resources.converter

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExtensionType
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.storage.dao.ExtensionDao
import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import org.osiam.storage.entities.ExtensionFieldValueEntity
import org.osiam.storage.helper.NumberPadder
import spock.lang.Specification

class ExtensionConverterSpec extends Specification {

    private static String URN1 = "urn:org.osiam.extensions:Test01:1.0"
    private static String URN2 = "urn:org.osiam.extensions:Test02:1.0"

    private ExtensionDao extensionDao = Mock()

    private ExtensionConverter converter = new ExtensionConverter(extensionDao)

    Map fixtures = [(URN1): [
            [fieldname: 'gender', valueAsString: 'male', value: 'male', type: ExtensionFieldType.STRING],
            [fieldname: 'size', valueAsString: '1.78', value: new BigDecimal('1.78'), type: ExtensionFieldType.DECIMAL],
            [fieldname: 'numberChildren', valueAsString: '2', value: BigInteger.valueOf(2), type: ExtensionFieldType.INTEGER],
            [fieldname: 'birth', valueAsString: '2008-01-23T04:56:22.000Z',
             value    : new Date(ISODateTimeFormat.dateTime().withZoneUTC().parseDateTime("2008-01-23T04:56:22.000Z").getMillis())
             , type   : ExtensionFieldType.DATE_TIME],
            [fieldname: 'newsletter', valueAsString: 'true', value: true, type: ExtensionFieldType.BOOLEAN]
    ], (URN2)             : [
            [fieldname: 'favoredPet', valueAsString: 'doc', value: 'doc', type: ExtensionFieldType.STRING],
    ]]

    def 'convert extensionEntity set to scim extension set works'() {
        given:
        Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet = getFilledExtensionEntity(fixtures, URN1, URN2)
        Set<Extension> scimExtensionSet = getFilledScimExtension(fixtures, URN1, URN2)

        when:
        Set<Extension> extensions = converter.toScim(extensionFieldValueEntitySet)

        then:
        extensions == scimExtensionSet
    }

    def 'convert scim extension to extensionEntity works'() {
        given:
        Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet = getFilledExtensionEntity(fixtures, URN1, URN2)
        Set<Extension> scimExtensionSet = getFilledScimExtension(fixtures, URN1, URN2)
        Map<String, ExtensionEntity> extensionMap = [(URN1): createExtension(URN1), (URN2): createExtension(URN2)]

        when:
        Set<ExtensionFieldEntity> extensions = converter.fromScim(scimExtensionSet)

        then:
        1 * extensionDao.getExtensionByUrn(URN1) >> extensionMap[URN1]
        1 * extensionDao.getExtensionByUrn(URN2) >> extensionMap[URN2]
        extensions == extensionFieldValueEntitySet
    }

    def 'convert empty extensionEntity set to empty extension set works'() {
        given:
        Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet = Collections.emptySet()

        when:
        Set<Extension> extensions = converter.toScim(extensionFieldValueEntitySet)

        then:
        extensions != null
        extensions.size() == 0
    }

    def 'convert empty scim extension to empty extensionEntity works'() {
        given:
        Set<Extension> scimExtensionSet = Collections.emptySet()

        when:
        Set<ExtensionFieldEntity> extensions = converter.fromScim(scimExtensionSet)

        then:
        extensions != null
        extensions.size() == 0
    }

    private def createExtension(def urn) {
        def fixtureData = fixtures[urn]
        def fields = fixtureData.collect { new ExtensionFieldEntity(name: it.fieldname, type: it.type) }
        new ExtensionEntity(urn: urn, fields: fields as Set)
    }

    def addNameValuePairToExtensionEntity(ExtensionEntity extensionEntity, String name, String value) {
        ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity()
        fieldEntity.setName(name)

        ExtensionFieldValueEntity valueEntity = new ExtensionFieldValueEntity()
        valueEntity.setValue(value)
        valueEntity.setExtensionField(fieldEntity)

        fieldEntity.setExtension(extensionEntity)
    }

    def Set<ExtensionFieldValueEntity> getFilledExtensionEntity(Map fixtures, String... urns) {

        Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet = new HashSet<>()

        for (urn in urns) {
            ExtensionEntity entity = new ExtensionEntity()
            entity.setUrn(urn)

            def fixture = fixtures.get(urn)
            for (field in fixture) {
                ExtensionFieldValueEntity valueEntity = getFieldToEntityValueSet(entity, field.get('fieldname'), field.get('valueAsString'), field.get('type'))
                extensionFieldValueEntitySet.add(valueEntity)
            }
        }

        return extensionFieldValueEntitySet
    }

    def Set<Extension> getFilledScimExtension(Map fixtures, String... urns) {

        Set<Extension> scimExtensionSet = new HashSet<>()

        for (urn in urns) {
            Extension.Builder extension = new Extension.Builder(urn)
            def fixture = fixtures.get(urn)

            for (field in fixture) {
                extension.setField(field.get('fieldname'), field.get('value'))
            }

            scimExtensionSet.add(extension.build())
        }

        return scimExtensionSet
    }

    def ExtensionFieldValueEntity getFieldToEntityValueSet(ExtensionEntity extensionEntity, String name, String value, ExtensionFieldType type) {
        ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity()
        fieldEntity.setName(name)
        fieldEntity.setType(type)
        if (type == ExtensionFieldType.INTEGER || type == ExtensionFieldType.DECIMAL ){
            def numberPadder = new NumberPadder();
            value = numberPadder.pad(value)
        }

        ExtensionFieldValueEntity valueEntity = new ExtensionFieldValueEntity()
        valueEntity.setValue(value)
        fieldEntity.setExtension(extensionEntity)
        valueEntity.setExtensionField(fieldEntity)

        return valueEntity
    }
}
