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

package org.osiam.storage.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.osiam.storage.entities.GroupEntity;

public class GroupSimpleFilterChain implements FilterChain<GroupEntity> {

    private final String field;
    private final FilterConstraint constraint;
    private final String value; // TODO: strip quotes around the field

    private final List<String> splitKeys;

    private final GroupFilterField filterField;
    private final EntityManager em;

    public GroupSimpleFilterChain(EntityManager em, String filter) {
        Matcher matcher = FilterParser.SIMPLE_FILTER_PATTERN.matcher(filter);
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
        this.filterField = GroupFilterField.fromString(field.toLowerCase());

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
    public Predicate createPredicateAndJoin(Root<GroupEntity> root) {
        return filterField.addFilter(root, constraint, value, em.getCriteriaBuilder());
    }

}