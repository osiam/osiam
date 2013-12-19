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
