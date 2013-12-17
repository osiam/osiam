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
public class EvalVisitor<T extends InternalIdSkeleton> extends LogicalOperatorRulesBaseVisitor<Predicate> {

    private final FilterParser<T> filterParser;
    private final Root<T> root;

    public EvalVisitor(FilterParser<T> filterParser, Root<T> root) {
        this.filterParser = filterParser;
        this.root = root;
    }

    @Override
    public Predicate visitAndExp(@NotNull LogicalOperatorRulesParser.AndExpContext ctx) {
        Predicate left = this.visit(ctx.expression(0));
        Predicate right = this.visit(ctx.expression(1));

        return filterParser.entityManager.getCriteriaBuilder().and(left, right);
    }

    @Override
    public Predicate visitBraceExp(@NotNull LogicalOperatorRulesParser.BraceExpContext ctx) {
        return this.visit(ctx.expression());
    }

    @Override
    public Predicate visitSimpleExp(@NotNull LogicalOperatorRulesParser.SimpleExpContext ctx) {
        ScimExpression scimExpression = getScimExpressionFromContext(ctx);
        FilterChain<T> filterChain = filterParser.createFilterChain(scimExpression);
        return filterChain.createPredicateAndJoin(root);
    }

    private ScimExpression getScimExpressionFromContext(LogicalOperatorRulesParser.SimpleExpContext ctx) {
        String fieldName = ctx.FIELD().getText();
        String value = ctx.VALUE().getText();
        value = value.substring(1, value.length() - 1); // removed first and last quote
        value = value.replace("\\\"", "\""); // replaced \" with "
        FilterConstraint operator = FilterConstraint.fromString(ctx.OPERATOR().getText());
        return new ScimExpression(fieldName, operator, value);
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
        Predicate term = this.visit(ctx.expression());

        return filterParser.entityManager.getCriteriaBuilder().not(term);
    }

    @Override
    public Predicate visitOrExp(@NotNull LogicalOperatorRulesParser.OrExpContext ctx) {
        Predicate left = this.visit(ctx.expression(0));
        Predicate right = this.visit(ctx.expression(1));

        return filterParser.entityManager.getCriteriaBuilder().or(left, right);
    }
}