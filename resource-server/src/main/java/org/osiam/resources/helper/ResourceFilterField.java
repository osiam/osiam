package org.osiam.resources.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.format.ISODateTimeFormat;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.entities.InternalIdSkeleton_;
import org.osiam.storage.entities.MetaEntity_;

enum ResourceFilterField implements FilterField<> {

    EXTERNALID("externalid") {
        @Override
        public Predicate addFilter(AbstractQuery<Long> query, Root<? extends InternalIdSkeleton> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(InternalIdSkeleton_.externalId), value, cb);
        }
    },
    META_CREATED("meta.created") {
        @Override
        public Predicate addFilter(AbstractQuery<Long> query, Root<InternalIdSkeleton> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(root.get(InternalIdSkeleton_.meta).get(MetaEntity_.created),
                    date, cb);
        }
    },
    META_LASTMODIFIED("meta.lastmodified") {
        @Override
        public Predicate addFilter(AbstractQuery<Long> query, Root<InternalIdSkeleton> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(
                    root.get(InternalIdSkeleton_.meta).get(MetaEntity_.lastModified),
                    date, cb);
        }
    },
    META_LOCATION("meta.location") {
        @Override
        public Predicate addFilter(AbstractQuery<Long> query, Root<InternalIdSkeleton> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(InternalIdSkeleton_.meta)
                    .get(MetaEntity_.location), value, cb);
        }
    },
    ;

    private static final Map<String, ResourceFilterField> stringToEnum = new HashMap<>();

    static {
        for (ResourceFilterField filterField : values()) {
            stringToEnum.put(filterField.toString(), filterField);
        }
    }

    private final String name;

    private ResourceFilterField(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ResourceFilterField fromString(String name) {
        return stringToEnum.get(name);
    }

}