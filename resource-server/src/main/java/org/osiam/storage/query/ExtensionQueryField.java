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

import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.entities.*;
import org.osiam.storage.helper.NumberPadder;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SetAttribute;

public class ExtensionQueryField {

    private final String urn;
    private final ExtensionFieldEntity field;
    private final NumberPadder numberPadder;

    public ExtensionQueryField(String urn, ExtensionFieldEntity field, NumberPadder numberPadder) {
        this.urn = urn;
        this.field = field;
        this.numberPadder = numberPadder;
    }

    public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                               String value, CriteriaBuilder cb) {

        if (constraint != FilterConstraint.PRESENT && (field.getType() == ExtensionFieldType.INTEGER ||
                field.getType() == ExtensionFieldType.DECIMAL)) {

            value = numberPadder.pad(value);  // NOSONAR - We want our modify our parameters
        }

        final SetJoin<UserEntity, ExtensionFieldValueEntity> join = createOrGetJoin(generateAlias(urn + "." + field.getName()),
                root, UserEntity_.extensionFieldValues);
        Predicate filterPredicate = constraint.createPredicateForExtensionField(
                join.get(ExtensionFieldValueEntity_.value), // NOSONAR - XEntity_.X will be filled by JPA provider
                value, field, cb);
        Predicate joinOnPredicate = cb.equal(join.get(ExtensionFieldValueEntity_.extensionField)
                .get(ExtensionFieldEntity_.internalId)
                , field.getInternalId()); // NOSONAR - XEntity_.X will be filled by JPA provider
        return cb.and(filterPredicate, joinOnPredicate);
    }

    private String generateAlias(String value) {
        int hashCode = value.hashCode();
        if (hashCode < 0) {
            hashCode *= -1;
        }

        return "extensionFieldAlias" + hashCode;
    }

    @SuppressWarnings("unchecked")
    protected <T> SetJoin<UserEntity, T> createOrGetJoin(String alias, Root<UserEntity> root,
                                                         SetAttribute<UserEntity, T> attribute) {

        for (Join<UserEntity, ?> currentJoin : root.getJoins()) {
            if (currentJoin.getAlias().equals(alias)) {
                return (SetJoin<UserEntity, T>) currentJoin;
            }
        }

        final SetJoin<UserEntity, T> join = root.join(attribute, JoinType.LEFT);
        join.alias(alias);

        return join;
    }
}
