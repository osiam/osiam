/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.provisioning;

import org.osiam.resources.exception.NoSuchElementException;
import org.osiam.resources.exception.OsiamBackendFailureException;
import org.osiam.resources.provisioning.model.ExtensionDefinition;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Extension.Field;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.ExtensionRepository;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

@Service
@Transactional
public class SCIMExtensionProvisioning {

    private static final Logger logger = LoggerFactory.getLogger(SCIMExtensionProvisioning.class);

    private final ExtensionRepository repository;

    @Autowired
    public SCIMExtensionProvisioning(ExtensionRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all persisted extension definitions.
     *
     * @return A list of all {@link ExtensionDefinition} that are persisted.
     */
    public List<ExtensionDefinition> getAllExtensionDefinitions() {
        List<Extension> extensions = fromEntity(repository.findAll());
        final List<ExtensionDefinition> extensionDefinitions = new ArrayList<>();

        for (Extension extension : extensions) {
            final ExtensionDefinition extensionDefinition = new ExtensionDefinition(extension.getUrn());

            for (Entry<String, Field> fieldEntry : extension.getFields().entrySet()) {
                final String field = fieldEntry.getKey();
                final ExtensionFieldType<?> type = fieldEntry.getValue().getType();

                extensionDefinition.addPair(field, type);
            }

            extensionDefinitions.add(extensionDefinition);
        }

        return extensionDefinitions;
    }

    private List<Extension> fromEntity(List<ExtensionEntity> entities) {
        List<Extension> result = new ArrayList<>();

        for (ExtensionEntity entity : entities) {
            Extension.Builder builder = new Extension.Builder(entity.getUrn());
            setField(entity, builder);

            result.add(builder.build());
        }

        return result;
    }

    private void setField(ExtensionEntity entity, Extension.Builder builder) {
        for (ExtensionFieldEntity fieldEntity : entity.getFields()) {
            switch (fieldEntity.getType().getName()) {
                case "STRING":
                    builder.setField(fieldEntity.getName(), "null");
                    break;
                case "INTEGER":
                    builder.setField(fieldEntity.getName(), BigInteger.ZERO);
                    break;
                case "DECIMAL":
                    builder.setField(fieldEntity.getName(), BigDecimal.ZERO);
                    break;
                case "BOOLEAN":
                    builder.setField(fieldEntity.getName(), false);
                    break;
                case "DATE_TIME":
                    builder.setField(fieldEntity.getName(), new Date(0L));
                    break;
                case "BINARY":
                    builder.setField(fieldEntity.getName(), ByteBuffer.wrap(new byte[]{}));
                    break;
                case "REFERENCE":
                    try {
                        builder.setField(fieldEntity.getName(), new URI("http://www.osiam.org"));
                    } catch (URISyntaxException e) {
                        logger.warn("Unable to contruct URI for http://www.osiam.org This should never happen!", e);
                        throw new OsiamBackendFailureException();
                    }
                    break;
                default:
                    throw new NoSuchElementException("Type '" + fieldEntity.getType().getName() + "' does not exist");
            }
        }
    }
}
