package org.osiam.security.authorization;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;

public class OsiamDelegateExpressionParser implements ExpressionParser {

    private final ExpressionParser delegate;

    public OsiamDelegateExpressionParser(ExpressionParser delegate) {
        this.delegate = delegate;
    }

    @Override
    public Expression parseExpression(String expression) throws ParseException {
        return delegate.parseExpression(expression);
    }

    @Override
    public Expression parseExpression(String expression, ParserContext parserContext) throws ParseException {
        return delegate.parseExpression(expression, parserContext);
    }
}
