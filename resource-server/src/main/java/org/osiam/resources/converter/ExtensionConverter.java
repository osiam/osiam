package org.osiam.resources.converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Extension.Field;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.entities.extension.ExtensionEntity;
import org.osiam.storage.entities.extension.ExtensionFieldEntity;
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity;
import org.springframework.stereotype.Service;

@Service
public class ExtensionConverter implements Converter<Set<Extension>, Set<ExtensionFieldValueEntity>>{

    @Override
    public Set<ExtensionFieldValueEntity> fromScim(Set<Extension> scim) {
        if(scim == null){
            return null;
        }
        Set<ExtensionFieldValueEntity> fieldValuesSet = new HashSet<>();
        
        for(Extension extension : scim){
            ExtensionEntity entity = new ExtensionEntity();
            entity.setUrn(extension.getUrn());
            
            Map<String, Field> fields = extension.getAllFields();
            for(String fieldName : fields.keySet()){
                Field field = fields.get(fieldName);
                
                ExtensionFieldEntity fieldEntity = new ExtensionFieldEntity();
                fieldEntity.setName(fieldName);
                fieldEntity.setType(field.getType());

                ExtensionFieldValueEntity valueEntity = new ExtensionFieldValueEntity();
                valueEntity.setValue(field.getValue());
                fieldEntity.setExtension(entity);
                valueEntity.setExtensionField(fieldEntity);
                
                fieldValuesSet.add(valueEntity);
            }
            
        }

        return fieldValuesSet;
    }

    @Override
    public Set<Extension> toScim(Set<ExtensionFieldValueEntity> entity) {
        if(entity == null){
            return null;
        }
        
        Map<String, Extension> extensionMap = new HashMap<>();
        
        for(ExtensionFieldValueEntity fieldValueEntity : entity){
            String urn = fieldValueEntity.getExtensionField().getExtension().getUrn();
            Extension extension;
            
            if(extensionMap.containsKey(urn)){
                extension = extensionMap.get(urn);
            }else{
                extension = new Extension(urn);
                extensionMap.put(urn, extension);
            }
            
            ExtensionFieldType<?> type = fieldValueEntity.getExtensionField().getType();
            if(type == null){
                throw new IllegalArgumentException("The ExtensionField type can't be null");
            }
            String value = fieldValueEntity.getValue();
            String name = fieldValueEntity.getExtensionField().getName();
            addField(extension, type, name, value);
        }
        
        Set<Extension> extensionSet = new HashSet<>(extensionMap.values());

        return extensionSet;
    }

    private <T> void addField(Extension extension, ExtensionFieldType<T> type, String fieldName, String stringValue) {
        extension.addOrUpdateField(fieldName, type.fromString(stringValue), type);
    }

}

