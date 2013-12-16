package org.osiam.storage.query;

import org.antlr.v4.runtime.misc.NotNull;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.parser.LogicalOperatorRulesBaseVisitor;
import org.osiam.storage.parser.LogicalOperatorRulesParser;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Implements the generated visitor class to do the mapping to criteria api.
 * User: Jochen Todea
 * Date: 09.12.13
 * Time: 12:36
 * Created: with Intellij IDEA
 */
public class EvalVisitor<T extends InternalIdSkeleton> extends LogicalOperatorRulesBaseVisitor {

    private final FilterParser filterParser;
    private final Root<T> root;

    public EvalVisitor(FilterParser filterParser, Root<T> root) {
        this.filterParser = filterParser;
        this.root = root;
    }

    @Override
    public Predicate visitAndExp(@NotNull LogicalOperatorRulesParser.AndExpContext ctx) {
        Predicate left = (Predicate) this.visit(ctx.expression(0));
        Predicate right = (Predicate) this.visit(ctx.expression(1));

        return filterParser.entityManager.getCriteriaBuilder().and(left, right);
    }

    @Override
    public Predicate visitBraceExp(@NotNull LogicalOperatorRulesParser.BraceExpContext ctx) {
        return (Predicate) this.visit(ctx.expression());
    }

    @Override
    public Predicate visitSimpleExp(@NotNull LogicalOperatorRulesParser.SimpleExpContext ctx) {
        String fieldName = ctx.FIELD().getText();
        String value = ctx.VALUE().getText();
        value = value.substring(1, value.length() - 1); // removed first and last quot
        value = value.replace("\\\"", "\""); // replaced \" with "
        FilterConstraint operator = FilterConstraint.fromString(ctx.OPERATOR().getText());
        ScimExpression scimExpression = new ScimExpression(fieldName, operator, value);

        FilterChain<T> filterChain = filterParser.createFilterChain(scimExpression);
        return filterChain.createPredicateAndJoin(root);
    }

    @Override
    public Predicate visitSimplePresentExp(@NotNull LogicalOperatorRulesParser.SimplePresentExpContext ctx) {
        String fieldName = ctx.FIELD().getText();
        FilterConstraint operator = FilterConstraint.fromString(ctx.PRESENT().getText());
        ScimExpression scimExpression = new ScimExpression(fieldName, operator, null);

        FilterChain<T> filterChain = filterParser.createFilterChain(scimExpression);
        return filterChain.createPredicateAndJoin(root);
    }

    @Override
    public Predicate visitNotExp(@NotNull LogicalOperatorRulesParser.NotExpContext ctx) {
        Predicate term = (Predicate) this.visit(ctx.expression());

        return filterParser.entityManager.getCriteriaBuilder().not(term);
    }

    @Override
    public Predicate visitOrExp(@NotNull LogicalOperatorRulesParser.OrExpContext ctx) {
        Predicate left = (Predicate) this.visit(ctx.expression(0));
        Predicate right = (Predicate) this.visit(ctx.expression(1));

        return filterParser.entityManager.getCriteriaBuilder().or(left, right);
    }
}