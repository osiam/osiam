package org.osiam.storage.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.osiam.storage.entities.InternalIdSkeleton;

public interface FilterField<T extends InternalIdSkeleton> {
    Predicate addFilter(Root<T> root, FilterConstraint constraint, String value,
            CriteriaBuilder cb);
}
