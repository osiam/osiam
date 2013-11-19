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

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Subqueries;
import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.resources.helper.FilterParser;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.entities.InternalIdSkeleton_;

public abstract class InternalIdSkeletonDao {

    protected static final Logger LOGGER = Logger.getLogger(InternalIdSkeletonDao.class.getName()); // NOSONAR used in child classes

    @PersistenceContext
    protected EntityManager em; // NOSONAR used in child classes

    @Inject
    protected FilterParser filterParser;

    protected <T extends InternalIdSkeleton> T getInternalIdSkeleton(String id) {
        Query query = em.createNamedQuery("getById");
        query.setParameter("id", id);
        return getSingleInternalIdSkeleton(query, id);
    }

    @SuppressWarnings("unchecked")
    protected <T extends InternalIdSkeleton> T getSingleInternalIdSkeleton(Query query, String identifier) {
        List result = query.getResultList();
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Resource " + identifier + " not found.");
        }
        return (T) result.get(0);
    }

    protected <T extends InternalIdSkeleton> SCIMSearchResult<T> search(Class<T> clazz, String filter, int count,
            int startIndex, String sortBy, String sortOrder) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<T> resourceQuery = cb.createQuery(clazz);
        Root<T> resourceRoot = resourceQuery.from(clazz);

        Subquery<Long> internalIdQuery = resourceQuery.subquery(Long.class);
        Root<T> internalIdRoot = internalIdQuery.from(clazz);
        internalIdQuery.select(internalIdRoot.get(InternalIdSkeleton_.internalId));

        // TODO: add predicates
        /*if (filter != null && !filter.isEmpty()) {
            idsOnlyCriteria.add(filterParser.parse(filter, clazz).buildCriterion());
        }*/

        List<T> results = getResults(idsOnlyCriteria, clazz, count, startIndex, sortBy, sortOrder);
        long totalResult = getTotalResults(idsOnlyCriteria, clazz);

        Expression<?> sortByField = resourceRoot.get(sortBy);
        Order order;
        if (sortOrder.equalsIgnoreCase("descending")) {
            order = cb.desc(sortByField);
        } else {
            order = cb.asc(sortByField);
        }
        resourceQuery.orderBy(order);

        return new SCIMSearchResult<T>(results, totalResult, count, newStartIndex, getCoreSchema());
    }
        List<T> results = q.getResultList();

    private <T> List<T> getResults(DetachedCriteria idsOnlyCriteria, Class<T> clazz, int count, int startIndex, String sortBy,
            String sortOrder) {
	    Criteria criteria = ((Session) em.getDelegate()).createCriteria(clazz);

        long totalResult = getTotalResults(clazz, filter);

        ///////////////////////////////////////////////////////////////////////////////////

        int newStartIndex = startIndex < 1 ? 1 : startIndex;

        return new SCIMSearchResult<T>(results, totalResult, count, newStartIndex, Constants.CORE_SCHEMA);
    }

    private <T extends InternalIdSkeleton> long getTotalResults(Class<T> clazz, String filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> resourceQuery = cb.createQuery(Long.class);
        Root<T> resourceRoot = resourceQuery.from(clazz);

        // TODO: add predicates
        /*if (filter != null && !filter.isEmpty()) {
            idsOnlyCriteria.add(filterParser.parse(filter, clazz).buildCriterion());
        }*/

        resourceQuery.select(cb.count(resourceRoot));

        Long total = em.createQuery(resourceQuery).getSingleResult();

        return total;
    }

    protected abstract void createAliasesForCriteria(DetachedCriteria criteria);
    protected abstract String getCoreSchema();
}
