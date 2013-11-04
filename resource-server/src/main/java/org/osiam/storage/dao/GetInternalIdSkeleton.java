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

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Subqueries;
import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.resources.helper.FilterParser;
import org.osiam.resources.scim.Constants;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.storage.entities.InternalIdSkeleton;

public abstract class GetInternalIdSkeleton {

    protected static final Logger LOGGER = Logger.getLogger(GetInternalIdSkeleton.class.getName()); // NOSONAR used in child classes

    @PersistenceContext
    protected EntityManager em; // NOSONAR used in child classes

    @Inject
    private FilterParser filterParser;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void setFilterParser(FilterParser filterParser) {
        this.filterParser = filterParser;
    }

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

    protected <T> SCIMSearchResult<T> search(Class<T> clazz, String filter, int count, int startIndex, String sortBy,
                                             String sortOrder) {
        // create subquery criteria for all possible (internal) ids that match the filter (used by result and total count queries)
        DetachedCriteria idsOnlyCriteria = DetachedCriteria.forClass(clazz);
        createAliasesForCriteria(idsOnlyCriteria);
        if (filter != null && !filter.isEmpty()) {
            idsOnlyCriteria.add(filterParser.parse(filter, clazz).buildCriterion());
        }
        idsOnlyCriteria.setProjection(Projections.distinct(Projections.id()));

        List results = getResults(idsOnlyCriteria, clazz, count, startIndex, sortBy, sortOrder);
        long totalResult = getTotalResults(idsOnlyCriteria, clazz);

        int newStartIndex = startIndex <1 ? 1 :startIndex;
        
        return new SCIMSearchResult(results, totalResult, count, newStartIndex, Constants.CORE_SCHEMA);
    }

    
    private <T> List getResults(DetachedCriteria idsOnlyCriteria, Class<T> clazz, int count, int startIndex, String sortBy,
            String sortOrder) {
	    Criteria criteria = ((Session) em.getDelegate()).createCriteria(clazz);

        criteria.setReadOnly(true);
        criteria.setCacheMode(CacheMode.GET);
        criteria.setCacheable(true);
        
        criteria.add(Subqueries.propertyIn("internalId", idsOnlyCriteria));
        setSortOrder(sortBy, sortOrder, criteria);
        criteria.setMaxResults(count);
        criteria.setFirstResult(startIndex-1);//-1 due to scim spec and default of 1 instead of 0

        // FIXME: The next line should not be necessary, but it is ...
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }
    
    private void setSortOrder(String sortBy, String sortOrder, Criteria criteria) {
        if (sortOrder.equalsIgnoreCase("descending")) {
            criteria.addOrder(Order.desc(sortBy));
        } else {
            criteria.addOrder(Order.asc(sortBy));
        }
    }

    private <T> long getTotalResults(DetachedCriteria idsOnlyCriteria, Class<T> clazz) {
        Criteria countCriteria = ((Session) em.getDelegate()).createCriteria(clazz);

        countCriteria.setReadOnly(true);
        countCriteria.setCacheMode(CacheMode.GET);
        countCriteria.setCacheable(true);
        
        countCriteria.add(Subqueries.propertyIn("internalId", idsOnlyCriteria));
        countCriteria.setProjection(Projections.rowCount());
        Object totalResultAsObject = countCriteria.uniqueResult();
        
        return totalResultAsObject != null ? (long) totalResultAsObject : 0;
    }

    protected abstract void createAliasesForCriteria(DetachedCriteria criteria);
}
