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

package org.osiam.storage.helper

import org.osiam.storage.entities.DBVersion
import org.osiam.storage.helper.DataBaseSchemeVersionValidator
import spock.lang.Specification

import javax.persistence.EntityManager

class DataBaseSchemeVersionValidatorTest extends Specification {
    def em = Mock(EntityManager)

    def underTest = new DataBaseSchemeVersionValidator(em: em)

    def "should not throw an exception if set version of database-scheme got found"() {
        given:
        def version = new DBVersion()
        when:
        underTest.checkVersion()
        then:
        1 * em.find(DBVersion, DBVersion.DB_VERSION) >> version
    }

    def "should throw an exception if set version of database-scheme got not found"() {
        when:
        underTest.checkVersion()

        then:
        def e = thrown(IllegalStateException)
        e.message == "Database Scheme " + DBVersion.DB_VERSION +
                " not found. The reason may be that the wrong database scheme is enrolled, please contact a System-Administrator"
    }

    def "should throw an exception if the versions of database-scheme are not equals"() {
        given:
        def version = new DBVersion()
        version.version = 0.06
        em.find(DBVersion, DBVersion.DB_VERSION) >> version

        when:
        underTest.checkVersion()

        then:
        def e = thrown(IllegalStateException)
        e.message == "Database Scheme " + DBVersion.DB_VERSION +
                " not found. The reason may be that the wrong database scheme is enrolled, please contact a System-Administrator"
    }
}
