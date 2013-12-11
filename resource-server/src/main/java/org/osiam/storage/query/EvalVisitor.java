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

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.antlr.v4.runtime.misc.NotNull;
import org.osiam.storage.entities.ResourceEntity;
import org.osiam.storage.parser.LogicalOperatorRulesBaseVisitor;
import org.osiam.storage.parser.LogicalOperatorRulesParser;

/**
 * Implements the generated visitor class to do the mapping to criteria api.
 * @author Jochen Todea
 */
public class EvalVisitor<T extends ResourceEntity> extends LogicalOperatorRulesBaseVisitor<Predicate> {

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