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

package org.osiam.storage.entities;

import java.net.MalformedURLException;
import java.net.URL;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.osiam.resources.exceptions.OsiamException;
import org.osiam.resources.scim.Photo;

/**
 * Photos Entity
 */
@Entity
@Table(name = "scim_photo")
public class PhotoEntity extends BaseMultiValuedAttributeEntityWithValue {

    /**
     * <p>
     * The type of this Photo.
     * </p>
     * 
     * <p>
     * Custom type mapping is provided by {@link org.osiam.storage.entities.jpa_converters.PhotoTypeConverter}.
     * </p>
     */
    @Basic
    private Photo.Type type; // @Basic is needed for JPA meta model generator

    @Override
    public void setValue(String value) {
        ensureGivenValueIsAnUrl(value);
        super.setValue(value);
    }

    private void ensureGivenValueIsAnUrl(String value) {
        try {
            new URL(value);
        } catch (MalformedURLException e) {
            throw new OsiamException("The given value MUST be an URL pointing to an photo.", e);
        }
    }

    public Photo.Type getType() {
        return type;
    }

    public void setType(Photo.Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (type == null ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PhotoEntity other = (PhotoEntity) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PhotoEntity [type=").append(type).append(", getValue()=").append(getValue()).append("]");
        return builder.toString();
    }

}