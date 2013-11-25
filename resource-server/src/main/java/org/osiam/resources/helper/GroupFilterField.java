package org.osiam.resources.helper;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.GroupEntity_;

enum GroupFilterField {

    DISPLAYNAME("displayname") {
        @Override
        public Predicate addFilter(AbstractQuery<Long> query, Root<GroupEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(GroupEntity_.displayName), value, cb);
        }
    },
    ;

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

    public abstract Predicate addFilter(AbstractQuery<Long> query, Root<GroupEntity> root,
            FilterConstraint constraint, String value, CriteriaBuilder cb);

}