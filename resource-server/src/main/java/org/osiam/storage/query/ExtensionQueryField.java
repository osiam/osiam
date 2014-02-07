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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SetAttribute;

import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.ExtensionFieldEntity_;
import org.osiam.storage.entities.ExtensionFieldValueEntity;
import org.osiam.storage.entities.ExtensionFieldValueEntity_;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.UserEntity_;
import org.osiam.storage.helper.NumberPadder;

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

            value = numberPadder.pad(value); // NOSONAR - We want our modify our parameters
        }

        final SetJoin<UserEntity, ExtensionFieldValueEntity> join = createOrGetJoin(
                generateAlias(urn + "." + field.getName()), root, UserEntity_.extensionFieldValues);
        
        Predicate filterPredicate = constraint.createPredicateForExtensionField(
                join.get(ExtensionFieldValueEntity_.value), // NOSONAR - XEntity_.X will be filled by JPA provider
                value, field, cb);
        
        Predicate valueBelongsToField = cb.equal(join.get(ExtensionFieldValueEntity_.extensionField)
                .get(ExtensionFieldEntity_.internalId), field.getInternalId());
        
        join.on(valueBelongsToField);

        return filterPredicate;
    }

    private String generateAlias(String value) {
        int hashCode = value.hashCode();
        if (hashCode < 0) {
            hashCode *= -1;
        }

        return "extensionFieldAlias" + hashCode;
    }

    @SuppressWarnings("unchecked")
    protected SetJoin<UserEntity, ExtensionFieldValueEntity> createOrGetJoin(String alias, Root<UserEntity> root,
            SetAttribute<UserEntity, ExtensionFieldValueEntity> attribute) {

        for (Join<UserEntity, ?> currentJoin : root.getJoins()) {
            if (currentJoin.getAlias() == null) {
                // if alias is null, it is not an alias for an extension join, so we ignore it
                continue;
            }

            if (currentJoin.getAlias().equals(alias)) {
                return (SetJoin<UserEntity, ExtensionFieldValueEntity>) currentJoin;
            }
        }

        final SetJoin<UserEntity, ExtensionFieldValueEntity> join = root.join(attribute, JoinType.LEFT);

        join.alias(alias);

        return join;
    }
}
