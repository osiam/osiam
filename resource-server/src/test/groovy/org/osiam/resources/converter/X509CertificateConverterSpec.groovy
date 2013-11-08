package org.osiam.resources.converter

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.EntitlementsEntity
import org.osiam.storage.entities.RolesEntity
import org.osiam.storage.entities.X509CertificateEntity;

import spock.lang.Specification

class X509CertificateConverterSpec extends Specification {

    X509CertificateEntity entity
    MultiValuedAttribute attribute
    X509CertificateConverter converter;
    
    def setup(){
        def value = 'example'
        
        entity = new X509CertificateEntity()
        entity.setValue(value)
    
        attribute = new MultiValuedAttribute.Builder()
               .setValue(value)
               .build()
                
        converter = new X509CertificateConverter()
    }    
    
    def 'convert Entity to scim X509Certificate MultiValueAttribute works'() {
        when:
        def attribute = converter.toScim(entity)
        
        then:
        attribute.equals(this.attribute)
    }

    def 'convert scim X509Certificate MultiValueAttribuite to Entity works'() {
        when:
        def entity = converter.fromScim(this.attribute)
        
        then:
        entity == this.entity
    }
}
