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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.parser.LogicalOperatorRulesLexer;
import org.osiam.storage.parser.LogicalOperatorRulesParser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FilterParser<T extends InternalIdSkeleton> {

    @PersistenceContext
    protected EntityManager entityManager; // NOSONAR - doesn't need to be private

    public Predicate createPredicateAndJoin(String filterString, Root<T> root) {
        LogicalOperatorRulesLexer lexer = new LogicalOperatorRulesLexer(new ANTLRInputStream(filterString));
        LogicalOperatorRulesParser parser = new LogicalOperatorRulesParser(new CommonTokenStream(lexer));
        parser.addErrorListener(new OsiamAntlrErrorListener());
        ParseTree tree = parser.parse();
        EvalVisitor<T> visitor = new EvalVisitor<>(this, root);

        return (Predicate) visitor.visit(tree);
    }

    public Expression<?> createSortByField(String sortBy, Root<T> root) {
        QueryField<T> filterField = getFilterField(sortBy);

        if (filterField == null) {
            throw new IllegalArgumentException("Sorting by " + sortBy + " is not suported.");
        }

        return filterField.createSortByField(root, entityManager.getCriteriaBuilder());
    }

    protected abstract QueryField<T> getFilterField(String sortBy);

    protected abstract FilterChain<T> createFilterChain(ScimExpression filter);

}