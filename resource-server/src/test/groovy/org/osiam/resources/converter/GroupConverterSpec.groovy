package org.osiam.resources.converter

import javax.persistence.NoResultException

import org.osiam.resources.scim.Group
import org.osiam.storage.dao.GroupDAO
import org.osiam.storage.entities.GroupEntity

import spock.lang.Specification

class GroupConverterSpec extends Specification {

    Map fixtures = [displayName:'displayName',
                    externalId: 'externalId'
                    ]
    
    private GroupDAO groupDao = Mock(GroupDAO)
    
    GroupConverter groupConverter = new GroupConverter(groupDao:groupDao)
    
    def 'converting group entity to scim works as expected'() {
        given:
        def uuid = UUID.randomUUID()
        
        GroupEntity groupEntity = getFilledGroupEntity(uuid)
        
        when:
        def group = groupConverter.toScim(groupEntity)

        then:
        group.id == uuid.toString()
        group.displayName == fixtures["displayName"]
        group.externalId == fixtures["externalId"]
    }

    def 'converting scim group to entity works as expected'(){
        given:
        def uuid = UUID.randomUUID()
        
        Group group = getFilledGroup(uuid)
        
        when:
        def groupEntity = groupConverter.fromScim(group)

        then:
        1 * groupDao.getById(_) >> {throw new NoResultException()} 
        
        groupEntity.id == uuid
        groupEntity.displayName == fixtures["displayName"]
        groupEntity.externalId == fixtures["externalId"]
    }
        
    def 'converting null scim group returns null'(){
        expect:
        groupConverter.fromScim(null) == null
    }
    
    def 'converting null group entity returns null'(){
        expect:
        groupConverter.toScim(null) == null
    }
    
    def GroupEntity getFilledGroupEntity(def uuid){
        
        GroupEntity groupEntity = new GroupEntity(fixtures)
        
        groupEntity.setId(uuid)
        
        return groupEntity
    }
    
    def Group getFilledGroup(UUID uuid){
        
        Group.Builder groupBuilder = new Group.Builder(fixtures)
        
        groupBuilder.setId(uuid.toString())
        
        return groupBuilder.build()
    }
    
    
}
