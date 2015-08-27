package org.osiam.resources.provisioning;

import org.osiam.resources.exception.NoSuchElementException;
import org.osiam.resources.exception.OsiamBackendFailureException;
import org.osiam.resources.provisioning.model.ExtensionDefinition;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Extension.Field;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.dao.ExtensionDao;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(SCIMExtensionProvisioning.class);

    @Autowired
    private ExtensionDao dao;

    /**
     * Get all persisted extension definitions.
     *
     * @return A list of all {@link ExtensionDefinition} that are persisted.
     */
    public List<ExtensionDefinition> getAllExtensionDefinitions() {
        List<Extension> extensions = fromEntity(dao.getAllExtensions());
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
                        LOGGER.warn("Unable to contruct URI for http://www.osiam.org This should never happen!", e);
                        throw new OsiamBackendFailureException();
                    }
                    break;
                default:
                    throw new NoSuchElementException("Type '" + fieldEntity.getType().getName() + "' does not exist");
            }
        }
    }
}
