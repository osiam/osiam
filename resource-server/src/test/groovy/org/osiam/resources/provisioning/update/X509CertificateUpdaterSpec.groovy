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

import org.osiam.resources.converter.X509CertificateConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.X509Certificate
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.entities.X509CertificateEntity

import spock.lang.Specification


class X509CertificateUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    X509CertificateEntity x509CertificateEntity = Mock()
    X509CertificateConverter x509CertificateConverter = Mock()
    X509CertificateUpdater x509CertificateUpdater = new X509CertificateUpdater(x509CertificateConverter : x509CertificateConverter)

    def 'removing all x509Certificate is possible'() {
        when:
        x509CertificateUpdater.update(null, userEntity, ['x509Certificates'] as Set)

        then:
        1 * userEntity.removeAllX509Certificates()
        userEntity.getX509Certificates() >> ([
            new X509CertificateEntity(value : IRRELEVANT),
            new X509CertificateEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an x509Certificate is possible'(){
        given:
        MultiValuedAttribute x509Certificate01 = new X509Certificate.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        X509CertificateEntity x509CertificateEntity01 = new X509CertificateEntity(value : IRRELEVANT)

        when:
        x509CertificateUpdater.update([x509Certificate01] as List, userEntity, [] as Set)

        then:
        1 * x509CertificateConverter.fromScim(x509Certificate01) >> x509CertificateEntity01
        1 * userEntity.removeX509Certificate(x509CertificateEntity01)
    }

    def 'adding a new x509Certificate is possible'(){
        given:
        MultiValuedAttribute x509Certificate = new X509Certificate.Builder(value : IRRELEVANT).build()
        X509CertificateEntity x509CertificateEntity = new X509CertificateEntity(value : IRRELEVANT)

        when:
        x509CertificateUpdater.update([x509Certificate] as List, userEntity, [] as Set)

        then:
        1 * x509CertificateConverter.fromScim(x509Certificate) >> x509CertificateEntity
        1 * userEntity.addX509Certificate(x509CertificateEntity)
    }
    
    def 'adding a new primary x509Certificate removes the primary attribite from the old one'(){
        given:
        MultiValuedAttribute newPrimaryX509Certificate = new X509Certificate.Builder(value : IRRELEVANT, primary : true).build()
        X509CertificateEntity newPrimaryX509CertificateEntity = new X509CertificateEntity(value : IRRELEVANT, primary : true)

        X509CertificateEntity oldPrimaryX509CertificateEntity = Spy()
        oldPrimaryX509CertificateEntity.setValue(IRRELEVANT_02)
        oldPrimaryX509CertificateEntity.setPrimary(true)

        X509CertificateEntity x509CertificateEntity = new X509CertificateEntity(value : IRRELEVANT_02, primary : false)

        when:
        x509CertificateUpdater.update([newPrimaryX509Certificate] as List, userEntity, [] as Set)

        then:
        1 * x509CertificateConverter.fromScim(newPrimaryX509Certificate) >> newPrimaryX509CertificateEntity
        1 * userEntity.getX509Certificates() >> ([oldPrimaryX509CertificateEntity] as Set)
        1 * userEntity.addX509Certificate(newPrimaryX509CertificateEntity)
        1 * oldPrimaryX509CertificateEntity.setPrimary(false)
    }
}
