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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Name Entity
 */
@Entity
@Table(name = "scim_name")
public class NameEntity {

    @Id
    @GeneratedValue
    private long id;

    private String formatted;

    private String familyName;

    private String givenName;

    private String middleName;

    private String honorificPrefix;

    private String honorificSuffix;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getHonorificPrefix() {
        return honorificPrefix;
    }

    public void setHonorificPrefix(String honorificPrefix) {
        this.honorificPrefix = honorificPrefix;
    }

    public String getHonorificSuffix() {
        return honorificSuffix;
    }

    public void setHonorificSuffix(String honorificSuffix) {
        this.honorificSuffix = honorificSuffix;
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
        NameEntity other = (NameEntity) obj;
        if (familyName == null) {
            if (other.familyName != null) {
                return false;
            }
        } else if (!familyName.equals(other.familyName)) {
            return false;
        }
        if (formatted == null) {
            if (other.formatted != null) {
                return false;
            }
        } else if (!formatted.equals(other.formatted)) {
            return false;
        }
        if (givenName == null) {
            if (other.givenName != null) {
                return false;
            }
        } else if (!givenName.equals(other.givenName)) {
            return false;
        }
        if (honorificPrefix == null) {
            if (other.honorificPrefix != null) {
                return false;
            }
        } else if (!honorificPrefix.equals(other.honorificPrefix)) {
            return false;
        }
        if (honorificSuffix == null) {
            if (other.honorificSuffix != null) {
                return false;
            }
        } else if (!honorificSuffix.equals(other.honorificSuffix)) {
            return false;
        }
        if (middleName == null) {
            if (other.middleName != null) {
                return false;
            }
        } else if (!middleName.equals(other.middleName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
        result = prime * result + ((formatted == null) ? 0 : formatted.hashCode());
        result = prime * result + ((givenName == null) ? 0 : givenName.hashCode());
        result = prime * result + ((honorificPrefix == null) ? 0 : honorificPrefix.hashCode());
        result = prime * result + ((honorificSuffix == null) ? 0 : honorificSuffix.hashCode());
        result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NameEntity [formatted=").append(formatted).append(", familyName=").append(familyName)
                .append(", givenName=").append(givenName).append(", middleName=").append(middleName)
                .append(", honorificPrefix=").append(honorificPrefix).append(", honorificSuffix=")
                .append(honorificSuffix).append("]");
        return builder.toString();
    }

}