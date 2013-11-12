package org.osiam.resources.converter

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.EntitlementsEntity
import org.osiam.storage.entities.RolesEntity

import spock.lang.Specification

class RoleConverterSpec extends Specification {

    RolesEntity entity
    MultiValuedAttribute attribute
    RoleConverter converter;
    
    def setup(){
        def value = 'student'
        
        entity = new RolesEntity()
        entity.setValue(value)
    
        attribute = new MultiValuedAttribute.Builder()
               .setValue(value)
               .build()
                
        converter = new RoleConverter()
    }    
    
    def 'convert Entity to scim Role MultiValueAttribute works'() {
        when:
        def attribute = converter.toScim(entity)
        
        then:
        attribute.equals(this.attribute)
    }

    def 'convert scim Role MultiValueAttribuite to Entity works'() {
        when:
        def entity = converter.fromScim(this.attribute)
        
        then:
        entity == this.entity
    }
}
