package org.osiam.resources.converter

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.EntitlementsEntity

import spock.lang.Specification

class EntitlementConverterSpec extends Specification {

    EntitlementsEntity entity
    MultiValuedAttribute attribute
    EntitlementConverter converter;
    
    def setup(){
        def value = 'example'
        
        entity = new EntitlementsEntity()
        entity.setValue(value)
    
        attribute = new MultiValuedAttribute.Builder()
               .setValue(value)
               .build()
                
        converter = new EntitlementConverter()
    }    
    
    def 'convert Entity to scim Entitlement MultiValueAttribute works'() {
        when:
        def attribute = converter.toScim(entity)
        
        then:
        attribute.equals(this.attribute)
    }

    def 'convert scim Entitlement MultiValueAttribuite to Entity works'() {
        when:
        def entity = converter.fromScim(this.attribute)
        
        then:
        entity == this.entity
    }
}
