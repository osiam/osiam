package org.osiam.storage.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SetAttribute;

import org.osiam.storage.entities.ExtensionEntity_;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.ExtensionFieldEntity_;
import org.osiam.storage.entities.ExtensionFieldValueEntity;
import org.osiam.storage.entities.ExtensionFieldValueEntity_;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.UserEntity_;

public class ExtensionFilterField {

    private final String urn;
    private final ExtensionFieldEntity field;

    public ExtensionFilterField(String urn, ExtensionFieldEntity field) {
        this.urn = urn;
        this.field = field;
    }

    public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
            String value, CriteriaBuilder cb) {

        final SetJoin<UserEntity, ExtensionFieldValueEntity> join = createOrGetJoin(aliasForUrn(urn),
                root, UserEntity_.extensionFieldValues);
        Predicate filterPredicate = constraint.createPredicateForExtensionField(
                join.get(ExtensionFieldValueEntity_.value),
                value, field, cb);
        Predicate joinOnPredicate = cb.equal(join.get(ExtensionFieldValueEntity_.extensionField)
                .get(ExtensionFieldEntity_.extension)
                .get(ExtensionEntity_.urn)
                , urn);
        return cb.and(filterPredicate, joinOnPredicate);
    }

    private String aliasForUrn(String urn) {
        return String.valueOf(urn.hashCode());
    }

    @SuppressWarnings("unchecked")
    protected <T> SetJoin<UserEntity, T> createOrGetJoin(String alias, Root<UserEntity> root,
            SetAttribute<UserEntity, T> attribute) {

        for (Join<UserEntity, ?> currentJoin : root.getJoins()) {
            if (currentJoin.getAlias().equals(alias)) {
                return (SetJoin<UserEntity, T>) currentJoin;
            }
        }
        final SetJoin<UserEntity, T> join = root.join(attribute, JoinType.LEFT);
        join.alias(alias);
        return join;
    }
}
