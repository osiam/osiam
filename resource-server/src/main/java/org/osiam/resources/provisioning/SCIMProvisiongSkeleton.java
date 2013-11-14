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

import java.util.GregorianCalendar;

import org.osiam.resources.converter.Converter;
import org.osiam.resources.converter.ResourceConverter;
import org.osiam.resources.scim.Resource;
import org.osiam.storage.dao.GenericDAO;
import org.osiam.storage.entities.InternalIdSkeleton;

public abstract class SCIMProvisiongSkeleton<T extends Resource, E extends InternalIdSkeleton> implements SCIMProvisioning<T> {

    protected abstract GenericDAO<E> getDao();

    protected abstract Converter<T, E> getConverter();

    public abstract T create(T resource);

    @Override
    public T getById(String id) {
        return getConverter().toScim(getDao().getById(id));
    }

    protected abstract SCIMEntities getScimEntities();

    protected void setFieldsWrapException(GenericSCIMToEntityWrapper genericSCIMToEntityWrapper) {
        try {
            genericSCIMToEntityWrapper.setFields();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException("This should not happen.", e);
        }
    }

    @Override
    public T update(String id, T resource) {

        E entity = getDao().getById(id);

        GenericSCIMToEntityWrapper genericSCIMToEntityWrapper = new GenericSCIMToEntityWrapper(getTarget(), resource,
                entity, GenericSCIMToEntityWrapper.Mode.PATCH, getScimEntities());
        setFieldsWrapException(genericSCIMToEntityWrapper);

        return updateLastModified(entity);
    }

    @Override
    public void delete(String id) {
        getDao().delete(id);
    }

    public abstract GenericSCIMToEntityWrapper.For getTarget();

    private T updateLastModified(E resource) {
        resource.getMeta().setLastModified(GregorianCalendar.getInstance().getTime());
        return getConverter().toScim(getDao().update(resource));
    }
}
