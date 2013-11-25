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


import org.osiam.storage.entities.GroupEntity;

import javax.persistence.EntityManager;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupCombinedFilterChain implements FilterChain<GroupEntity> {
    static final Pattern COMBINED_FILTER_CHAIN =
            Pattern.compile("(?i)[\\(]?([\\S ]+?)[\\)]? (and|or) [\\(]?([\\S ]+?)[\\)]?");
    private final FilterChain<GroupEntity> leftTerm;
    private final Combiner combinedWith;
    private final FilterChain<GroupEntity> rightTerm;

    private final EntityManager em;

    public GroupCombinedFilterChain(GroupFilterParser filterParser, String filter) {
        this.em = filterParser.entityManager;
        Matcher matcher = COMBINED_FILTER_CHAIN.matcher(filter);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(filter + " is not a complex filter string");
        }
        this.leftTerm = filterParser.parse(matcher.group(1)); // NOSONAR - no need to make constant for number
        this.combinedWith = Combiner.valueOf(matcher.group(2).toUpperCase(Locale.ENGLISH)); // NOSONAR - no need to make constant for number
        this.rightTerm = filterParser.parse(matcher.group(3)); // NOSONAR - no need to make constant for number
    }

    @Override
    public Predicate createPredicateAndJoin(AbstractQuery<Long> query, Root<GroupEntity> root) {
        return combinedWith.addFilter(query, root, em.getCriteriaBuilder(), leftTerm, rightTerm);

    }


    public enum Combiner {
        AND {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<GroupEntity> root, CriteriaBuilder cb,
                                       FilterChain<GroupEntity> leftTerm, FilterChain<GroupEntity> rightTerm) {
                return cb.and(leftTerm.createPredicateAndJoin(query, root), rightTerm.createPredicateAndJoin(query, root));
            }
        },
        OR {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<GroupEntity> root, CriteriaBuilder cb,
                                       FilterChain<GroupEntity> leftTerm, FilterChain<GroupEntity> rightTerm) {
                return cb.or(leftTerm.createPredicateAndJoin(query, root), rightTerm.createPredicateAndJoin(query, root));
            }
        };

        public abstract Predicate addFilter(AbstractQuery<Long> query, Root<GroupEntity> root, CriteriaBuilder cb,
                                            FilterChain<GroupEntity> leftTerm, FilterChain<GroupEntity> rightTerm);
    }
}