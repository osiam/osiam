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

package org.osiam.storage.dao;

import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.entities.InternalIdSkeleton_;
import org.osiam.storage.filter.FilterChain;
import org.osiam.storage.filter.FilterParser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.logging.Logger;

public abstract class ResourceDao<T extends InternalIdSkeleton> {

    protected static final Logger LOGGER = Logger.getLogger(ResourceDao.class.getName()); // NOSONAR used in child classes

    @PersistenceContext
    protected EntityManager em; // NOSONAR used in child classes

    protected T getInternalIdSkeleton(String id) {
        Query query = em.createNamedQuery("getById");
        query.setParameter("id", id);
        return getSingleInternalIdSkeleton(query, id);
    }

    protected T getSingleInternalIdSkeleton(Query query, String identifier) {
        @SuppressWarnings("unchecked")
        List<T> result = query.getResultList();

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Resource " + identifier + " not found.");
        }

        return result.get(0);
    }

    protected SearchResult<T> search(Class<T> clazz, String filter, int count, int startIndex, String sortBy,
            String sortOrder) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<T> resourceQuery = cb.createQuery(clazz);
        Root<T> resourceRoot = resourceQuery.from(clazz);

        Subquery<Long> internalIdQuery = resourceQuery.subquery(Long.class);
        Root<T> internalIdRoot = internalIdQuery.from(clazz);
        internalIdQuery.select(internalIdRoot.get(InternalIdSkeleton_.internalId));

        if (filter != null && !filter.isEmpty()) {
            Predicate predicate = getFilterParser().createPredicateAndJoin(filter, internalIdRoot);
            internalIdQuery.where(predicate);
        }

        resourceQuery.select(resourceRoot).where(
                cb.in(resourceRoot.get(InternalIdSkeleton_.internalId)).value(internalIdQuery));

        Expression<?> sortByField = resourceRoot.get(getDefaultSortByField());

        if (sortBy != null && !sortBy.isEmpty()) {

            String[] sortSplit = sortBy.split("\\.");

            if (sortBy.length() == 1) {
                sortByField = resourceRoot.get(sortSplit[0]);
            } else if (sortSplit.length == 2) {
                sortByField = resourceRoot.get(sortSplit[0]).get(sortSplit[1]);
            }
        }

        // default order is ascending
        Order order = cb.asc(sortByField);

        if (sortOrder.equalsIgnoreCase("descending")) {
            order = cb.desc(sortByField);
        }

        resourceQuery.orderBy(order);

        TypedQuery<T> query = em.createQuery(resourceQuery);
        query.setFirstResult(startIndex);
        query.setMaxResults(count);

        List<T> results = query.getResultList();


        long totalResult = getTotalResults(clazz, internalIdQuery);

        return new SearchResult<>(results, totalResult);
    }

    private long getTotalResults(Class<T> clazz, Subquery<Long> internalIdQuery) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> resourceQuery = cb.createQuery(Long.class);
        Root<T> resourceRoot = resourceQuery.from(clazz);
        //  );

        resourceQuery.select(cb.count(resourceRoot)).where(cb.in(resourceRoot.get(InternalIdSkeleton_.internalId)).value(internalIdQuery));

        Long total = em.createQuery(resourceQuery).getSingleResult();

        return total;
    }

    protected abstract SingularAttribute<? super T, ?> getDefaultSortByField();

    protected abstract FilterParser<T> getFilterParser();

    protected abstract String getCoreSchema();

    protected abstract Class<T> getResourceClass();
}
