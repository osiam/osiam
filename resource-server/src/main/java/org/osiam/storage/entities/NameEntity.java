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

    @Column
    private String formatted;

    @Column
    private String familyName;

    @Column
    private String givenName;

    @Column
    private String middleName;

    @Column
    private String honorificPrefix;

    @Column
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
    public boolean equals(Object o) { // NOSONAR - Cyclomatic Complexity can be > 10
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NameEntity other = (NameEntity) o;

        if (familyName != null ? !familyName.equals(other.familyName) : other.familyName != null) {
            return false;
        }
        if (formatted != null ? !formatted.equals(other.formatted) : other.formatted != null) {
            return false;
        }
        if (givenName != null ? !givenName.equals(other.givenName) : other.givenName != null) {
            return false;
        }
        if (honorificPrefix != null ? !honorificPrefix.equals(other.honorificPrefix) : other.honorificPrefix != null) {
            return false;
        }
        if (honorificSuffix != null ? !honorificSuffix.equals(other.honorificSuffix) : other.honorificSuffix != null) {
            return false;
        }
        if (middleName != null ? !middleName.equals(other.middleName) : other.middleName != null) {
            return false;
        }

        return true;
    }

    @Override                // NOSONAR : Cyclomatic complexity can't be reduced
    public int hashCode() {  // NOSONAR : Cyclomatic complexity can't be reduced
        final int prime = 31;
        int result = formatted != null ? formatted.hashCode() : 0;
        result = prime * result + (familyName != null ? familyName.hashCode() : 0);
        result = prime * result + (givenName != null ? givenName.hashCode() : 0);
        result = prime * result + (middleName != null ? middleName.hashCode() : 0);
        result = prime * result + (honorificPrefix != null ? honorificPrefix.hashCode() : 0);
        result = prime * result + (honorificSuffix != null ? honorificSuffix.hashCode() : 0);
        return result;
    }
}