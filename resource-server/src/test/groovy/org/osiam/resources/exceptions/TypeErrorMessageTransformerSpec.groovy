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

package org.osiam.resources.exceptions

import spock.lang.Specification

class TypeErrorMessageTransformerSpec extends Specification {
    def underTest = new TypeErrorMessageTransformer()

    def "should transform message No enum constant org.osiam.storage.entities.PhotoEntity.CanonicalPhotoTypes.huch"() {
        when:
        def result = underTest.transform("No enum constant org.osiam.storage.entities.PhotoEntity.CanonicalPhotoTypes.huch")
        then:
        result == "huch is not a valid Photo type only photo, thumbnail are allowed."
    }

    def "should not transform message No enum constant org.osiam.storage.hahaha.CanonicalPhotoTypes.huch"() {
        when:
        def result = underTest.transform("No enum constant org.osiam.storage.hahaha.CanonicalPhotoTypes.huch")
        then:
        result == "No enum constant org.osiam.storage.hahaha.CanonicalPhotoTypes.huch"
    }

    def "should ignore null"() {
        when:
        def result = underTest.transform(null)
        then:
        !result
    }
}
