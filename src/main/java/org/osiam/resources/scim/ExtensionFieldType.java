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

package org.osiam.resources.scim;

import com.google.common.io.BaseEncoding;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This enum like class represents the valid extension field types. Instances of this class also define methods for
 * converting these types from and to {@link String}.
 *
 * @param <T> the actual type this {@link ExtensionFieldType} represents
 */
public abstract class ExtensionFieldType<T> implements Serializable {

    /**
     * ExtensionFieldType for the Scim type String (actual type is {@link String})
     */
    public static final ExtensionFieldType<String> STRING = new ExtensionFieldType<String>("STRING") {

        @Override
        public String fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            return stringValue;
        }

        @Override
        public String toString(String value) {
            ensureValueIsNotNull(value);
            return value;
        }

    };
    /**
     * ExtensionFieldType for the Scim type Integer (actual type is {@link BigInteger})
     */
    public static final ExtensionFieldType<BigInteger> INTEGER = new ExtensionFieldType<BigInteger>("INTEGER") {

        @Override
        public BigInteger fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return new BigInteger(stringValue);
            } catch (NumberFormatException e) {
                throw createConversionException(stringValue, "BigInteger", e);
            }
        }

        @Override
        public String toString(BigInteger value) {
            ensureValueIsNotNull(value);
            return value.toString();
        }

    };
    /**
     * ExtensionFieldType for the Scim type Decimal (actual type is {@link BigDecimal})
     */
    public static final ExtensionFieldType<BigDecimal> DECIMAL = new ExtensionFieldType<BigDecimal>("DECIMAL") {

        @Override
        public BigDecimal fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return new BigDecimal(stringValue);
            } catch (NumberFormatException e) {
                throw createConversionException(stringValue, "BigDecimal", e);
            }
        }

        @Override
        public String toString(BigDecimal value) {
            ensureValueIsNotNull(value);
            return value.toString();
        }

    };
    /**
     * ExtensionFieldType for the Scim type Boolean (actual type is {@link Boolean})
     */
    public static final ExtensionFieldType<Boolean> BOOLEAN = new ExtensionFieldType<Boolean>("BOOLEAN") {

        @Override
        public Boolean fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            if (!stringValue.equals("true") && !stringValue.equals("false")) {
                throw createConversionException(stringValue, "Boolean");
            }
            return Boolean.valueOf(stringValue);
        }

        @Override
        public String toString(Boolean value) {
            ensureValueIsNotNull(value);
            return value.toString();
        }

    };
    /**
     * ExtensionFieldType for the Scim type Binary (actual type is {@link ByteBuffer})
     */
    public static final ExtensionFieldType<ByteBuffer> BINARY = new ExtensionFieldType<ByteBuffer>("BINARY") {

        @Override
        public ByteBuffer fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return ByteBuffer.wrap(BaseEncoding.base64().decode(stringValue));
            } catch (IllegalArgumentException e) {
                throw createConversionException(stringValue, "byte[]", e);
            }
        }

        @Override
        public String toString(ByteBuffer value) {
            ensureValueIsNotNull(value);
            return BaseEncoding.base64().encode(value.array());
        }

    };
    /**
     * ExtensionFieldType for the Scim type Reference (actual type is {@link URI})
     */
    public static final ExtensionFieldType<URI> REFERENCE = new ExtensionFieldType<URI>("REFERENCE") {

        @Override
        public URI fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                return new URI(stringValue);
            } catch (URISyntaxException e) {
                throw createConversionException(stringValue, "URI", e);
            }
        }

        @Override
        public String toString(URI value) {
            ensureValueIsNotNull(value);
            return value.toString();
        }

    };
    private static final long serialVersionUID = 5665143978696725609L;
    /**
     * Since this field can not be serialized, it will be created when the object is de-serialized (see readObject()).
     */
    private static DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();
    /**
     * ExtensionFieldType for the Scim type DateTime (actual type is {@link Date}). Valid values are in ISO
     * DateTimeFormat with the timeZone UTC like '2011-08-01T18:29:49.000Z'
     */
    public static final ExtensionFieldType<Date> DATE_TIME = new ExtensionFieldType<Date>("DATE_TIME") {
        // it is ok if the inner class is over 20 characters long


        @Override
        public Date fromString(String stringValue) {
            ensureValueIsNotNull(stringValue);
            try {
                long millis = dateTimeFormatter.parseDateTime(stringValue).getMillis();
                return new Date(millis);
            } catch (NumberFormatException e) {
                throw createConversionException(stringValue, "Date", e);
            }
        }

        @Override
        public String toString(Date value) {
            ensureValueIsNotNull(value);
            return dateTimeFormatter.print(value.getTime());
        }

    };
    private String name;

    private ExtensionFieldType(String name) {
        this.name = name;
    }

    /**
     * Retrieves a {@link ExtensionFieldType} by its name.
     *
     * @param name the name of the {@link ExtensionFieldType} as it is returned by toString()
     * @return the {@link ExtensionFieldType} based on the given name
     * @throws IllegalArgumentException if there is no {@link ExtensionFieldType} with the given name
     */
    public static ExtensionFieldType<?> valueOf(String name) {
        switch (name) {
            case "STRING":
                return STRING;
            case "INTEGER":
                return INTEGER;
            case "DECIMAL":
                return DECIMAL;
            case "BOOLEAN":
                return BOOLEAN;
            case "DATE_TIME":
                return DATE_TIME;
            case "BINARY":
                return BINARY;
            case "REFERENCE":
                return REFERENCE;
            default:
                throw new IllegalArgumentException("Type " + name + " does not exist");
        }
    }

    /**
     * Converts the given {@link String} to the actual type.
     *
     * @param stringValue the {@link String} value to be converted
     * @return the given {@link String} value converted to the actual Type
     */
    public abstract T fromString(String stringValue);

    /**
     * Converts a value of the actual type to {@link String}.
     *
     * @param value the value to be converted
     * @return the given value as {@link String}
     */
    public abstract String toString(T value);

    /**
     * Returns the name of the {@link ExtensionFieldType}
     *
     * @return the name of the {@link ExtensionFieldType}
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns a string representation of the {@link ExtensionFieldType} which is its name.
     */
    @Override
    public String toString() {
        return getName();
    }

    protected IllegalArgumentException createConversionException(String stringValue, String targetType, Throwable cause) {
        IllegalArgumentException exception = createConversionException(stringValue, targetType);
        exception.initCause(cause);
        return exception;
    }

    protected IllegalArgumentException createConversionException(String stringValue, String targetType) {
        return new IllegalArgumentException("The value " + stringValue + " cannot be converted into a " + targetType
                + ".");
    }

    protected void ensureValueIsNotNull(Object value) {
        checkArgument(value != null, "The given value cannot be null.");
    }
}
