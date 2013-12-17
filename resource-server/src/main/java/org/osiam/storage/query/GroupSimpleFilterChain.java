/*
 * Copyright 2013
 *     tarent AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osiam.storage.query;

import org.osiam.storage.entities.GroupEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Locale;

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
            throw new IllegalArgumentException("Filtering not possible. Field '" + scimExpression.getField() + "' not available.");
        }

        return filterField.addFilter(root, scimExpression.getConstraint(), scimExpression.getValue(), criteriaBuilder);
    }

}