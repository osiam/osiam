package org.osiam.resources.converter

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.Name
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.NameEntity

import spock.lang.Specification

class EmailConverterSpec extends Specification {

    EmailEntity entity
    MultiValuedAttribute attribute
    EmailConverter converter;
    
    def setup(){
        def value = 'example@web.com'
        def type = 'work'
        
        entity = new EmailEntity()
        entity.setValue(value)
        entity.setType(type)
        entity.setPrimary(true)
    
        attribute = new MultiValuedAttribute.Builder()
               .setValue(value)
               .setType(type)
               .setPrimary(true) 
               .build()
                
        converter = new EmailConverter()
    }    
    
    def 'convert Entity to scim Email MultiValueAttribute works'() {
        when:
        def attribute = converter.toScim(entity)
        
        then:
        attribute.equals(this.attribute)
    }

    def 'convert scim Email MultiValueAttribuite to Entity works'() {
        when:
        def entity = converter.fromScim(this.attribute)
        
        then:
        entity == this.entity
    }
    
}
