package org.osiam.storage.filter;

import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.storage.entities.*;
import org.osiam.storage.helper.NumberPadder;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SetAttribute;

public class ExtensionFilterField {

    private final String urn;
    private final ExtensionFieldEntity field;
    private final NumberPadder numberPadder;

    public ExtensionFilterField(String urn, ExtensionFieldEntity field, NumberPadder numberPadder) {
        this.urn = urn;
        this.field = field;
        this.numberPadder = numberPadder;
    }

    public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
            String value, CriteriaBuilder cb) {

        if(field.getType() == ExtensionFieldType.INTEGER || field.getType() == ExtensionFieldType.DECIMAL) {
            value = numberPadder.pad(value);
        }

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
