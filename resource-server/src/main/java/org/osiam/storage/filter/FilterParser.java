package org.osiam.storage.filter;

import org.osiam.storage.entities.InternalIdSkeleton;

import javax.persistence.EntityManager;

public interface FilterParser<T extends InternalIdSkeleton> {

    FilterChain<T> parse(String filter);

    EntityManager getEntityManager();
}