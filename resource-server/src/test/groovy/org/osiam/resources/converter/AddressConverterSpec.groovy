package org.osiam.resources.converter

import org.hibernate.collection.internal.AbstractPersistentCollection.SetProxy;
import org.junit.Before;
import org.osiam.resources.scim.Address
import org.osiam.resources.scim.User
import org.osiam.storage.entities.AddressEntity;

import spock.lang.Specification;

class AddressConverterSpec extends Specification {

    AddressEntity addressEntity
    Address scimAddress
    AddressConverter addressConverter
    
    def setup(){
        addressEntity = new AddressEntity(
            formatted:'example', 
            streetAddress:'Fake Street 123', 
            locality:'de', 
            region:'de', 
            postalCode:'12345',
            country:'Germany')
        addressEntity.setPrimary(true)             
            

        scimAddress = new Address.Builder()
                        .setFormatted('example')
                        .setStreetAddress('Fake Street 123')
                        .setLocality('de')
                        .setRegion('de')
                        .setPostalCode('12345')
                        .setCountry('Germany')
                        .setPrimary(true)        
                        .build()
                        
          addressConverter = new AddressConverter()
    }    
    
    def 'convert addressEntity to scim address works'() {
        when:
        Address address = addressConverter.toScim(addressEntity)
        
        then:
        address.equals(scimAddress)
    }

    def 'convert scim address to  addressEntity works'() {
        when:
        AddressEntity entity = addressConverter.fromScim(scimAddress)
        
        then:
        entity == addressEntity
    }
    
    def 'passing null scim address returns null'(){
        expect:
        addressConverter.fromScim(null) == null
    }
    
    def 'passing null addressEntity returns null'(){
        expect:
        addressConverter.toScim(null) == null
    }
}
