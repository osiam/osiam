package org.osiam.resources.converter;

import org.osiam.resources.scim.Extension;
import org.osiam.storage.entities.extension.ExtensionEntity;
import org.springframework.stereotype.Service;

@Service
public class ExtensionConverter implements Converter<Extension, ExtensionEntity>{

    @Override
    public ExtensionEntity fromScim(Extension scim) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Converting all ExtensionFields of the User into a Scim-Extensions representation.
     *
     * @param userEntity
     *          getting the extensions for conversion
     * @return A Map of extensions with corresponding urn's. Never null
     */
    @Override
    public Extension toScim(ExtensionEntity entity) {
        // TODO Auto-generated method stub
        return null;
    }

}
