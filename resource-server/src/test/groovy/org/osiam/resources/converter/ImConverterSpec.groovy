package org.osiam.resources.converter

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.Name
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.PhoneNumberEntity;

import spock.lang.Specification

class ImConverterSpec extends Specification {

    ImEntity entity
    MultiValuedAttribute attribute
    ImConverter converter;
    
    def setup(){
        def value = 'user@gmail.com'
        def type = 'gtalk'
        
        entity = new ImEntity()
        entity.setValue(value)
        entity.setType(type)
    
        attribute = new MultiValuedAttribute.Builder()
               .setValue(value)
               .setType(type)
               .build()
                
        converter = new ImConverter()
    }    
    
    def 'convert Entity to scim IM MultiValueAttribute works'() {
        when:
        def attribute = converter.toScim(entity)
        
        then:
        attribute.equals(this.attribute)
    }

    def 'convert scim IM MultiValueAttribuite to Entity works'() {
        when:
        def entity = converter.fromScim(this.attribute)
        
        then:
        entity == this.entity
    }
}
