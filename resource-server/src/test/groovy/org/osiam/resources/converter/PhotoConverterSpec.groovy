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

package org.osiam.resources.converter

import org.osiam.resources.scim.Photo
import org.osiam.storage.entities.PhotoEntity

import spock.lang.Specification

class PhotoConverterSpec extends Specification {

    PhotoEntity entity
    Photo attribute
    PhotoConverter converter

    def setup(){
        def value = 'myphoto.com/me.JPG'
        def type = Photo.Type.PHOTO

        entity = new PhotoEntity()
        entity.setValue(value)
        entity.setType(type)

        attribute = new Photo.Builder()
                .setValue(value)
                .setType(type)
                .build()

        converter = new PhotoConverter()
    }

    def 'convert Entity to scim Photo MultiValueAttribute works'() {
        when:
        def attribute = converter.toScim(entity)

        then:
        attribute.equals(this.attribute)
    }

    def 'convert scim Photo MultiValueAttribuite to Entity works'() {
        when:
        def entity = converter.fromScim(this.attribute)

        then:
        entity == this.entity
    }
}
