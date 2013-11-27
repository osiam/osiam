package org.osiam.storage.filter;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.osiam.resources.exceptions.InvalidConstraintException;
import org.osiam.storage.entities.EmailEntity.CanonicalEmailTypes;
import org.osiam.storage.entities.ExtensionFieldEntity;

public enum FilterConstraint {
    EQUALS("eq") {
        @Override
        public Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb) {
            return cb.equal(path, value);
        }

        @Override
        public Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb) {
            return cb.equal(path, value);
        }

        @Override
        public Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb) {
            return cb.equal(path, value);
        }

        @Override
        public Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path, CanonicalEmailTypes value,
                CriteriaBuilder cb) {
            return cb.equal(path, value);
        }

        @Override
        public Predicate createPredicateForExtensionField(Path<String> path, String value, ExtensionFieldEntity field,
                CriteriaBuilder cb) {
            if (!field.isConstrainedValid(toString())) {
                throw new InvalidConstraintException(toString());
            }
            return createPredicateForStringField(path, value, cb);
        }

    },
    CONTAINS("co") {
        @Override
        public Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb) {
            return cb.like(path, "%" + value + "%");
        }

        @Override
        public Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path, CanonicalEmailTypes value,
                CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForExtensionField(Path<String> path, String value, ExtensionFieldEntity field,
                CriteriaBuilder cb) {
            if (!field.isConstrainedValid(toString())) {
                throw new InvalidConstraintException(toString());
            }
            return createPredicateForStringField(path, value, cb);
        }

    },
    STARTS_WITH("sw") {
        @Override
        public Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb) {
            return cb.like(path, value + "%");
        }

        @Override
        public Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path, CanonicalEmailTypes value,
                CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForExtensionField(Path<String> path, String value, ExtensionFieldEntity field,
                CriteriaBuilder cb) {
            if (!field.isConstrainedValid(toString())) {
                throw new InvalidConstraintException(toString());
            }
            return createPredicateForStringField(path, value, cb);
        }
    },
    PRESENT("pr") {
        @Override
        public Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb) {
            return cb.and(cb.isNotNull(path), cb.notEqual(path, ""));
        }

        @Override
        public Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb) {
            return cb.isNotNull(path);
        }

        @Override
        public Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb) {
            return cb.and();
        }

        @Override
        public Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path, CanonicalEmailTypes value,
                CriteriaBuilder cb) {
            return cb.isNotNull(path);
        }

        @Override
        public Predicate createPredicateForExtensionField(Path<String> path, String value, ExtensionFieldEntity field,
                CriteriaBuilder cb) {
            if (!field.isConstrainedValid(toString())) {
                throw new InvalidConstraintException(toString());
            }
            return createPredicateForStringField(path, value, cb);
        }
    },
    GREATER_THAN("gt") {
        @Override
        public Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb) {
            return cb.greaterThan(path, value);
        }

        @Override
        public Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb) {
            return cb.greaterThan(path, value);
        }

        @Override
        public Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path, CanonicalEmailTypes value,
                CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForExtensionField(Path<String> path, String value, ExtensionFieldEntity field,
                CriteriaBuilder cb) {
            if (!field.isConstrainedValid(toString())) {
                throw new InvalidConstraintException(toString());
            }
            return createPredicateForStringField(path, value, cb);
        }
    },
    GREATER_EQUALS("ge") {
        @Override
        public Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb) {
            return cb.greaterThanOrEqualTo(path, value);
        }

        @Override
        public Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb) {
            return cb.greaterThanOrEqualTo(path, value);
        }

        @Override
        public Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path, CanonicalEmailTypes value,
                CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForExtensionField(Path<String> path, String value, ExtensionFieldEntity field,
                CriteriaBuilder cb) {
            if (!field.isConstrainedValid(toString())) {
                throw new InvalidConstraintException(toString());
            }
            return createPredicateForStringField(path, value, cb);
        }

    },
    LESS_THAN("lt") {
        @Override
        public Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb) {
            return cb.lessThan(path, value);
        }

        @Override
        public Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb) {
            return cb.lessThan(path, value);
        }

        @Override
        public Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path, CanonicalEmailTypes value,
                CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForExtensionField(Path<String> path, String value, ExtensionFieldEntity field,
                CriteriaBuilder cb) {
            if (!field.isConstrainedValid(toString())) {
                throw new InvalidConstraintException(toString());
            }
            return createPredicateForStringField(path, value, cb);
        }
    },
    LESS_EQUALS("le") {
        @Override
        public Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb) {
            return cb.lessThanOrEqualTo(path, value);
        }

        @Override
        public Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb) {
            return cb.lessThanOrEqualTo(path, value);
        }

        @Override
        public Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path, CanonicalEmailTypes value,
                CriteriaBuilder cb) {
            throw new InvalidConstraintException(toString());
        }

        @Override
        public Predicate createPredicateForExtensionField(Path<String> path, String value, ExtensionFieldEntity field,
                CriteriaBuilder cb) {
            if (!field.isConstrainedValid(toString())) {
                throw new InvalidConstraintException(toString());
            }
            return createPredicateForStringField(path, value, cb);
        }
    };

    static Map<String, FilterConstraint> stringToEnum = new ConcurrentHashMap<>();

    static {
        for (final FilterConstraint constraint : values()) {
            stringToEnum.put(constraint.toString(), constraint);
        }
    }

    public static FilterConstraint fromString(String name) {
        return stringToEnum.get(name.toLowerCase());
    }

    @Override
    public String toString() {
        return name;
    }

    private final String name; // NOSONAR - is not singular because it is used in the static block

    FilterConstraint(String constraint) {
        this.name = constraint;
    }

    public abstract Predicate createPredicateForStringField(Path<String> path, String value, CriteriaBuilder cb);

    public abstract Predicate createPredicateForDateField(Path<Date> path, Date value, CriteriaBuilder cb);

    public abstract Predicate createPredicateForBooleanField(Path<Boolean> path, Boolean value, CriteriaBuilder cb);

    public abstract Predicate createPredicateForEmailTypeField(Path<CanonicalEmailTypes> path,
            CanonicalEmailTypes value, CriteriaBuilder cb);

    public abstract Predicate createPredicateForExtensionField(Path<String> path, String value,
            ExtensionFieldEntity field, CriteriaBuilder cb);
}