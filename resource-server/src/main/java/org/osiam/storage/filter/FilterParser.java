package org.osiam.storage.filter;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    protected EntityManager entityManager;

    public Predicate createPredicateAndJoin(String filterString, Root<T> root) {
        ArrayList<String> filterFragments = new ArrayList<>();
        ArrayList<Predicate> predicates = new ArrayList<>();

        push(filterString, filterFragments);

        while(!filterFragments.isEmpty()) {
            String filterFragment = pop(filterFragments);

            Matcher matcherCombined = COMBINED_FILTER_PATTERN.matcher(filterFragment);
            Matcher matcherSimple = SIMPLE_FILTER_PATTERN.matcher(filterFragment);

            Combiner combiner = null;
            try {
                combiner = Combiner.valueOf(filterFragment);
            } catch (IllegalArgumentException e) {
                // safe to ignore - if the string is no combiner then we are not interested in it
            }

            if(matcherCombined.matches()) {
                String leftTerm = matcherCombined.group(1); // NOSONAR - no need to make constant for number
                String combinedWith = matcherCombined.group(2).toUpperCase(Locale.ENGLISH); // NOSONAR - no need to make constant for number
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

    protected abstract FilterChain<T> createFilterChain(String filter);

    protected <E> void push(E o, ArrayList<E> stack) {
        stack.add(o);
    }

    protected <E> E pop(ArrayList<E> stack) {
        return stack.remove(stack.size() - 1);
    }
}