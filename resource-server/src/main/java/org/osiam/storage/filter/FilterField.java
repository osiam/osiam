package org.osiam.storage.filter;

import org.osiam.storage.entities.InternalIdSkeleton;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface FilterField<T extends InternalIdSkeleton> {
    Predicate addFilter(AbstractQuery<Long> query, Root<T> root, FilterConstraint constraint, String value,
                        CriteriaBuilder cb);
}
