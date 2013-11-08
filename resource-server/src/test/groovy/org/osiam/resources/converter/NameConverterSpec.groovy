package org.osiam.resources.converter

import javax.persistence.Column;

import org.hibernate.collection.internal.AbstractPersistentCollection.SetProxy;
import org.junit.Before;
import org.osiam.resources.scim.Address
import org.osiam.resources.scim.Name
import org.osiam.resources.scim.User
import org.osiam.storage.entities.AddressEntity;
import org.osiam.storage.entities.NameEntity

import spock.lang.Specification;

class NameConverterSpec extends Specification {

    NameEntity nameEntity
    Name scimName
    NameConverter nameConverter;
    
    def setup(){
        nameEntity = new NameEntity(
            formatted:'Mr. Homer Simpson',
            familyName:'Simpson',
            givenName:'Homer',
            middleName:'Jay',
            honorificPrefix:'Dr.',
            honorificSuffix:'md.'
            )
    
        scimName = new Name.Builder()
                .setFormatted('Mr. Homer Simpson')
                .setFamilyName('Simpson')
                .setGivenName('Homer')
                .setMiddleName('Jay')
                .setHonorificPrefix('Dr.')
                .setHonorificSuffix('md.')
                .build()
                
        nameConverter = new NameConverter()
    }    
    
    def 'convert nameEntity to scim name works'() {
        when:
        Name name = nameConverter.toScim(nameEntity)
        
        then:
        name.equals(scimName)
    }

    def 'convert scim name to nameEntity works'() {
        when:
        NameEntity entity = nameConverter.fromScim(scimName)
        
        then:
        entity == nameEntity
    }
    
    def 'passing null scim name returns null'(){
        expect:
        nameConverter.fromScim(null) == null
    }
    
    def 'passing null nameEntity returns null'(){
        expect:
        nameConverter.toScim(null) == null
    }
}
