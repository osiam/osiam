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

package org.osiam.storage.entities

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.PhotoEntity
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

class PhotoEntitySpec extends Specification {

    PhotoEntity photoEntity = new PhotoEntity()
    def userEntity = Mock(UserEntity)


    def "should throw exception if the value has no valid file suffix"() {
        when:
        photoEntity.setValue("https://photos.example.com/profilephoto/72930000000Ccne/T")

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "The photo MUST have an attribute 'value' that ends with JPEG, JPG, GIF, PNG."
    }

    def "setter and getter for the type should be present"() {
        when:
        photoEntity.setType("thumbnail")

        then:
        photoEntity.getType() == "thumbnail"
    }

    def "setter and getter for the user should be present"() {
        when:
        photoEntity.setUser(userEntity)

        then:
        photoEntity.getUser() == userEntity
    }

    def "value should contain a valid file suffix"() {
        when:
        photoEntity.setValue("file.JPG")

        then:
        photoEntity.getValue() == "file.JPG"
    }

    def "should throw an exception if the type is unknown"() {
        when:
        photoEntity.setType("huch")

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "No enum constant org.osiam.storage.entities.PhotoEntity.CanonicalPhotoTypes.huch"
    }

    def "setter and getter for id should be present"() {
        when:
        photoEntity.setMultiValueId(1234)

        then:
        photoEntity.getMultiValueId() == 1234
    }
}