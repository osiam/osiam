package org.osiam.resources.converter

import java.util.Collections;

import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.entities.extension.ExtensionEntity
import org.osiam.storage.entities.extension.ExtensionFieldEntity
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity

import spock.lang.Specification

class ExtensionConverterSpec extends Specification {

    private static String URN1 = "urn:org.osiam.extensions:Test01:1.0"
    private static String URN2 = "urn:org.osiam.extensions:Test02:1.0"
    
    List fixtures1 = [
            [fieldname:'gender', valueAsString: 'male', value: 'male' , type: ExtensionFieldType.STRING],
            [fieldname:'size', valueAsString: '1.78', value: new BigDecimal('1.78') , type: ExtensionFieldType.DECIMAL],
            [fieldname:'numberChildren', valueAsString: '2', value: BigInteger.valueOf(2) , type: ExtensionFieldType.INTEGER],
            [fieldname:'birth', valueAsString: '2008-01-23T04:56:22.000Z', 
                value: new Date(ISODateTimeFormat.dateTime().withZoneUTC().parseDateTime("2008-01-23T04:56:22.000Z").getMillis())
                                                                                          , type: ExtensionFieldType.DATE_TIME],
            [fieldname:'newsletter', valueAsString: 'true', value: true , type: ExtensionFieldType.BOOLEAN]
        ]

    def fixtures2 = [
        [fieldname:'favoredPet', valueAsString: 'doc', value: 'doc' , type: ExtensionFieldType.STRING],
    ]
    
    Set<ExtensionFieldValueEntity> extensionFieldValueEntitySet
    Set<Extension> scimExtensionSet
    
    private ExtensionConverter converter;
    
    def setup(){
        converter  = new ExtensionConverter()
    }    
    
    def 'convert extensionEntity set to scim extension set works'() {
        given:
        extensionFieldValueEntitySet = createExtensionValueList(urn1, fixture1, urn2, fixture2)
        scimExtensionSet = new HashSet<>()
        createFilledExtensionEntity()
        createFilledScimExtension()
        
        when:
        Set<Extension> extensions = converter.toScim(extensionFieldValueEntitySet)
        
        then:
        extensions == scimExtensionSet
    }

    def 'convert scim extension to extensionEntity works'() {
        when:
        Set<ExtensionFieldEntity> extensions = converter.fromScim(scimExtensionSet)
        
        then:
        extensions == extensionFieldValueEntitySet
    }
    
    def 'convert empty extensionEntity set to empty extension set works'(){
        given:
        extensionFieldValueEntitySet = Collections.emptySet()
        
        when:
        Set<Extension> extensions = converter.toScim(extensionFieldValueEntitySet)
        
        then:
        extensions != null
        extensions.size() == 0
    }

    def 'convert empty scim extension to empty extensionEntity works'() {
        given:
        scimExtensionSet = Collections.emptySet()
        
        when:
        Set<ExtensionFieldEntity> extensions = converter.fromScim(scimExtensionSet)
        
        then:
        extensions != null
        extensions.size() == 0
    }
        
    def 'passing null scim extension returns null'(){
        expect:
        converter.fromScim(null) == null
    }
    
    def 'passing null extensionEntity returns null'(){
        expect:
        converter.toScim(null) == null
    }
    
    def addNameValuePairToExtensionEntity(ExtensionEntity extensionEntity, String name, String value){
        ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity();
        fieldEntity.setName(name);
        
        ExtensionFieldValueEntity valueEntity = new ExtensionFieldValueEntity();
        valueEntity.setValue(value);
        valueEntity.setExtensionField(fieldEntity);
        
        fieldEntity.setExtension(extensionEntity);
    }
    
    def createFilledExtensionEntity(){
        
        def test = fixtures1.get(0).getAt('fieldname')
        ExtensionEntity entity01 = new ExtensionEntity();
        entity01.setUrn(URN1);
        for (var in fixtures1) {
            addFieldToEntityValueSet(entity01, var.getAt('fieldname'), var.getAt('valueAsString'), var.getAt('type'))
        }
        
       
        ExtensionEntity entity02 = new ExtensionEntity();
        entity02.setUrn(URN2);
        for (var in fixtures2) {
            addFieldToEntityValueSet(entity01, var.getAt('fieldname'), var.getAt('valueAsString'), var.getAt('type'))
        }
    }
    
    def createFilledScimExtension(){
        
        Extension extension01 = new Extension(URN1);
        
        for (var in fixtures1) {
            extension01.addOrUpdateField(var.getAt('fieldname'), var.getAt('value'));
        }
        extension01.addOrUpdateField("gender", "male");
        extension01.addOrUpdateField("size", new BigDecimal(1.78));
        extension01.addOrUpdateField("numberChildren", BigInteger.valueOf(2));
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();
        Date birthDay = new Date(dateTimeFormatter.parseDateTime("2008-01-23T04:56:22.000Z").getMillis());
        extension01.addOrUpdateField("birth", birthDay);
        extension01.addOrUpdateField("newsletter", true);
        
        Extension extension02 = new Extension(urn02);
        extension02.addOrUpdateField("favoredPet", "doc");

        scimExtensionSet.add(extension01);
        scimExtensionSet.add(extension02);
    }
    
    def addFieldToEntityValueSet(ExtensionEntity extensionEntity, String name, String value, ExtensionFieldType type){
        ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity();
        fieldEntity.setName(name);
        fieldEntity.setType(type)
        
        ExtensionFieldValueEntity valueEntity = new ExtensionFieldValueEntity();
        valueEntity.setValue(value);
        fieldEntity.setExtension(extensionEntity);
        valueEntity.setExtensionField(fieldEntity)
        
        extensionFieldValueEntitySet.add(valueEntity);
    }
}
