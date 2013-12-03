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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.storage.entities.*;

public class EntityListFieldWrapper {


    private final GenericSCIMToEntityWrapper.Mode mode;
    private Object entity;

    public EntityListFieldWrapper(Object entity, GenericSCIMToEntityWrapper.Mode mode) {
        this.entity = entity;
        this.mode = mode;
    }

    public void set(Object userValue, SCIMEntities.Entity attributes, Field field) {
        if (mode == GenericSCIMToEntityWrapper.Mode.PATCH && (userValue == null || ((Collection) userValue).isEmpty())) {
            return;
        }
        wrapExceptions(userValue, attributes, field);

    }

    private void wrapExceptions(Object userValue, SCIMEntities.Entity attributes, Field field) {
        try {
            updateListFields(userValue, attributes, field);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private void updateListFields(Object userValue, SCIMEntities.Entity attributes, Field field)
            throws IllegalAccessException, InstantiationException {
        if (attributes.isNotMultiValue()) {
            setNeedToBeReplacedCompletely(userValue, field, attributes.getClazz());
        } else {
            updateMultiValueList(userValue, attributes, field);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateMultiValueList(Object userValue, SCIMEntities.Entity attributes, Field field)
            throws IllegalAccessException, InstantiationException {
        Object o = getFieldObject(field);
        Class<?> clazz = attributes.getClazz();
        updateList((Collection<Object>) o, clazz, (Collection) userValue);
    }

    private Object getFieldObject(Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(entity);
    }

    private void updateList(Collection<Object> targetList, Class<?> clazz, Collection listOfMultiValue)
            throws InstantiationException, IllegalAccessException {
        clearIfNotInPatchMode(targetList);
        if (listOfMultiValue == null) { return; }
        for (Object o : listOfMultiValue) {
            MultiValuedAttribute m = (MultiValuedAttribute) o;
            if (notDeleted(m, targetList)) {
                addSingleObject(clazz, targetList, m);
            }
        }
    }

    private boolean notDeleted(MultiValuedAttribute m, Collection<Object> targetList) {
        boolean equals = "delete".equals(m.getOperation());
        if (equals) { seekAndDelete(m, targetList); }
        return !equals;

    }

    private void seekAndDelete(MultiValuedAttribute m, Collection<Object> targetList) {
        for (Object o : targetList) {
            if (deleteSingleAttribute(m, targetList, o)) {
                return;
            }
        }
    }

    private boolean deleteSingleAttribute(MultiValuedAttribute m, Collection<Object> targetList, Object o) {
        ChildOfMultiValueAttribute valueAttribute = (ChildOfMultiValueAttribute) o;
        String value = valueAttribute.getValue().toUpperCase(Locale.ENGLISH);
        Object mValue = String.valueOf(m.getValue()).toUpperCase(Locale.ENGLISH);
        return mValue.equals(value) && targetList.remove(o);
    }

    private void addSingleObject(Class<?> clazz, Collection<Object> collection, MultiValuedAttribute m)
            throws InstantiationException, IllegalAccessException {
        Object target = createSingleObject(clazz, m);
        removeExistingValues(collection, m);
        collection.add(target);

    }

    private void removeExistingValues(Collection<Object> collection, MultiValuedAttribute multiValuedAttribute) {
        if (mode != GenericSCIMToEntityWrapper.Mode.PATCH) {
            return;
        }
        for (Object o : collection) {
            if (removeWhenValueExists(collection, o, multiValuedAttribute)) {
                return;
            }

        }
    }

    private boolean removeWhenValueExists(Collection<Object> collection, Object o,
                                          MultiValuedAttribute multiValuedAttribute) {
        ChildOfMultiValueAttribute entityValue = (ChildOfMultiValueAttribute) o;
        boolean valueIsEqual = entityValue.getValue().equals(multiValuedAttribute.getValue());
        return valueIsEqual && collection.remove(o);
    }

    private Object createSingleObject(Class<?> clazz, MultiValuedAttribute m)
            throws InstantiationException, IllegalAccessException {
        Object target = clazz.newInstance();

        ((ChildOfMultiValueAttribute) target).setValue(m.getValue() == null ? null : String.valueOf(m.getValue()));

        if (target instanceof ChildOfMultiValueAttributeWithIdAndType) {
            ((ChildOfMultiValueAttributeWithIdAndType) target).setType(m.getType());
        }
        if (target instanceof ChildOfMultiValueAttributeWithIdAndTypeAndPrimary) {
            ((ChildOfMultiValueAttributeWithIdAndTypeAndPrimary) target)
                    .setPrimary(m.isPrimary() != null ? m.isPrimary() : false);
        }
        addHibernateUserReferenceToEntityIfNeeded(target);
        return target;
    }

    //TODO: Ugly stuff, rework hole generic PATCH mechanism
    private void addHibernateUserReferenceToEntityIfNeeded(Object target) {
        if (target instanceof EmailEntity) {
            ((EmailEntity) target).setUser((UserEntity) entity);
        } else if (target instanceof PhoneNumberEntity ) {
            ((PhoneNumberEntity) target).setUser((UserEntity) entity);
        } else if (target instanceof ImEntity) {
            ((ImEntity) target).setUser((UserEntity) entity);
        } else if (target instanceof PhotoEntity) {
            ((PhotoEntity) target).setUser((UserEntity) entity);
        } else if (target instanceof X509CertificateEntity) {
            ((X509CertificateEntity) target).setUser((UserEntity) entity);
        }
    }

    private void clearIfNotInPatchMode(Collection<?> collection) {
        if (mode != GenericSCIMToEntityWrapper.Mode.PATCH && collection != null) {
            collection.clear();
        }
    }

    @SuppressWarnings("unchecked")
    void setNeedToBeReplacedCompletely(Object userValue, Field field, Class<?> clazz)
            throws IllegalAccessException, InstantiationException {
        Object object = getFieldObject(field);
        if (object != null) {
            Collection collection = (Collection) object;
            collection.clear();
            if (userValue != null) {
                for (Object o : ((Collection) userValue)) {
                    collection.add(setSingleFields(clazz, o));
                }
            }
        }


    }

    private Object setSingleFields(Class<?> clazz, Object o) throws IllegalAccessException, InstantiationException {
        GetFieldsOfInputAndTarget fields = new GetFieldsOfInputAndTarget(o.getClass(), clazz);
        Object result = clazz.newInstance();
        EntityFieldWrapper entityFieldWrapper = new EntityFieldWrapper(result, mode);
        for (Map.Entry<String, Field> e : fields.getInputFields().entrySet()) {
            setSingleField(o, fields, entityFieldWrapper, e);
        }
        return result;
    }

    private void setSingleField(Object o, GetFieldsOfInputAndTarget fields, EntityFieldWrapper entityFieldWrapper,
                                Map.Entry<String, Field> e) throws IllegalAccessException, InstantiationException {
        Field entityField = fields.getTargetFields().get(e.getKey());
        Field field = fields.getInputFields().get(e.getKey());
        field.setAccessible(true);
        Object userValue = field.get(o);
        entityFieldWrapper.updateSingleField(entityField, userValue, e.getKey());
    }
}