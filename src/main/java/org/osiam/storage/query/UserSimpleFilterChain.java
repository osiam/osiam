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

import org.osiam.resources.exception.OsiamException;
import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.UserEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserSimpleFilterChain implements FilterChain<UserEntity> {

    private final FilterExpression<UserEntity> filterExpression;

    private final QueryField<UserEntity> userFilterField;
    private final ExtensionDao extensionDao;
    private final CriteriaBuilder criteriaBuilder;
    private final ExtensionQueryField extensionFilterField;

    public UserSimpleFilterChain(CriteriaBuilder criteriaBuilder, ExtensionDao extensionDao,
                                 FilterExpression<UserEntity> filterExpression) {
        this.criteriaBuilder = criteriaBuilder;
        this.extensionDao = extensionDao;
        this.filterExpression = filterExpression;

        if (filterExpression.getField().isExtension()) {
            extensionFilterField = getExtensionFilterField(filterExpression);
            userFilterField = null;
        } else {
            userFilterField = filterExpression.getField().getQueryField();
            extensionFilterField = null;
        }
    }

    private ExtensionQueryField getExtensionFilterField(FilterExpression<UserEntity> filterExpression) {
        final ExtensionEntity extension;
        try {
            extension = extensionDao.getExtensionByUrn(filterExpression.getField().getUrn(), true);
        } catch (OsiamException ex) {
            throw new IllegalArgumentException(String.format("Filtering not possible. Field '%s' not available.", filterExpression.getField()));
        }
        final ExtensionFieldEntity fieldEntity = extension.getFieldForName(filterExpression.getField().getName(), true);
        return new ExtensionQueryField(filterExpression.getField().getUrn(), fieldEntity);
    }

    @Override
    public Predicate createPredicateAndJoin(Root<UserEntity> root) {
        if (userFilterField != null) {
            return userFilterField.addFilter(root, filterExpression.getConstraint(), filterExpression.getValue(),
                    criteriaBuilder);
        } else {
            return extensionFilterField.addFilter(root, filterExpression.getConstraint(), filterExpression.getValue(),
                    criteriaBuilder);
        }
    }
}
