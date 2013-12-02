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

import org.osiam.resources.scim.Name
import org.osiam.storage.entities.NameEntity

import spock.lang.Specification

class NameConverterSpec extends Specification {

    NameEntity nameEntity
    Name scimName
    NameConverter nameConverter;

    def setup() {
        nameEntity = new NameEntity(
                formatted: 'Mr. Homer Simpson',
                familyName: 'Simpson',
                givenName: 'Homer',
                middleName: 'Jay',
                honorificPrefix: 'Dr.',
                honorificSuffix: 'md.'
                )

        scimName = new Name.Builder()
                .setFormatted('Mr. Homer Simpson')
                .setFamilyName('Simpson')
                .setGivenName('Homer')
                .setMiddleName('Jay')
                .setHonorificPrefix('Dr.')
                .setHonorificSuffix('md.')
                .build()

        nameConverter = new NameConverter()
    }

    def 'convert nameEntity to scim name works'() {
        when:
        Name name = nameConverter.toScim(nameEntity)

        then:
        name.equals(scimName)
    }

    def 'convert scim name to nameEntity works'() {
        when:
        NameEntity entity = nameConverter.fromScim(scimName)

        then:
        entity == nameEntity
    }

    def 'passing null scim name returns null'() {
        expect:
        nameConverter.fromScim(null) == null
    }

    def 'passing null nameEntity returns null'() {
        expect:
        nameConverter.toScim(null) == null
    }
}
