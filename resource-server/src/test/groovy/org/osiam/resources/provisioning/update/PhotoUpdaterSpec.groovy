package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.PhotoConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.PhotoEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class PhotoUpdaterSpec extends Specification {

    static IRRELEVANT = 'x.JPEG'
    static IRRELEVANT_02 = 'x.GIF'

    UserEntity userEntity = Mock()
    PhotoEntity photoEntity = Mock()
    PhotoConverter photoConverter = Mock()
    PhotoUpdater photoUpdater = new PhotoUpdater(photoConverter : photoConverter)

    def 'removing all photos is possible'() {
        when:
        photoUpdater.update(null, userEntity, ['photos'] as Set)

        then:
        1 * userEntity.removeAllPhotos()
        userEntity.getPhotos() >> ([
            new PhotoEntity(value : IRRELEVANT),
            new PhotoEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an photo is possible'(){
        given:
        MultiValuedAttribute photo01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        PhotoEntity photoEntity01 = new PhotoEntity(value : IRRELEVANT)

        when:
        photoUpdater.update([photo01] as List, userEntity, [] as Set)

        then:
        1 * photoConverter.fromScim(photo01) >> photoEntity01
        1 * userEntity.removePhoto(photoEntity01)
    }

    def 'adding a new photo is possible'(){
        given:
        MultiValuedAttribute photo = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        PhotoEntity photoEntity = new PhotoEntity(value : IRRELEVANT)

        when:
        photoUpdater.update([photo] as List, userEntity, [] as Set)

        then:
        1 * photoConverter.fromScim(photo) >> photoEntity
        1 * userEntity.addPhoto(photoEntity)
    }

}
