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


import org.osiam.storage.entities.UserEntity;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserCombinedFilterChain implements FilterChain<UserEntity> {
    // will be used to identify combined filter chain, the expression can be expressed in bracelets or without.
    //e.q.: title pr or userType eq Intern, title pr or (userType eq Intern and userName pr)
    // The first block is term1, the second is combinedWith and the third is term2.
    // Bracelets will be cut off for further processing.
    static final Pattern COMBINED_FILTER_CHAIN =
            Pattern.compile("(?i)[\\(]?([\\S ]+?)[\\)]? (and|or) [\\(]?([\\S ]+?)[\\)]?");
    private final FilterChain<UserEntity> leftTerm;
    private final Combiner combinedWith;
    private final FilterChain<UserEntity> rightTerm;

    private final CriteriaBuilder criteriaBuilder;

    public UserCombinedFilterChain(FilterParser<UserEntity> filterParser, String filter) {
        this.criteriaBuilder = filterParser.getEntityManager().getCriteriaBuilder();
        Matcher matcher = COMBINED_FILTER_CHAIN.matcher(filter);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(filter + " is not a complex filter string");
        }
        this.leftTerm = filterParser.parse(matcher.group(1)); // NOSONAR - no need to make constant for number
        this.combinedWith = Combiner.valueOf(matcher.group(2).toUpperCase(Locale.ENGLISH)); // NOSONAR - no need to make constant for number
        this.rightTerm = filterParser.parse(matcher.group(3)); // NOSONAR - no need to make constant for number
    }

    @Override
    public Predicate createPredicateAndJoin(AbstractQuery<Long> query, Root<UserEntity> root) {
        return combinedWith.addFilter(query, root, criteriaBuilder, leftTerm, rightTerm);

    }


    public enum Combiner {
        AND {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, CriteriaBuilder cb,
                                       FilterChain<UserEntity> leftTerm, FilterChain<UserEntity> rightTerm) {
                return cb.and(leftTerm.createPredicateAndJoin(query, root), rightTerm.createPredicateAndJoin(query, root));
            }
        },
        OR {
            @Override
            public Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, CriteriaBuilder cb,
                                       FilterChain<UserEntity> leftTerm, FilterChain<UserEntity> rightTerm) {
                return cb.or(leftTerm.createPredicateAndJoin(query, root), rightTerm.createPredicateAndJoin(query, root));
            }
        };

        public abstract Predicate addFilter(AbstractQuery<Long> query, Root<UserEntity> root, CriteriaBuilder cb,
                                            FilterChain<UserEntity> leftTerm, FilterChain<UserEntity> rightTerm);
    }
}