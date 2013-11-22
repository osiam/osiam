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

import org.joda.time.format.ISODateTimeFormat;
import org.osiam.storage.entities.*;
import org.osiam.storage.entities.EmailEntity.CanonicalEmailTypes;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;
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
    private final FilterConstraint constraint;
    private final String value;      // TODO: strip quotes around the field

    private final List<String> splitKeys;

    private final FilterField filterField;
    private final EntityManager em;

    public UserSimpleFilterChain(EntityManager em, String filter) {
        Matcher matcher = SIMPLE_CHAIN_PATTERN.matcher(filter);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(filter + " is not a simple filter string");
        }

        this.em = em;
        this.field = matcher.group(1).trim();
        this.constraint = FilterConstraint.stringToEnum.get(matcher.group(2)); // NOSONAR - no need to make constant for number
        this.filterField = FilterField.fromString(field.toLowerCase());

        // TODO: is this needed anymore? maybe for extensions!
        this.splitKeys = splitKey(field); // Split keys for handling complex types

        this.value = matcher.group(3).trim(); // NOSONAR - no need to make constant for number
    }

    private List<String> splitKey(String key) {
        List<String> split;
        if (key.contains(".")) {
            split = Arrays.asList(key.split("\\."));
        } else {
            split = new ArrayList<>();
            split.add(key);
        }
        return split;
    }

    @Override
    public Predicate createPredicateAndJoin(AbstractQuery<Long> query, Root<UserEntity> root) {
        return filterField.addFilter(query, root, constraint, value, em.getCriteriaBuilder());
    }

    private static enum FilterField {

        EXTERNALID("externalid") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.externalId), value, cb);
            }
        },
        META_CREATED("meta.created") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                Date date = ISODateTimeFormat.dateParser().parseDateTime(value).toDate();
                return constraint.createPredicateForDateField(root.get(UserEntity_.meta).get(MetaEntity_.created), date, cb);
            }
        },
        USERNAME("username") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.userName), value, cb);
            }

        },
        DISPLAYNAME("displayname") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.displayName), value, cb);
            }

        },
        NICKNAME("nickname") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.nickName), value, cb);
            }

        },
        PROFILEURL("profileurl") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.profileUrl), value, cb);
            }

        },
        TITLE("title") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.title), value, cb);
            }

        },
        PREFERREDLANGUAGE("preferredlanguage") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.preferredLanguage), value, cb);
            }

        },
        LOCALE("locale") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.locale), value, cb);
            }

        },
        TIMEZONE("timezone") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.timezone), value, cb);
            }

        },
        ACTIVE("active") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForBooleanField(root.get(UserEntity_.active), Boolean.valueOf(value),
                        cb);
            }

        },
        NAME_FORMATTED("name.formatted") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.formatted),
                        value, cb);
            }

        },
        NAME_FAMILYNAME("name.familyname") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.familyName),
                        value, cb);
            }

        },
        NAME_GIVENNAME("name.givenname") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.givenName),
                        value, cb);
            }

        },
        NAME_MIDDLENAME("name.middlename") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.middleName),
                        value, cb);
            }

        },
        NAME_HONORIFICPREFIX("name.honorificprefix") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(
                        root.get(UserEntity_.name).get(NameEntity_.honorificPrefix), value, cb);
            }

        },
        NAME_HONORIFICSUFFIX("name.honorificsuffix") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                return constraint.createPredicateForStringField(
                        root.get(UserEntity_.name).get(NameEntity_.honorificSuffix), value, cb);
            }

        },
        EMAILS("emails") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {

                SetJoin<UserEntity, EmailEntity> join = root.join(UserEntity_.emails, JoinType.LEFT);
                return constraint.createPredicateForStringField(join.get(EmailEntity_.value), value, cb);
            }

        },
        EMAILS_VALUE("emails.value") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {

                SetJoin<UserEntity, EmailEntity> join = root.join(UserEntity_.emails, JoinType.LEFT);
                return constraint.createPredicateForStringField(join.get(EmailEntity_.value), value, cb);
            }

        },

        EMAILS_TYPE("emails.type") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {

                SetJoin<UserEntity, EmailEntity> join = root.join(UserEntity_.emails, JoinType.LEFT);
                return constraint.createPredicateForEmailTypeField(join.get(EmailEntity_.type), CanonicalEmailTypes.valueOf(value), cb);
            }

        },
        EMAILS_PRIMARY("emails.primary") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {

                SetJoin<UserEntity, EmailEntity> join = root.join(UserEntity_.emails, JoinType.LEFT);
                return constraint.createPredicateForBooleanField(join.get(EmailEntity_.primary), Boolean.valueOf(value), cb);
            }

        },

        PHONENUMBERS("phoneNumbers") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                SetJoin<UserEntity, PhoneNumberEntity> join = root.join(UserEntity_.phoneNumbers, JoinType.LEFT);
                return constraint.createPredicateForStringField(join.get(PhoneNumberEntity_.value), value, cb);
            }
        },

        PHONENUMBERS_VALUE("phonenumbers.value") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                SetJoin<UserEntity, PhoneNumberEntity> join = root.join(UserEntity_.phoneNumbers, JoinType.LEFT);
                return constraint.createPredicateForStringField(join.get(PhoneNumberEntity_.value), value, cb);
            }
        },

        IMS("ims") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                SetJoin<UserEntity, ImEntity> join = root.join(UserEntity_.ims, JoinType.LEFT);
                return constraint.createPredicateForStringField(join.get(ImEntity_.value), value, cb);

            }
        },

        IMS_VALUE("ims.value") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                SetJoin<UserEntity, ImEntity> join = root.join(UserEntity_.ims, JoinType.LEFT);
                return constraint.createPredicateForStringField(join.get(ImEntity_.value), value, cb);
            }

        },
        PHOTOS("photos") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                SetJoin<UserEntity, PhotoEntity> join = root.join(UserEntity_.photos, JoinType.LEFT);
                return constraint.createPredicateForStringField(join.get(PhotoEntity_.value), value, cb);

            }
        },
        PHOTOS_VALUE("photos.value") {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, FilterConstraint constraint,
                                       String value, CriteriaBuilder cb) {
                SetJoin<UserEntity, PhotoEntity> join = root.join(UserEntity_.photos, JoinType.LEFT);
                return constraint.createPredicateForStringField(join.get(PhotoEntity_.value), value, cb);

            }
        };


        private static final Map<String, FilterField> stringToEnum = new HashMap<>();

        static {
            for (FilterField filterField : values()) {
                stringToEnum.put(filterField.toString(), filterField);
            }
        }

        private final String name;

        private FilterField(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static FilterField fromString(String name) {
            return stringToEnum.get(name);
        }

        public abstract Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root,
                                            FilterConstraint constraint, String value, CriteriaBuilder cb);
    }

}