/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.storage.query;

import org.osiam.resources.exception.InvalidFilterException;
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
                    throw new InvalidFilterException(String.format("Period (.) is not a valid field separator: %s", field));
                }

                name = field.substring(Group.SCHEMA.length() + 1).toLowerCase(Locale.ENGLISH);

            } else {
                name = field.toLowerCase(Locale.ENGLISH);
            }
            queryField = GroupQueryField.fromString(name);

            if (queryField == null) {
                throw new InvalidFilterException(String.format("Unable to parse %s, extensions not supported for groups",
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
