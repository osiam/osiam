package org.osiam.resources.converter;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.dao.ExtensionDao;
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
public class ExtensionConverter implements Converter<Set<Extension>, Set<ExtensionFieldValueEntity>> {

    @Inject
    private ExtensionDao extensionDao;


    @Override
    public Set<ExtensionFieldValueEntity> fromScim(Set<Extension> extensions) {
        Set<ExtensionFieldValueEntity> result = new HashSet<>();

        for (Extension extension : extensions) {
            String urn = extension.getUrn();
            ExtensionEntity extensionEntity = extensionDao.getExtensionByUrn(urn);

            for (ExtensionFieldEntity field : extensionEntity.getFields()) {
                if (extension.isFieldPresent(field.getName())) {
                    ExtensionFieldValueEntity value = new ExtensionFieldValueEntity();
                    // This is a shortcut that only works because we know that the field values are always
                    // stored as string.
                    value.setValue(extension.getField(field.getName(), ExtensionFieldType.STRING));
                    value.setExtensionField(field);
                    result.add(value);
                }
            }

        }
        return result;
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
                // If this is ever true, something went very, very wrong.
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
}

