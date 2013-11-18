package org.osiam.resources.converter

import org.osiam.resources.scim.Meta
import org.osiam.storage.entities.MetaEntity

import spock.lang.Specification

class MetaConverterSpec extends Specification {
    
    MetaConverter metaConverter = new MetaConverter();
    
    def 'convert MetaEntity to scim meta works'() {
        given:
        Calendar calendar = Calendar.instance;
        MetaEntity metaEntity = new MetaEntity(calendar);
        metaEntity.setResourceType('USER')
        
        when:
        Meta meta = metaConverter.toScim(metaEntity)
        
        then:
        meta.getCreated() == calendar.getTime()
        meta.getLastModified() == calendar.getTime()
        meta.getResourceType() == 'USER'
    }
    
    def 'passing null metaEntity returns null'(){
        expect:
        metaConverter.toScim(null) == null
    }
}
