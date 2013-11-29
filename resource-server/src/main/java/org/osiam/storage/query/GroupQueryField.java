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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SetAttribute;

import org.joda.time.format.ISODateTimeFormat;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.GroupEntity_;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.entities.InternalIdSkeleton_;
import org.osiam.storage.entities.MetaEntity_;

public enum GroupQueryField implements QueryField<GroupEntity> {
    EXTERNALID("externalid") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(GroupEntity_.externalId), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<GroupEntity> root, CriteriaBuilder cb) {
            return root.get(GroupEntity_.externalId);
        }
    },
    META_CREATED("meta.created") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(root.get(GroupEntity_.meta).get(MetaEntity_.created), date, cb);  // NOSONAR - XEntity_.X will be filled by JPA provider
        }

        @Override
        public Expression<?> createSortByField(Root<GroupEntity> root, CriteriaBuilder cb) {
            return root.get(GroupEntity_.meta).get(MetaEntity_.created);
        }
    },
    META_LASTMODIFIED("meta.lastmodified") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(
                    root.get(GroupEntity_.meta).get(MetaEntity_.lastModified), date, cb); // NOSONAR - XEntity_.X will be filled by JPA provider
        }

        @Override
        public Expression<?> createSortByField(Root<GroupEntity> root, CriteriaBuilder cb) {
            return root.get(GroupEntity_.meta).get(MetaEntity_.lastModified);
        }
    },
    META_LOCATION("meta.location") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(GroupEntity_.meta)
                    .get(MetaEntity_.location), value, cb);  // NOSONAR - XEntity_.X will be filled by JPA provider
        }

        @Override
        public Expression<?> createSortByField(Root<GroupEntity> root, CriteriaBuilder cb) {
            return root.get(GroupEntity_.meta).get(MetaEntity_.location);
        }
    },
    DISPLAYNAME("displayname") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(GroupEntity_.displayName), value, cb);  // NOSONAR - XEntity_.X will be filled by JPA provider
        }

        @Override
        public Expression<?> createSortByField(Root<GroupEntity> root, CriteriaBuilder cb) {
            return root.get(GroupEntity_.displayName);
        }
    },
    MEMBERS("members") {

        @Override
        public Predicate addFilter(Root<GroupEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<GroupEntity, InternalIdSkeleton> join = createOrGetJoin("members", root, GroupEntity_.members); // NOSONAR - XEntity_.X will be filled by JPA provider
            return constraint.createPredicateForStringField(join.get(InternalIdSkeleton_.id), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<GroupEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    MEMBERS_VALUE("members.value") {

        @Override
        public Predicate addFilter(Root<GroupEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<GroupEntity, InternalIdSkeleton> join = createOrGetJoin("members", root,
                    GroupEntity_.members);
            return constraint.createPredicateForStringField(join.get(InternalIdSkeleton_.id), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<GroupEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    };

    private static final Map<String, GroupQueryField> STRING_TO_ENUM = new HashMap<>();

    static {
        for (GroupQueryField filterField : values()) {
            STRING_TO_ENUM.put(filterField.toString(), filterField);
        }
    }

    private final String name;

    private GroupQueryField(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static GroupQueryField fromString(String name) {
        return STRING_TO_ENUM.get(name);
    }

    protected RuntimeException handleSortByFieldNotSupported(String fieldName) {
        throw new RuntimeException("Sorting by " + fieldName + " is not supported yet");// NOSONAR - will be removed after implementing
    }

    @SuppressWarnings("unchecked")
    protected <T> SetJoin<GroupEntity, T> createOrGetJoin(String alias, Root<GroupEntity> root,
            SetAttribute<GroupEntity, T> attribute) {

        for (Join<GroupEntity, ?> currentJoin : root.getJoins()) {
            if (currentJoin.getAlias().equals(alias)) {
                return (SetJoin<GroupEntity, T>) currentJoin;
            }
        }

        final SetJoin<GroupEntity, T> join = root.join(attribute, JoinType.LEFT);
        join.alias(alias);

        return join;
    }

}