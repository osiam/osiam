package org.osiam.storage.filter;

import java.util.regex.Matcher;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.UserEntity;

public class UserSimpleFilterChain implements FilterChain<UserEntity> {

    private final String field;

    private final String value;

    private final FilterConstraint constraint;

    private final FilterField<UserEntity> userFilterField;

    private ExtensionFilterField extensionFilterField;

    private final ExtensionDao extensionDao;
    private final CriteriaBuilder criteriaBuilder;

    public UserSimpleFilterChain(CriteriaBuilder criteriaBuilder, ExtensionDao extensionDao, String filter) {
        Matcher matcher = FilterParser.SIMPLE_FILTER_PATTERN.matcher(filter);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(filter + " is not a simple filter string");
        }

        this.criteriaBuilder = criteriaBuilder;
        this.extensionDao = extensionDao;

        field = matcher.group(1).trim();

        userFilterField = UserFilterField.fromString(field.toLowerCase());

        // It's not a known user field, so try to build a extension filter
        if (userFilterField == null) {
            extensionFilterField = getExtensionFilterField(field.toLowerCase());
        }

        String constraintName = matcher.group(2); // NOSONAR - no need to make constant for number
        constraint = FilterConstraint.stringToEnum.get(constraintName);

        value = matcher.group(3).trim().replace("\"", ""); // NOSONAR - no need to make constant for number
    }

    private ExtensionFilterField getExtensionFilterField(String fieldString) {
        int lastIndexOf = fieldString.lastIndexOf('.');
        if (lastIndexOf == -1) {
            throw new IllegalArgumentException("Filtering not possible. Field '" + field + "' not available.");
        }

        String urn = fieldString.substring(0, lastIndexOf);
        String fieldName = fieldString.substring(lastIndexOf - 1);
        final ExtensionEntity extension;
        try {
            extension = extensionDao.getExtensionByUrn(urn);
        } catch (NoResultException ex) {
            throw new IllegalArgumentException("Filtering not possible. Field '" + field + "' not available.", ex);
        }
        final ExtensionFieldEntity fieldEntity = extension.getFieldForName(fieldName);
        return new ExtensionFilterField(urn, fieldEntity);
    }

    @Override
    public Predicate createPredicateAndJoin(Root<UserEntity> root) {
        if (userFilterField != null) {
            return userFilterField.addFilter(root, constraint, value, criteriaBuilder);
        } else if (extensionFilterField != null) {
            return extensionFilterField.addFilter(root, constraint, value, criteriaBuilder);
        } else {
            throw new IllegalArgumentException("Filtering not possible. Field '" + field + "' not available.");
        }
    }

}