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
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.SingularAttribute;

import org.antlr.v4.runtime.tree.ParseTree;
import org.osiam.resources.exceptions.OsiamException;
import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.ResourceEntity;
import org.osiam.storage.entities.ResourceEntity_;
import org.osiam.storage.query.FilterParser;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceDao {

    @PersistenceContext
    private EntityManager em;

    public <T extends ResourceEntity> SearchResult<T> search(Class<T> clazz, ParseTree filterTree, int count,
            int startIndex,
            String sortBy, String sortOrder, FilterParser<T> filterParser) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<T> resourceQuery = cb.createQuery(clazz);
        Root<T> resourceRoot = resourceQuery.from(clazz);

        Subquery<Long> internalIdQuery = resourceQuery.subquery(Long.class);
        Root<T> internalIdRoot = internalIdQuery.from(clazz);
        internalIdQuery.select(internalIdRoot.get(ResourceEntity_.internalId));

        if (filterTree != null && filterTree.getChildCount() > 0) {
            Predicate predicate = filterParser.createPredicateAndJoin(filterTree, internalIdRoot);
            internalIdQuery.where(predicate);
        }

        resourceQuery.select(resourceRoot).where(
                cb.in(resourceRoot.get(ResourceEntity_.internalId)).value(internalIdQuery));

        // TODO: evaluate if a User-/GroupDao supplied default sortBy field is possible
        Expression<?> sortByField = resourceRoot.get(ResourceEntity_.id);

        if (sortBy != null && !sortBy.isEmpty()) {
            sortByField = filterParser.createSortByField(sortBy, resourceRoot);
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

    private <T extends ResourceEntity> long getTotalResults(Class<T> clazz, Subquery<Long> internalIdQuery) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> resourceQuery = cb.createQuery(Long.class);
        Root<T> resourceRoot = resourceQuery.from(clazz);

        resourceQuery.select(cb.count(resourceRoot)).where(
                cb.in(resourceRoot.get(ResourceEntity_.internalId)).value(internalIdQuery));

        Long total = em.createQuery(resourceQuery).getSingleResult();

        return total;
    }

    /**
     * Retrieves a single {@link ResourceEntity} by the given id.
     * 
     * @param id
     *            the id of the resource to retrieve it by
     * @param clazz
     *            the concrete resource entity class to retrieve (may also be {@link ResourceEntity})
     * @return The matching {@link ResourceEntity}
     * @throws ResourceNotFoundException
     *             if no {@link ResourceEntity} with the given id could be found
     */
    public <T extends ResourceEntity> T getById(String id, Class<T> clazz) {
        return getByAttribute(ResourceEntity_.id, id, clazz);
    }

    /**
     * Retrieves a single {@link ResourceEntity} by the given attribute and value.
     * 
     * @param attribute
     *            The attribute of the resource entity to retrieve it by
     * @param value
     *            The value of the attribute to compare it to
     * @param clazz
     *            The concrete resource entity class to retrieve (may also be {@link ResourceEntity})
     * @return The matching {@link ResourceEntity}
     * @throws ResourceNotFoundException
     *             If no {@link ResourceEntity} could be found
     * @throws OsiamException
     *             If more than 1 {@link ResourceEntity} was found
     */
    public <T extends ResourceEntity, V> T getByAttribute(SingularAttribute<? super T, V> attribute, V value,
            Class<T> clazz) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> resource = cq.from(clazz);

        cq.select(resource).where(cb.equal(resource.get(attribute), value));

        TypedQuery<T> q = em.createQuery(cq);

        try {
            return q.getSingleResult();
        } catch (NoResultException nre) {
            throw new ResourceNotFoundException(String.format("Resource with attribute '%s' set to '%s' not found",
                    attribute.getName(), value), nre);
        } catch (NonUniqueResultException nure) {
            throw new OsiamException(String.format("Muliple resources with attribute '%s' set to '%s' found",
                    attribute.getName(), value), nure);
        }
    }

    public <T extends ResourceEntity, V> boolean isUniqueAttributeAlreadyTaken(String attributeValue, String id,
            SingularAttribute<? super T, V> attribute, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> resource = cq.from(clazz);

        cq.select(cb.countDistinct(resource));

        Predicate predicate = cb.equal(resource.get(attribute), attributeValue);
        if (id != null) {
            Predicate ignoreId = cb.notEqual(resource.get(ResourceEntity_.id), id);
            predicate = cb.and(predicate, ignoreId);
        }
        cq.where(predicate);

        TypedQuery<Long> countQuery = em.createQuery(cq);

        return countQuery.getSingleResult() > 0;
    }

    public boolean isExternalIdAlreadyTaken(String externalId, String id) {
        return isUniqueAttributeAlreadyTaken(externalId, id, ResourceEntity_.externalId, ResourceEntity.class);
    }

    public boolean isExternalIdAlreadyTaken(String externalId) {
        return isExternalIdAlreadyTaken(externalId, null);
    }

    /**
     * Removes a {@link ResourceEntity} from the database by its id
     * 
     * @param id
     *            id of the {@link ResourceEntity}
     * @throws ResourceNotFoundException
     *             if no {@link ResourceEntity} could be found with the given id
     * @throws IllegalArgumentException
     *             if the instance is not a managed {@link ResourceEntity}
     */
    public void delete(String id) {
        ResourceEntity resourceEntity = getById(id, ResourceEntity.class);

        Set<GroupEntity> groups = resourceEntity.getGroups();
        for (GroupEntity group : groups) {
            group.removeMember(resourceEntity);
        }

        em.remove(resourceEntity);
    }

    public <T extends ResourceEntity> T update(T resourceEntity) {
        return em.merge(resourceEntity);
    }

    public <T extends ResourceEntity> void create(T resourceEntity) {
        em.persist(resourceEntity);
    }

}
