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
package org.osiam.scim.extension;

import org.hibernate.validator.constraints.NotEmpty;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.ExtensionRepository;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toSet;

@Component
@ConfigurationProperties("osiam.scim")
public class ExtensionsConfiguration {

    private final ExtensionRepository extensionRepository;
    private final List<Extension> extensions = new ArrayList<>();

    @Autowired
    public ExtensionsConfiguration(ExtensionRepository extensionRepository) {
        this.extensionRepository = extensionRepository;
    }

    @PostConstruct
    public void createExtensions() {
        extensions.stream()
                .filter(extension -> !extensionRepository.existsByUrnIgnoreCase(extension.getUrn()))
                .map(Extension::toEntity)
                .forEach(extensionRepository::saveAndFlush);
    }

    @Valid
    public List<Extension> getExtensions() {
        return extensions;
    }

    public static class Extension {

        @NotEmpty(message = "No URN defined for extension")
        private String urn;

        @NotEmpty(message = "No fields defined for extension")
        @Valid
        private final List<Field> fields = new ArrayList<>();

        public ExtensionEntity toEntity(){
            ExtensionEntity entity = new ExtensionEntity();
            entity.setUrn(urn);
            entity.setFields(fields.stream().map(Field::toEntity).collect(toSet()));
            return entity;
        }

        public String getUrn() {
            return urn;
        }

        public void setUrn(String urn) {
            this.urn = urn;
        }

        public List<Field> getFields() {
            return fields;
        }
    }

    public static class Field {

        @NotEmpty(message = "No name defined for field")
        private String name;

        @NotNull(message = "No type defined for field")
        private ExtensionFieldType type;

        private boolean required;

        public ExtensionFieldEntity toEntity(){
            ExtensionFieldEntity entity = new ExtensionFieldEntity();
            entity.setName(name);
            entity.setType(type);
            entity.setRequired(required);
            return entity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type != null ? type.toString() : null;
        }

        public void setType(String type) {
            this.type = ExtensionFieldType.valueOf(type.toUpperCase());
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
