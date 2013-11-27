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

package org.osiam.resources.converter;

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.X509CertificateEntity;
import org.springframework.stereotype.Service;

@Service
public class X509CertificateConverter implements Converter<MultiValuedAttribute, X509CertificateEntity> {

    @Override
    public X509CertificateEntity fromScim(MultiValuedAttribute scim) {
        X509CertificateEntity x509CertificateEntity = new X509CertificateEntity();
        x509CertificateEntity.setValue(String.valueOf(scim.getValue()));
        return x509CertificateEntity;
    }

    @Override
    public MultiValuedAttribute toScim(X509CertificateEntity entity) {
        return new MultiValuedAttribute.Builder().
                setValue(entity.getValue()).
                build();
    }

}
