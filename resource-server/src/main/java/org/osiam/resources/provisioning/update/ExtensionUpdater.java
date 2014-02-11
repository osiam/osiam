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

package org.osiam.resources.provisioning.update;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.ExtensionFieldValueEntity;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.helper.NumberPadder;
import org.springframework.stereotype.Service;

/**
 * The ExtensionUpdater provides the functionality to update the {@link ExtensionEntity} and
 * {@link ExtensionFieldValueEntity} of a UserEntity
 */
@Service
class ExtensionUpdater {

    @Inject
    private ExtensionDao extensionDao;
    
    @Inject
    private NumberPadder numberPadder;

    /**
     * updates (adds new, delete, updates) the {@link ExtensionEntity}'s of the given {@link UserEntity} based on the given
     * List of Email's
     * 
     * @param extensions
     *            map of {@link Extension} to be deleted, updated or added
     * @param userEntity
     *            user who needs to be updated
     * @param attributes
     *            all {@link Extension}'s will be deleted if this Set contains 'extensions'
     */
    void update(Map<String, Extension> extensions, UserEntity userEntity, Set<String> attributes) {

        if (attributes.contains("extensions")) {
            userEntity.removeAllExtensionFieldValues();
        }
        
        if (extensions != null) {
            for (Entry<String, Extension> extensionEntry : extensions.entrySet()) {
                updateExtension(extensionEntry, userEntity);
            }
        }
    }
    
    private void updateExtension(Entry<String, Extension> extensionEntry, UserEntity userEntity) {
        String urn = extensionEntry.getKey();
        Extension updatedExtension = extensionEntry.getValue();
        ExtensionEntity extensionEntity = extensionDao.getExtensionByUrn(urn);

        for (ExtensionFieldEntity extensionField : extensionEntity.getFields()) {
            String fieldName = extensionField.getName();
            ExtensionFieldValueEntity extensionFieldValue = findExtensionFieldValue(extensionField, userEntity);

            boolean isFieldPresent = updatedExtension.isFieldPresent(fieldName);
            if (extensionFieldValue == null && !isFieldPresent) {
                continue;
            } else if (extensionFieldValue != null && !isFieldPresent) {
                continue;
            }

            String newValue = getNewExtensionValue(extensionField, updatedExtension, fieldName);
            if (newValue == null) {
                continue;
            }

            extensionFieldValue.setValue(newValue);
            extensionFieldValue.setExtensionField(extensionField);
            userEntity.addOrUpdateExtensionValue(extensionFieldValue);
        }
    }
    
    private String getNewExtensionValue(ExtensionFieldEntity extensionField, Extension updatedExtension,
            String fieldName) {
        String newValue = updatedExtension.getField(fieldName, ExtensionFieldType.STRING);
        if (newValue != null &&
                (extensionField.getType() == ExtensionFieldType.INTEGER
                || extensionField.getType() == ExtensionFieldType.DECIMAL)) {
            newValue = numberPadder.pad(newValue);
        }
        return newValue;
    }

    private ExtensionFieldValueEntity findExtensionFieldValue(ExtensionFieldEntity extensionField, UserEntity userEntity) {
        for (ExtensionFieldValueEntity extensionFieldValue : userEntity.getExtensionFieldValues()) {
            if (extensionFieldValue.getExtensionField().equals(extensionField)) {
                return extensionFieldValue;
            }
        }

        return null;
    }
}