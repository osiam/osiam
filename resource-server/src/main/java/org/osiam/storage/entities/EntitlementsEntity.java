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

import org.osiam.resources.scim.MultiValuedAttribute;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Entitlements Entity
 */
@Entity(name = "scim_entitlements")
public class EntitlementsEntity extends MultiValueAttributeEntitySkeleton implements Serializable {

    private static final long serialVersionUID = -6534056565639057058L;

    public MultiValuedAttribute toScim() {
        return new MultiValuedAttribute.Builder().
                setValue(getValue()).
                build();
    }

    public static EntitlementsEntity fromScim(MultiValuedAttribute multiValuedAttribute) {
        EntitlementsEntity entitlementsEntity = new EntitlementsEntity();
        entitlementsEntity.setValue(String.valueOf(multiValuedAttribute.getValue()));
        return entitlementsEntity;
    }
}