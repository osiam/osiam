package org.osiam.storage.query;

/**
 * Representation of scim search expression.
 * User: Jochen Todea
 * Date: 11.12.13
 * Time: 14:21
 * Created: with Intellij IDEA
 */
public class ScimExpression {

    private final String field;
    private final FilterConstraint constraint;
    private final String value;


    public ScimExpression(String field, FilterConstraint constraint, String value) {
        this.field = field;
        this.constraint = constraint;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public FilterConstraint getConstraint() {
        return constraint;
    }

    public String getValue() {
        return value;
    }
}