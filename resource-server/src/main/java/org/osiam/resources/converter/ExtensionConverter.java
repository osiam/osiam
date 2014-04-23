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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.ExtensionFieldValueEntity;
import org.osiam.storage.helper.NumberPadder;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Service
public class ExtensionConverter implements Converter<Set<Extension>, Set<ExtensionFieldValueEntity>> {

    @Inject
    private ExtensionDao extensionDao;

    @Inject
    private NumberPadder numberPadder;

    @Override
    public Set<ExtensionFieldValueEntity> fromScim(Set<Extension> extensions) {
        Set<ExtensionFieldValueEntity> result = new HashSet<>();

        for (Extension extension : checkNotNull(extensions)) {
            String urn = extension.getUrn();
            ExtensionEntity extensionEntity = extensionDao.getExtensionByUrn(urn);

            for (ExtensionFieldEntity field : extensionEntity.getFields()) {
                if (extension.isFieldPresent(field.getName())) {

                    if (Strings.isNullOrEmpty(extension.getField(field.getName(), ExtensionFieldType.STRING))) {
                        continue;
                    }

                    ExtensionFieldValueEntity value = new ExtensionFieldValueEntity();

                    String typeCheckedStringValue = getTypeCheckedStringValue(field.getType(), field.getName(),
                            extension);

                    if (field.getType() == ExtensionFieldType.INTEGER || field.getType() == ExtensionFieldType.DECIMAL) {
                        typeCheckedStringValue = numberPadder.pad(typeCheckedStringValue);
                    }

                    value.setValue(typeCheckedStringValue);
                    value.setExtensionField(field);
                    result.add(value);
                }
            }

        }
        return result;
    }

    private <T> String getTypeCheckedStringValue(ExtensionFieldType<T> type, String fieldName, Extension extension) {
        T value = extension.getField(fieldName, type);
        return type.toString(value);
    }

    @Override
    public Set<Extension> toScim(Set<ExtensionFieldValueEntity> entity) {
        Map<String, Extension.Builder> extensionMap = new HashMap<>();

        for (ExtensionFieldValueEntity fieldValueEntity : checkNotNull(entity)) {
            String urn = fieldValueEntity.getExtensionField().getExtension().getUrn();
            Extension.Builder extensionBuilder;

            if (extensionMap.containsKey(urn)) {
                extensionBuilder = extensionMap.get(urn);
            } else {
                extensionBuilder = new Extension.Builder(urn);
                extensionMap.put(urn, extensionBuilder);
            }

            ExtensionFieldType<?> type = fieldValueEntity.getExtensionField().getType();
            if (type == null) {
                // If this is ever true, something went very, very wrong.
                throw new IllegalArgumentException("The ExtensionField type can't be null");
            }
            String value = fieldValueEntity.getValue();

            if (type == ExtensionFieldType.INTEGER || type == ExtensionFieldType.DECIMAL) {
                value = numberPadder.unpad(value);
            }

            String name = fieldValueEntity.getExtensionField().getName();
            addField(extensionBuilder, type, name, value);
        }

        HashSet<Extension> extensions = new HashSet<>();
        for (Extension.Builder builder : extensionMap.values()) {
            extensions.add(builder.build());
        }
        
        return extensions;
    }

    private <T> void addField(Extension.Builder extensionBuilder, ExtensionFieldType<T> type, String fieldName, String stringValue) {
        extensionBuilder.setField(fieldName, type.fromString(stringValue), type);
    }
}
