package org.osiam.resources.exceptions;

public class InvalidConstraintException extends OsiamException {

    private static final long serialVersionUID = -3458413407035304091L;

    private final String constraint;

    public InvalidConstraintException(String constraint) {
        this.constraint = constraint;
    }

    @Override
    public String getMessage() {
        return String.format("Constraint %s is invalid", constraint);
    }

}
