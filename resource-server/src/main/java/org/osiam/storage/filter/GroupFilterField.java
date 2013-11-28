package org.osiam.storage.filter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SetAttribute;

import org.joda.time.format.ISODateTimeFormat;
import org.osiam.storage.entities.EntitlementsEntity;
import org.osiam.storage.entities.EntitlementsEntity_;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.GroupEntity_;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.entities.InternalIdSkeleton_;
import org.osiam.storage.entities.MetaEntity_;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.UserEntity_;

enum GroupFilterField implements FilterField<GroupEntity> {
    EXTERNALID("externalid") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(GroupEntity_.externalId), value, cb);
        }
    },
    META_CREATED("meta.created") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(root.get(GroupEntity_.meta).get(MetaEntity_.created),
                    date, cb);
        }
    },
    META_LASTMODIFIED("meta.lastmodified") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(
                    root.get(GroupEntity_.meta).get(MetaEntity_.lastModified),
                    date, cb);
        }
    },
    META_LOCATION("meta.location") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(GroupEntity_.meta)
                    .get(MetaEntity_.location), value, cb);
        }
    },
    DISPLAYNAME("displayname") {
        @Override
        public Predicate addFilter(Root<GroupEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(GroupEntity_.displayName), value, cb);
        }
    },
    MEMBERS("members") {

        @Override
        public Predicate addFilter(Root<GroupEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<GroupEntity, InternalIdSkeleton> join = createOrGetJoin("members", root,
                    GroupEntity_.members);
            return constraint.createPredicateForStringField(join.get(InternalIdSkeleton_.id), value, cb);
        }
    },
    MEMBERS_VALUE("members.value") {

        @Override
        public Predicate addFilter(Root<GroupEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<GroupEntity, InternalIdSkeleton> join = createOrGetJoin("members", root,
                    GroupEntity_.members);
            return constraint.createPredicateForStringField(join.get(InternalIdSkeleton_.id), value, cb);
        }
    };

    private static final Map<String, GroupFilterField> stringToEnum = new HashMap<>();

    static {
        for (GroupFilterField filterField : values()) {
            stringToEnum.put(filterField.toString(), filterField);
        }
    }

    private final String name;

    private GroupFilterField(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static GroupFilterField fromString(String name) {
        return stringToEnum.get(name);
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