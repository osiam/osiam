package org.osiam.storage.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public enum Combiner {
    AND {
        @Override
        public Predicate addFilter(CriteriaBuilder cb, Predicate leftTerm, Predicate rightTerm) {
            return cb.and(leftTerm, rightTerm);
        }
    },
    OR {
        @Override
        public Predicate addFilter(CriteriaBuilder cb, Predicate leftTerm, Predicate rightTerm) {
            return cb.or(leftTerm, rightTerm);
        }
    };

    public abstract Predicate addFilter(CriteriaBuilder cb, Predicate leftTerm, Predicate rightTerm);
}