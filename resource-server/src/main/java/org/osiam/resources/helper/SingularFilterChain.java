/*
 * Copyright 2013
 *     tarent AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osiam.resources.helper;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.PhotoEntity;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingularFilterChain implements FilterChain {

    static final Pattern SINGULAR_CHAIN_PATTERN =
            Pattern.compile("(\\S+) (" + Constraints.createOrConstraints() + ")[ ]??([\\S ]*?)");

    private static final String KEYNAME_TYPE = "type";
    private static final String KEYNAME_PRIMARY = "primary";
    private static final String KEYNAME_ID = "id";
    private static final String KEYNAME_CREATED = "created";
    private static final String KEYNAME_LASTMODIFIED = "lastModified";

    private final String key;
    private final Constraints constraint;
    private final Object value;

    /* List of key splitted in all subkeys. */
    private final List<String> splitKeys;

    /* Class name of value */
    private final String className;

    public SingularFilterChain(String chain, Class clazz) {
        Matcher matcher = SINGULAR_CHAIN_PATTERN.matcher(chain);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(chain + " is not a SingularFilterChain.");
        }
        this.key = matcher.group(1).trim();
        this.constraint = Constraints.fromString.get(matcher.group(2)); // NOSONAR - no need to make constant for number

        // Split keys for handling complex types and get Class name of value type.
        this.splitKeys = splitKey();
        List<Field> fields = getAllFieldsIncludingSuperclass(clazz, new ArrayList<Field>());
        Field field = getSingleField(splitKeys, fields);
        this.className = getClassName(field);

        this.value = castToOriginValue(matcher.group(3).trim()); // NOSONAR - no need to make constant for number
    }

    private Object castToOriginValue(String group) {
        if (isNumber(group)) {
            return Long.valueOf(group);
        }
        if (className.equals("Boolean")) {
            return getBoolean(group);
        }
        return getMultivalue(group);
    }

    private Object getMultivalue(String group) {

        if (isSubkey()) {
            String keyname =  splitKeys.get(1);
            switch (className + keyname) {
                case "EmailEntity" + KEYNAME_TYPE:
                    return EmailEntity.CanonicalEmailTypes.valueOf(group);
                case "EmailEntity" + KEYNAME_PRIMARY:
                    return getBoolean(group);
                case "PhotoEntity" + KEYNAME_TYPE:
                    return PhotoEntity.CanonicalPhotoTypes.valueOf(group);
                case "ImEntity" + KEYNAME_TYPE:
                    return ImEntity.CanonicalImTypes.valueOf(group);
                case "PhoneNumberEntity" + KEYNAME_TYPE:
                    return PhoneNumberEntity.CanonicalPhoneNumberTypes.valueOf(group);
                case "AddressEntity" + KEYNAME_PRIMARY:
                    return getBoolean(group);
            }
        }

        return getStringOrDate(group);
    }

    private Boolean getBoolean(String group) {
        if (group.equalsIgnoreCase("true") || group.equalsIgnoreCase("false")) {
            return Boolean.valueOf(group);
        }
        throw new IllegalArgumentException("Value of Field " + key + " mismatch!");
    }

    /**
     * Method to get the simple class name even if the field is a Set.
     * In case it is a Set, the generic simple class name is returned.
     *
     * @param field the {@link Field} whose type name should be returned
     * @return the class name as a {@link String}
     */
    private String getClassName(Field field) {
        if (field.getType().getSimpleName().equals("Set")) {
            ParameterizedType fieldGenericType = (ParameterizedType) field.getGenericType();
            Class<?> fieldGenericClass = (Class<?>) fieldGenericType.getActualTypeArguments()[0];
            return fieldGenericClass.getSimpleName();
        } else {
            return field.getType().getSimpleName();
        }
    }

    private Field getSingleField(List<String> split, List<Field> fields) {
        String filterField = split.get(0);
        Field field = null;
        for (Field f : fields) {
            if (f.getName().equals(filterField)) {
                field = f;
                break;
            }
        }
        if (field == null) {
            throw new IllegalArgumentException("Filtering not possible. Field '" + filterField + "' not available.");
        }
        return field;
    }

    private List<String> splitKey() {
        List<String> split;
        if (key.contains(".")) {
            split = Arrays.asList(key.split("\\."));
        } else {
            split = new ArrayList<>();
            split.add(key);
        }
        return split;
    }

    private List<Field> getAllFieldsIncludingSuperclass(Class clazz, List<Field> fields) {
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSimpleName().equals("InternalIdSkeleton")) {
            return fields;
        }
        return getAllFieldsIncludingSuperclass(clazz.getSuperclass(), fields);
    }

    private Object getStringOrDate(String group) {
        String result = group.replace("\"", "");
        try {
            return tryToGetDate(result);
        } catch (IllegalArgumentException e) {
            return result;
        }
    }

    private Object tryToGetDate(String result) {
        DateTime time = ISODateTimeFormat.dateTimeParser().parseDateTime(result);
        return time.toDate();
    }

    private boolean isNumber(String group) {
        return group.matches("[0-9]+");
    }

    /**
     * Check if constraint is only applicable on {@link String} types.
     *
     * @return true if only applicable on {@link String}s, false if applicable on every complex type
     */
    private boolean isOnlyStringConstraint() {
        switch (constraint) {
            case CONTAINS:
                return true;
            case STARTS_WITH:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if subvalue is no {@link String}.
     *
     * @return true if subvalue is no {@link String}
     */
    private boolean isSubvalueNotString() {
        // Expand this list of incompatible Non{@String} values if necessary.
        List<String> nonStringKeys = new ArrayList<>();
        nonStringKeys.add(KEYNAME_PRIMARY);
        nonStringKeys.add(KEYNAME_TYPE);
        nonStringKeys.add(KEYNAME_ID);
        nonStringKeys.add(KEYNAME_CREATED);
        nonStringKeys.add(KEYNAME_LASTMODIFIED);

        // Get value of subkey and return false if it is NO {@String}.
        String subvalue = splitKeys.get(1);
        return nonStringKeys.contains(subvalue);
    }

    /**
     * Check if value is no {@link String}.
     *
     * @return true if value is no {@link String}
     */
    private boolean isValueNotString() {
        return !className.equals("String");
    }

    /**
     * Check if key is concurrently a subkey.
     *
     * @return true if key is a subkey, false if not
     */
    private boolean isSubkey() {
        // Check if there is a subkey.
        return splitKeys.size() >= 2;
    }

    /**
     * Check if operator is applicable on field and throw an {@link IllegalArgumentException} if not.
     */
    private void applicabilityCheck() {
        if (isOnlyStringConstraint()) {
            if (!isSubkey()) {
                // First level value and String
                if (isValueNotString()) {
                    throw new IllegalArgumentException("String filter operators 'co' and 'sw' are not applicable on field '" + splitKeys.get(0) + "' of type '" + className + "'.");
                }
            } else {
                // Second level value and String
                if (isSubvalueNotString()) {
                    throw new IllegalArgumentException("String filter operators 'co' and 'sw' are not applicable on field '" + splitKeys.get(1) + "'.");
                }
            }
        }
    }

    @Override
    public Criterion buildCriterion() {

        applicabilityCheck();

        switch (constraint) {
            case CONTAINS:
                return Restrictions.like(key, "%" + value + "%");
            case STARTS_WITH:
                return Restrictions.like(key, value + "%");
            case EQUALS:
                return Restrictions.eq(key, value);
            case GREATER_EQUALS:
                return Restrictions.ge(key, value);
            case GREATER_THAN:
                return Restrictions.gt(key, value);
            case LESS_EQUALS:
                return Restrictions.le(key, value);
            case LESS_THAN:
                return Restrictions.lt(key, value);
            case PRESENT:
                return Restrictions.isNotNull(key);
            default:
                throw new IllegalArgumentException("Unknown constraint.");
        }
    }

    public enum Constraints {
        EQUALS("eq"),
        CONTAINS("co"),
        STARTS_WITH("sw"),
        PRESENT("pr"),
        GREATER_THAN("gt"),
        GREATER_EQUALS("ge"),
        LESS_THAN("lt"),
        LESS_EQUALS("le");
        private static Map<String, Constraints> fromString = new ConcurrentHashMap<>();

        static {
            for (final Constraints k : values()) {
                fromString.put(k.constraint, k);
            }
        }

        private final String constraint; // NOSONAR - is not singular because it is used in the static block


        Constraints(String constraint) {
            this.constraint = constraint;
        }

        static String createOrConstraints() {
            StringBuilder sb = new StringBuilder();
            for (Constraints k : values()) {
                if (sb.length() != 0) {
                    sb.append("|");
                }
                sb.append(k.constraint);
            }
            return sb.toString();

        }


    }
}