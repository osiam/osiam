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
package org.osiam.resources.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Extension.Field;
import org.osiam.resources.scim.ExtensionFieldType;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

public class ExtensionSerializer extends JsonSerializer<Extension> {

    @Override
    public void serialize(Extension value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        Map<String, Field> fields = value.getFields();
        for (Entry<String, Field> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            ExtensionFieldType<?> fieldType = entry.getValue().getType();
            String rawFieldValue = entry.getValue().getValue();

            jgen.writeFieldName(fieldName);

            if (fieldType == ExtensionFieldType.INTEGER) {
                BigInteger valueAsBigInteger = ExtensionFieldType.INTEGER.fromString(rawFieldValue);
                jgen.writeNumber(valueAsBigInteger);
            } else if (fieldType == ExtensionFieldType.DECIMAL) {
                BigDecimal valueAsBigDecimal = ExtensionFieldType.DECIMAL.fromString(rawFieldValue);
                jgen.writeNumber(valueAsBigDecimal);
            } else if (fieldType == ExtensionFieldType.BOOLEAN) {
                Boolean valueAsBoolean = ExtensionFieldType.BOOLEAN.fromString(rawFieldValue);
                jgen.writeBoolean(valueAsBoolean);
            } else {
                jgen.writeString(rawFieldValue);
            }
        }

        jgen.writeEndObject();
    }

}
