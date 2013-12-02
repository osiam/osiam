/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.storage.query;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.osiam.storage.entities.InternalIdSkeleton;

public abstract class FilterParser<T extends InternalIdSkeleton> {

    static final Pattern COMBINED_FILTER_PATTERN =
            Pattern.compile("(?i)[\\(]?([\\S ]+?)[\\)]? (and|or) [\\(]?([\\S ]+?)[\\)]?");

    static final Pattern SIMPLE_FILTER_PATTERN = Pattern.compile("(\\S+) (" + createOrConstraints()
            + ")[ ]??([\\S ]*?)");

    protected static String createOrConstraints() {
        StringBuilder sb = new StringBuilder();
        for (FilterConstraint constraint : FilterConstraint.values()) {
            if (sb.length() != 0) {
                sb.append("|");
            }
            sb.append(constraint.toString());
        }
        return sb.toString();
    }

    @PersistenceContext
    protected EntityManager entityManager; // NOSONAR - doesn't need to be private

    public Predicate createPredicateAndJoin(String filterString, Root<T> root) {
        List<String> filterFragments = new LinkedList<>();
        List<Predicate> predicates = new LinkedList<>();

        push(filterString, filterFragments);

        while (!filterFragments.isEmpty()) {
            String filterFragment = pop(filterFragments);

            Matcher matcherCombined = COMBINED_FILTER_PATTERN.matcher(filterFragment);
            Matcher matcherSimple = SIMPLE_FILTER_PATTERN.matcher(filterFragment);

            FilterCombiner combiner = null;
            try {
                combiner = FilterCombiner.valueOf(filterFragment);
            } catch (IllegalArgumentException e) {
                // safe to ignore - if the string is no combiner then we are not interested in it
            }

            if (matcherCombined.matches()) {
                String leftTerm = matcherCombined.group(1); // NOSONAR - no need to make constant for number
                String combinedWith = matcherCombined.group(2).toUpperCase(Locale.ENGLISH); // NOSONAR - no need to make
                // constant for number
                String rightTerm = matcherCombined.group(3); // NOSONAR - no need to make constant for number

                push(combinedWith, filterFragments);
                push(rightTerm, filterFragments);
                push(leftTerm, filterFragments);
            } else if (matcherSimple.matches()) {
                FilterChain<T> simpleFilterChain = createFilterChain(filterFragment);
                Predicate predicate = simpleFilterChain.createPredicateAndJoin(root);

                push(predicate, predicates);
            } else if (combiner != null) {
                Predicate leftTerm = pop(predicates);
                Predicate rightTerm = pop(predicates);

                Predicate predicate = combiner.addFilter(entityManager.getCriteriaBuilder(), leftTerm, rightTerm);

                push(predicate, predicates);
            } else {
                throw new IllegalArgumentException(filterFragment + " is not a filter string");
            }
        }

        return predicates.get(0);
    }

    public Expression<?> createSortByField(String sortBy, Root<T> root) {
        QueryField<T> filterField = getFilterField(sortBy);

        if (filterField == null) {
            throw new IllegalArgumentException("Sorting by " + sortBy + " is not suported.");
        }

        return filterField.createSortByField(root, entityManager.getCriteriaBuilder());
    }

    protected abstract QueryField<T> getFilterField(String sortBy);

    protected abstract FilterChain<T> createFilterChain(String filter);

    protected <E> void push(E o, List<E> stack) {
        stack.add(o);
    }

    protected <E> E pop(List<E> stack) {
        return stack.remove(stack.size() - 1);
    }
}