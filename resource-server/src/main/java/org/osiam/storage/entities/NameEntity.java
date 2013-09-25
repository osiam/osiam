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

import org.osiam.resources.scim.Name;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Name Entity
 */
@Entity(name = "scim_name")
public class NameEntity implements Serializable {

    private static final long serialVersionUID = -6535056565659057058L;

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

    public Name toScim() {
        return new Name.Builder().
                setFamilyName(getFamilyName()).
                setFormatted(getFormatted()).
                setGivenName(getGivenName()).
                setHonorificPrefix(getHonorificPrefix()).
                setHonorificSuffix(getHonorificSuffix()).
                setMiddleName(getMiddleName()).
                build();
    }

    public static NameEntity fromScim(Name name) {
        NameEntity nameEntity = new NameEntity();
        if (name != null) {
            nameEntity.setFamilyName(name.getFamilyName());
            nameEntity.setFormatted(name.getFormatted());
            nameEntity.setGivenName(name.getGivenName());
            nameEntity.setHonorificPrefix(name.getHonorificPrefix());
            nameEntity.setHonorificSuffix(name.getHonorificSuffix());
            nameEntity.setMiddleName(name.getMiddleName());
        }
        return nameEntity;
    }
}