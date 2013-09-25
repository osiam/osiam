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

package org.osiam.resources.provisioning;

import org.osiam.resources.scim.Name;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class EntityFieldWrapper {
    private final GenericSCIMToEntityWrapper.Mode mode;
    private final SetComplexType setComplexType;
    private Object entity;

    public EntityFieldWrapper(Object entity, GenericSCIMToEntityWrapper.Mode mode) {
        this.entity = entity;
        this.mode = mode;
        setComplexType = new SetComplexType(mode, entity);
    }

    public void updateSingleField(Field entityField, Object userValue, String key) throws IllegalAccessException, InstantiationException {
        if (mode == GenericSCIMToEntityWrapper.Mode.PATCH && (userValue == null || String.valueOf(userValue).isEmpty())) {
            return;
        }
        if (userValue instanceof Name) {
            setComplexType.setComplexType(entityField, userValue);
        } else {
            if (!("password".equals(key) && (userValue == null || String.valueOf(userValue).isEmpty()))) {
                updateSimpleField(entityField, userValue);
            }
        }
    }

    void updateSimpleField(final Field entityField, Object userValue) throws IllegalAccessException {
        if (entityField != null) {
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    // privileged code goes here
                    entityField.setAccessible(true);
                    return null; // nothing to return
                }
            });
            entityField.set(entity, userValue);
        }
    }
}