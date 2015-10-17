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

import org.osiam.resources.exception.NoSuchElementException;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionEntity_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Locale;

@Repository
public class ExtensionDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionDao.class);

    @PersistenceContext
    private EntityManager em;

    /**
     * Retrieves the extension with the given URN from the database. The URN is case-sensitive.
     *
     * @param urn The URN of the extension to look up
     * @return the extension entity
     */
    public ExtensionEntity getExtensionByUrn(String urn) {
        return getExtensionByUrn(urn, false);
    }

    /**
     * Retrieves the extension with the given URN from the database
     *
     * @param urn             the URN of the extension to look up
     * @param caseInsensitive should the case of the URN be ignored
     * @return the extension entity
     */
    public ExtensionEntity getExtensionByUrn(String urn, boolean caseInsensitive) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExtensionEntity> cq = cb.createQuery(ExtensionEntity.class);
        Root<ExtensionEntity> extension = cq.from(ExtensionEntity.class);

        Predicate predicate;
        if (caseInsensitive) {
            predicate = cb.equal(cb.lower(extension.get(ExtensionEntity_.urn)), urn.toLowerCase(Locale.ENGLISH));
        } else {
            predicate = cb.equal(extension.get(ExtensionEntity_.urn), urn);
        }

        cq.select(extension).where(predicate);

        TypedQuery<ExtensionEntity> query = em.createQuery(cq);

        ExtensionEntity singleExtension;

        try {
            singleExtension = query.getSingleResult();
        } catch (NoResultException e) {
            String message = String.format("Extension with urn '%s' not found", urn);
            LOGGER.warn(message);
            throw new NoSuchElementException(message);
        }

        return singleExtension;
    }

    /**
     * Get all stored extensions.
     *
     * @return all extensions.
     */
    public List<ExtensionEntity> getAllExtensions() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExtensionEntity> cq = cb.createQuery(ExtensionEntity.class);
        Root<ExtensionEntity> extension = cq.from(ExtensionEntity.class);

        cq.select(extension);
        TypedQuery<ExtensionEntity> query = em.createQuery(cq);

        return query.getResultList();
    }

}
