package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.ImConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.ImEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class ImUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    ImEntity imEntity = Mock()
    ImConverter imConverter = Mock()
    ImUpdater imUpdater = new ImUpdater(imConverter : imConverter)

    def 'removing all ims is possible'() {
        when:
        imUpdater.update(null, userEntity, ['ims'] as Set)

        then:
        1 * userEntity.removeAllIms()
        userEntity.getIms() >> ([
            new ImEntity(value : IRRELEVANT),
            new ImEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an im is possible'(){
        given:
        MultiValuedAttribute im01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        ImEntity imEntity01 = new ImEntity(value : IRRELEVANT)

        when:
        imUpdater.update([im01] as List, userEntity, [] as Set)

        then:
        1 * imConverter.fromScim(im01) >> imEntity01
        1 * userEntity.removeIm(imEntity01)
    }

    def 'adding a new im is possible'(){
        given:
        MultiValuedAttribute im = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        ImEntity imEntity = new ImEntity(value : IRRELEVANT)

        when:
        imUpdater.update([im] as List, userEntity, [] as Set)

        then:
        1 * imConverter.fromScim(im) >> imEntity
        1 * userEntity.addIm(imEntity)
    }

}
