/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.PhotoConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.Photo
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
        Photo photo01 = new Photo.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        PhotoEntity photoEntity01 = new PhotoEntity(value : IRRELEVANT)

        when:
        photoUpdater.update([photo01] as List, userEntity, [] as Set)

        then:
        1 * photoConverter.fromScim(photo01) >> photoEntity01
        1 * userEntity.removePhoto(photoEntity01)
    }

    def 'adding a new photo is possible'(){
        given:
        Photo photo = new Photo.Builder(value : IRRELEVANT).build()
        PhotoEntity photoEntity = new PhotoEntity(value : IRRELEVANT)

        when:
        photoUpdater.update([photo] as List, userEntity, [] as Set)

        then:
        1 * photoConverter.fromScim(photo) >> photoEntity
        1 * userEntity.addPhoto(photoEntity)
    }
    
    def 'adding a new primary photo removes the primary attribite from the old one'(){
        given:
        Photo newPrimaryPhoto = new Photo.Builder(value : IRRELEVANT, primary : true).build()
        PhotoEntity newPrimaryPhotoEntity = new PhotoEntity(value : IRRELEVANT, primary : true)

        PhotoEntity oldPrimaryPhotoEntity = Spy()
        oldPrimaryPhotoEntity.setValue(IRRELEVANT_02)
        oldPrimaryPhotoEntity.setPrimary(true)

        PhotoEntity photoEntity = new PhotoEntity(value : IRRELEVANT_02, primary : false)

        when:
        photoUpdater.update([newPrimaryPhoto] as List, userEntity, [] as Set)

        then:
        1 * photoConverter.fromScim(newPrimaryPhoto) >> newPrimaryPhotoEntity
        1 * userEntity.getPhotos() >> ([oldPrimaryPhotoEntity] as Set)
        1 * userEntity.addPhoto(newPrimaryPhotoEntity)
        1 * oldPrimaryPhotoEntity.setPrimary(false)
    }
}
