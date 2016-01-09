package org.osiam.storage.query;

import org.osiam.resources.scim.Group;
import org.osiam.storage.entities.GroupEntity;

import java.util.Locale;

public class GroupFilterExpression extends FilterExpression<GroupEntity> {

    private final Field field;

    public GroupFilterExpression(String field, FilterConstraint constraint, String value) {
        super(constraint, value);
        this.field = new Field(field);
    }

    @Override
    public Field getField() {
        return field;
    }

    public static class Field implements FilterExpression.Field<GroupEntity> {
        private String name;
        private QueryField<GroupEntity> queryField;

        public Field(String field) {

            if (field.toLowerCase().startsWith(Group.SCHEMA.toLowerCase())) {
                if (field.charAt(Group.SCHEMA.length()) == '.') {
                    throw new IllegalArgumentException(String.format("Period (.) is not a valid field separator: %s", field));
                }

                name = field.substring(Group.SCHEMA.length() + 1).toLowerCase(Locale.ENGLISH);

            } else {
                name = field.toLowerCase(Locale.ENGLISH);
            }
            queryField = GroupQueryField.fromString(name);

            if (queryField == null) {
                throw new IllegalArgumentException(String.format("Unable to parse %s, extensions not supported for groups",
                        field));
            }
        }

        @Override
        public String getUrn() {
            // We are not supporting Group extensions at the moment.
            return Group.SCHEMA;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public QueryField<GroupEntity> getQueryField() {
            return queryField;
        }

        @Override
        public boolean isExtension() {
            return false;
        }
    }
}
