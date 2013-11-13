package org.osiam.resources.converter;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.resources.scim.User;
import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.extension.ExtensionEntity;
import org.osiam.storage.entities.extension.ExtensionFieldEntity;
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ExtensionConverter {

    @Inject
    ExtensionDao extensionDao;


    public Set<ExtensionFieldValueEntity> scimExtensionsToEntity(User scimUser, UserEntity userEntity) {
        Set<ExtensionFieldValueEntity> extensionFieldValueEntities = new HashSet<>();

        Set<String> userExtensionUris = scimUser.getAllExtensions().keySet();
        for (String urn : userExtensionUris) {
            Extension scimExtension = scimUser.getExtension(urn);
            ExtensionEntity extensionEntity = extensionDao.getExtensionByUrn(scimExtension.getUrn());
            Set<ExtensionFieldValueEntity> extensionFieldValues = mappingScimUserExtensionToEntity(scimExtension, userEntity, extensionEntity);
            addExtensionUrnToExtensionFields(extensionFieldValues, urn, extensionEntity);
            extensionFieldValueEntities.addAll(extensionFieldValues);
        }
        return extensionFieldValueEntities;
    }


    public Set<Extension> toScim(Set<ExtensionFieldValueEntity> entity) {
        if (entity == null) {
            return null;
        }

        Map<String, Extension> extensionMap = new HashMap<>();

        for (ExtensionFieldValueEntity fieldValueEntity : entity) {
            String urn = fieldValueEntity.getExtensionField().getExtension().getUrn();
            Extension extension;

            if (extensionMap.containsKey(urn)) {
                extension = extensionMap.get(urn);
            } else {
                extension = new Extension(urn);
                extensionMap.put(urn, extension);
            }

            ExtensionFieldType<?> type = fieldValueEntity.getExtensionField().getType();
            if (type == null) {
                throw new IllegalArgumentException("The ExtensionField type can't be null");
            }
            String value = fieldValueEntity.getValue();
            String name = fieldValueEntity.getExtensionField().getName();
            addField(extension, type, name, value);
        }

        return new HashSet<>(extensionMap.values());
    }

    private <T> void addField(Extension extension, ExtensionFieldType<T> type, String fieldName, String stringValue) {
        extension.addOrUpdateField(fieldName, type.fromString(stringValue), type);
    }

    private Set<ExtensionFieldValueEntity> mappingScimUserExtensionToEntity(Extension scimExtension, UserEntity userEntity, ExtensionEntity extensionEntity) {

        Set<ExtensionFieldValueEntity> extensionFieldValueEntities = new HashSet<>();

        for (ExtensionFieldEntity extensionFieldEntity : extensionEntity.getFields()) {
            ExtensionFieldValueEntity extensionFieldValueEntity = new ExtensionFieldValueEntity();
            extensionFieldValueEntity.setExtensionField(extensionFieldEntity);
            extensionFieldValueEntity.setUser(userEntity);

            if (scimExtension.isFieldPresent(extensionFieldEntity.getName())) {
                String extensionFieldValue = scimExtension.getField(extensionFieldEntity.getName(), ExtensionFieldType.STRING);
                extensionFieldValueEntity.setValue(extensionFieldValue);
            } else {
                extensionFieldValueEntity.setValue("");
            }

            extensionFieldValueEntities.add(extensionFieldValueEntity);
        }

        return extensionFieldValueEntities;
    }

    private void addExtensionUrnToExtensionFields(Set<ExtensionFieldValueEntity> extensionFieldValueEntities, String urn, ExtensionEntity extensionEntity) {
        Set<ExtensionFieldEntity> extensionFieldEntitySet = new HashSet<>();

        for (ExtensionFieldValueEntity extensionFieldValueEntity : extensionFieldValueEntities) {
            extensionFieldEntitySet.add(extensionFieldValueEntity.getExtensionField());
        }

        extensionEntity.setUrn(urn);
        extensionEntity.setFields(extensionFieldEntitySet);

    }


}

