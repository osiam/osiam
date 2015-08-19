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

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.osiam.storage.entities.GroupEntity;

public class GroupSimpleFilterChain implements FilterChain<GroupEntity> {

    private final ScimExpression scimExpression;

    private final GroupQueryField filterField;
    private final CriteriaBuilder criteriaBuilder;

    public GroupSimpleFilterChain(CriteriaBuilder criteriaBuilder, ScimExpression scimExpression) {
        this.criteriaBuilder = criteriaBuilder;
        this.scimExpression = scimExpression;
        filterField = GroupQueryField.fromString(scimExpression.getField().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Predicate createPredicateAndJoin(Root<GroupEntity> root) {
        if (filterField == null) {
            throw new IllegalArgumentException(String.format("Unable to filter. Field '%s' not available.",
                    scimExpression.getField()));
        }

        return filterField.addFilter(root, scimExpression.getConstraint(), scimExpression.getValue(), criteriaBuilder);
    }

}
