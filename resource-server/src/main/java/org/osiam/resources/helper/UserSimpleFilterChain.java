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

import org.osiam.storage.entities.*;

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
    private final String value; // TODO: strip quotes around the field

    private final List<String> splitKeys;

    private final FilterField<UserEntity> filterFieldUser;
    private final FilterField<InternalIdSkeleton> filterFieldResource;
    private final EntityManager em;

    public UserSimpleFilterChain(EntityManager em, String filter) {
        Matcher matcher = SIMPLE_CHAIN_PATTERN.matcher(filter);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(filter + " is not a simple filter string");
        }

        this.em = em;
        this.field = matcher.group(1).trim();
        this.constraint = FilterConstraint.stringToEnum.get(matcher.group(2)); // NOSONAR
                                                                               // -
                                                                               // no
                                                                               // need
                                                                               // to
                                                                               // make
                                                                               // constant
                                                                               // for
                                                                               // number
        this.filterFieldUser = UserFilterField.fromString(field.toLowerCase());
        this.filterFieldResource = ResourceFilterField.fromString(field.toLowerCase());

        // TODO: is this needed anymore? maybe for extensions!
        this.splitKeys = splitKey(field); // Split keys for handling complex
                                          // types

        this.value = matcher.group(3).trim().replace("\"", ""); // NOSONAR - no
                                                                // need to make
                                                                // constant for
                                                                // number
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
        if(filterFieldUser != null) {
            return filterFieldUser.addFilter(query, root, constraint, value, em.getCriteriaBuilder());
        } else if (filterFieldResource != null) {
            return filterFieldResource.addFilter(query, (Root<InternalIdSkeleton>) root, constraint, value, em.getCriteriaBuilder());
        } else {
            throw new IllegalArgumentException("Filtering not possible. Field '" + field + "' not available.");
        }
    }

}