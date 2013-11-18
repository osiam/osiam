package org.osiam.resources.converter

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.Name
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.PhoneNumberEntity;

import spock.lang.Specification

class PhoneNumberConverterSpec extends Specification {

    PhoneNumberEntity entity
    MultiValuedAttribute attribute
    PhoneNumberConverter converter;
    
    def setup(){
        def value = '+49 179 48754125'
        def type = 'work'
        
        entity = new PhoneNumberEntity()
        entity.setValue(value)
        entity.setType(type)
    
        attribute = new MultiValuedAttribute.Builder()
               .setValue(value)
               .setType(type)
               .build()
                
        converter = new PhoneNumberConverter()
    }    
    
    def 'convert Entity to scim PhoneNumber MultiValueAttribute works'() {
        when:
        def attribute = converter.toScim(entity)
        
        then:
        attribute.equals(this.attribute)
    }

    def 'convert scim PhoneNumber MultiValueAttribuite to Entity works'() {
        when:
        def entity = converter.fromScim(this.attribute)
        
        then:
        entity == this.entity
    }
}
