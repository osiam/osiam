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


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.osiam.resources.scim.MultiValuedAttribute;

/**
 * Email Entity
 */
@Entity(name = "scim_email")
public class EmailEntity extends MultiValueAttributeEntitySkeleton implements HasUser, ChildOfMultiValueAttributeWithIdAndTypeAndPrimary, Serializable{

    private static final long serialVersionUID = -6535056565639057057L;
    
    @Column
    @Enumerated(EnumType.STRING)
    private CanonicalEmailTypes type;

    
    @Column(name = "postgresql_does_not_like_primary")
    private boolean primary;

    @ManyToOne(optional = false)
    private UserEntity user;

    @Override
    public String getType() {
        if(type != null) {
            return type.toString();
        }
        return null;
    }

    @Override
    public void setType(String type) {
        if(type != null) {
            this.type = CanonicalEmailTypes.valueOf(type);
        }
    }

    @Override
    public boolean isPrimary() {
        return primary;
    }

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public MultiValuedAttribute toScim() {
        return new MultiValuedAttribute.Builder().
                setPrimary(isPrimary()).
                setType(getType()).
                setValue(getValue()).
                build();
    }

    public static EmailEntity fromScim(MultiValuedAttribute multiValuedAttribute) {
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setType(multiValuedAttribute.getType());
        emailEntity.setValue(String.valueOf(multiValuedAttribute.getValue()));
        emailEntity.setPrimary((multiValuedAttribute.isPrimary() == null ? false : multiValuedAttribute.isPrimary()));
        return emailEntity;
    }

    @Override
    public UserEntity getUser() {
        return user;
    }

    @Override
    public void setUser(UserEntity user) {
        this.user = user;
    }

    public enum CanonicalEmailTypes {
        work, home, other
    }
}