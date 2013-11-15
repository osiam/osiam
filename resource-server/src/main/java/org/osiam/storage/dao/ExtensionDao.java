package org.osiam.storage.dao;

import org.osiam.storage.entities.ExtensionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class ExtensionDao {
    @PersistenceContext
    private EntityManager em;

    public ExtensionEntity getExtensionByUrn(String urn) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExtensionEntity> cq = cb.createQuery(ExtensionEntity.class);
        Root<ExtensionEntity> extension = cq.from(ExtensionEntity.class);

        cq.select(extension).where(cb.equal(extension.get("urn"), urn));

        TypedQuery<ExtensionEntity> query = em.createQuery(cq);

        return query.getSingleResult();
    }
}
