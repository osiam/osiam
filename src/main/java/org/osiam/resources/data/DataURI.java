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
package org.osiam.resources.data;

import com.google.common.base.Strings;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.osiam.resources.exception.SCIMDataValidationException;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A URI of the form data:[<mediatype>][;base64],<data>
 */
public class DataURI {

    public static final String DATA = "data:";
    public static final String BASE64 = ";base64,";
    private URI dataUri;

    /**
     * @param dataUri A String presenting a URI of the form data:[<mediatype>][;base64],<data>
     * @throws SCIMDataValidationException if the dataUri violates RFC 2396, as augmented by the above deviations
     */
    public DataURI(String dataUri) {
        if (Strings.isNullOrEmpty(dataUri)) {
            throw new SCIMDataValidationException("The given string can't be null or empty.");
        }
        if (!dataUri.startsWith(DATA) || !dataUri.contains(BASE64)) {
            throw new SCIMDataValidationException("The given string '" + dataUri + "' is not a data URI.");
        }
        try {
            this.dataUri = new URI(dataUri);
        } catch (URISyntaxException e) {
            throw new SCIMDataValidationException(e.getMessage(), e);
        }
    }

    /**
     * @param dataUri A URI of the form data:[<mediatype>][;base64],<data>
     * @throws SCIMDataValidationException if the URI doesn't expects the schema
     */
    public DataURI(URI dataUri) {
        if (dataUri == null) {
            throw new SCIMDataValidationException("The given dataUri can't be null.");
        }
        if (!dataUri.toString().startsWith(DATA) || !dataUri.toString().contains(BASE64)) {
            throw new SCIMDataValidationException("The given URI '" + dataUri.toString() + "' is not a data URI.");
        }
        this.dataUri = dataUri;
    }

    /**
     * @param inputStream a inputStream which will be transformed into an DataURI
     * @throws IOException                 if the stream can not be read or is closed
     * @throws SCIMDataValidationException if the inputStream can't be converted into an DataURI
     */
    public DataURI(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new SCIMDataValidationException("The given inputStream can't be null.");
        }
        String mimeType = new Tika().detect(inputStream);
        dataUri = convertInputStreamToDataURI(inputStream, mimeType);
    }

    private URI convertInputStreamToDataURI(InputStream inputStream, String mimeType) throws IOException {
        byte[] byteArrayPhoto = IOUtils.toByteArray(inputStream);
        String base64Photo = DatatypeConverter.printBase64Binary(byteArrayPhoto);

        StringBuilder uriStringBuilder = new StringBuilder();
        uriStringBuilder.append(DATA).append(mimeType)
                .append(BASE64).append(base64Photo);

        URI retDataUri;
        try {
            retDataUri = new URI(uriStringBuilder.toString());
        } catch (URISyntaxException e) {
            throw new SCIMDataValidationException(e.getMessage(), e);
        }
        return retDataUri;
    }

    /**
     * @return gets the dataURI as java.net.URI
     */
    public URI getAsURI() {
        return dataUri;
    }

    /**
     * @return gets the dataURI as InputStream
     */
    public InputStream getAsInputStream() {
        String imageCode = dataUri.toString().substring(dataUri.toString().indexOf(BASE64) + BASE64.length());

        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(imageCode);
        return new ByteArrayInputStream(decodedBytes);
    }

    /**
     * a mime type e.g. image/png
     *
     * @return the mime type of the DataURI
     */
    public String getMimeType() {
        String uriString = dataUri.toString();
        return uriString.substring(DATA.length(), uriString.indexOf(BASE64));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataUri == null) ? 0 : dataUri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataURI other = (DataURI) obj;
        if (dataUri == null) {
            if (other.dataUri != null) {
                return false;
            }
        } else if (!dataUri.equals(other.dataUri)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return dataUri.toString();
    }

}
