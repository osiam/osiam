package org.osiam.storage.filter;

import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.UserEntity;

import javax.persistence.NoResultException;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserSimpleFilterChain implements FilterChain<UserEntity> {

    private static final Pattern SIMPLE_CHAIN_PATTERN = Pattern.compile("(\\S+) (" + createOrConstraints()
            + ")[ ]??([\\S ]*?)");


    private static String createOrConstraints() {
        StringBuilder sb = new StringBuilder();
        for (FilterConstraint constraint : FilterConstraint.values()) {
            if (sb.length() != 0) {
                sb.append("|");
            }
            sb.append(constraint.toString());
        }
        return sb.toString();

    }

    private final String field;

    private final String value;

    private final FilterConstraint constraint;

    private final FilterField<UserEntity> userFilterField;

    private ExtensionFilterField extensionFilterField;

    private final ExtensionDao extensionDao;
    private final CriteriaBuilder criteriaBuilder;

    public UserSimpleFilterChain(UserFilterParser filterParser, String filter) {
        Matcher matcher = SIMPLE_CHAIN_PATTERN.matcher(filter);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(filter + " is not a simple filter string");
        }

        this.criteriaBuilder = filterParser.getEntityManager().getCriteriaBuilder();
        this.extensionDao = filterParser.extensionDao;
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
    public Predicate createPredicateAndJoin(AbstractQuery<Long> query, Root<UserEntity> root) {
        if (userFilterField != null) {
            return userFilterField.addFilter(query, root, constraint, value, criteriaBuilder);
        } else if (extensionFilterField != null) {
            return null;
        } else {
            throw new IllegalArgumentException("Filtering not possible. Field '" + field + "' not available.");
        }
    }

}