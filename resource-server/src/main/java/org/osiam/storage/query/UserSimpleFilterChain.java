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

package org.osiam.storage.query;

import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.helper.NumberPadder;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Locale;

public class UserSimpleFilterChain implements FilterChain<UserEntity> {

    private final ScimExpression scimExpression;

    private final QueryField<UserEntity> userFilterField;

    private ExtensionQueryField extensionFilterField;

    private final ExtensionDao extensionDao;
    private final CriteriaBuilder criteriaBuilder;
    private final NumberPadder numberPadder;

    public UserSimpleFilterChain(CriteriaBuilder criteriaBuilder, ExtensionDao extensionDao, ScimExpression scimExpression,
            NumberPadder numberPadder) {
        this.criteriaBuilder = criteriaBuilder;
        this.extensionDao = extensionDao;
        this.numberPadder = numberPadder;
        this.scimExpression = scimExpression;

        userFilterField = UserQueryField.fromString(scimExpression.getField().toLowerCase(Locale.ENGLISH));

        // It's not a known user field, so try to build a extension filter
        if (userFilterField == null) {
            extensionFilterField = getExtensionFilterField(scimExpression.getField().toLowerCase(Locale.ENGLISH));
        }
    }

    private ExtensionQueryField getExtensionFilterField(String fieldString) {
        int lastIndexOf = fieldString.lastIndexOf('.');
        if (lastIndexOf == -1) {
            throw new IllegalArgumentException("Filtering not possible. Field '" + scimExpression.getField() + "' not available.");
        }

        String urn = fieldString.substring(0, lastIndexOf);
        String fieldName = fieldString.substring(lastIndexOf + 1);
        final ExtensionEntity extension;
        try {
            extension = extensionDao.getExtensionByUrn(urn, true);
        } catch (NoResultException ex) {
            throw new IllegalArgumentException("Filtering not possible. Field '" + scimExpression.getField() + "' not available.", ex);
        }
        final ExtensionFieldEntity fieldEntity = extension.getFieldForName(fieldName, true);
        return new ExtensionQueryField(urn, fieldEntity, numberPadder);
    }

    @Override
    public Predicate createPredicateAndJoin(Root<UserEntity> root) {
        if (userFilterField != null) {
            return userFilterField.addFilter(root, scimExpression.getConstraint(), scimExpression.getValue(), criteriaBuilder);
        } else if (extensionFilterField != null) {
            return extensionFilterField.addFilter(root, scimExpression.getConstraint(), scimExpression.getValue(), criteriaBuilder);
        } else {
            throw new IllegalArgumentException("Filtering not possible. Field '" + scimExpression.getField() + "' not available.");
        }
    }

}