package org.osiam.storage.filter;

import org.osiam.storage.entities.*;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SetAttribute;

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
                .get(ExtensionFieldEntity_.internalId)
                , field.getInternalId());
        return cb.and(filterPredicate, joinOnPredicate);
    }

    private String aliasForUrn(String urn) {
        int hashCode = urn.hashCode();
        if (hashCode < 0) {
            hashCode *= -1;
        }

        return "extensionAlias" + String.valueOf(hashCode);
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
