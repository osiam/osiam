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
