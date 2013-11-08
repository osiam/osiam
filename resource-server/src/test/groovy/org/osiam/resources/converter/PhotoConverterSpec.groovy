package org.osiam.resources.converter

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.ImEntity
import org.osiam.storage.entities.PhotoEntity

import spock.lang.Specification

class PhotoConverterSpec extends Specification {

    PhotoEntity entity
    MultiValuedAttribute attribute
    PhotoConverter converter;
    
    def setup(){
        def value = 'myphoto.com/me.JPG'
        def type = 'photo'
        
        entity = new PhotoEntity()
        entity.setValue(value)
        entity.setType(type)
    
        attribute = new MultiValuedAttribute.Builder()
               .setValue(value)
               .setType(type)
               .build()
                
        converter = new PhotoConverter()
    }    
    
    def 'convert Entity to scim Photo MultiValueAttribute works'() {
        when:
        def attribute = converter.toScim(entity)
        
        then:
        attribute.equals(this.attribute)
    }

    def 'convert scim Photo MultiValueAttribuite to Entity works'() {
        when:
        def entity = converter.fromScim(this.attribute)
        
        then:
        entity == this.entity
    }
}
