/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.data

import org.osiam.resources.exception.SCIMDataValidationException
import org.osiam.resources.scim.Photo
import spock.lang.Specification
import spock.lang.Unroll

class DataUriSpec extends Specification{

    @Unroll
    def 'A #type can be transformed into an DataURI'() {
        when:
        DataURI dataUri = new DataURI(value)

        then:
        dataUri.toString().length() > 0

        where:
        type          | value
        'String'      | 'data:image/png;base64,blabla'
        'URI'         | new URI('data:image/png;base64,blabla')
        'inputStream' | getClass().getResourceAsStream('osiamlogo.png')
    }

    @Unroll
    def 'A #type can be transformed into an ImageDataURI'() {
        when:
        ImageDataURI dataUri = new ImageDataURI(value)
        then:
        dataUri.toString().length() > 0

        where:
        type          | value
        'String'      | 'data:image/png;base64,blabla'
        'URI'         | new URI('data:image/png;base64,blabla')
        'inputStream' | getClass().getResourceAsStream('osiamlogo.png')
    }

    @Unroll
    def 'A invalid #type raises exception while creating an DataURI'() {
        when:
        DataURI dataUri = new DataURI(value)

        then:
        thrown(SCIMDataValidationException)

        where:
        type          | value
        'String'      | '!"§$%'
        'URI'         | new URI('blabla')
    }

    @Unroll
    def 'A invalid #type raises exception while creating an ImageDataURI'() {
        when:
        ImageDataURI dataUri = new ImageDataURI(value)

        then:
        thrown(SCIMDataValidationException)

        where:
        type          | value
        'String'      | 'data:text/csv;base64,blabla'
        'URI'         | new URI('data:text/csv;base64,blabla')
        'inputStream' | getClass().getResourceAsStream('noPicture.txt')
    }

    def 'the mimetype is returned in the correct way'(){
        given:
        DataURI dataUri = new DataURI(getClass().getResourceAsStream('osiamlogo.png'))
        String mimeType

        when:
        mimeType = dataUri.getMimeType()

        then:
        mimeType == 'image/png'
    }

    def 'given inputstream is correctly returned'(){
        given:
        InputStream inputStream = getClass().getResourceAsStream('osiamlogo.png')
        ImageDataURI dataUri
        InputStream returnedInputStream

        when:
        dataUri = new ImageDataURI(inputStream)
        returnedInputStream = dataUri.getAsInputStream()

        then:
        dataUri.toString() == new ImageDataURI(returnedInputStream).toString()
    }

    @Unroll
    def 'A mime type #type can be reconized correctly from the Photo'() {
        given:
        Photo.Builder photoBuilder = new Photo.Builder()
        PhotoValueType photoType

        when:
        photoType =  photoBuilder.setValue(value).build().getValueType()


        then:
        photoType == type

        where:
        type                          | value
        PhotoValueType.URI            | new URI('blabla')
        PhotoValueType.IMAGE_DATA_URI | new ImageDataURI(getClass().getResourceAsStream('osiamlogo.png'))
    }

}
