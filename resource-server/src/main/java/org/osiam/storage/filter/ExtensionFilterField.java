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
        return constraint.createPredicateForStringField(join.get(ExtensionFieldValueEntity_.value), value, cb);
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
