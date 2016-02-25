/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/** Define a grammer called LogicalOperatorRules which will be the basis for ARNTL to generate the java classes for lexer and parser */
grammar LogicalOperatorRules;

/** Parser: will organize the tokens provided by the lexer into the tree with sub-trees and also defining the precedence of the operators */
parse
    : expression
    ;

expression
    : expression OR expression    #orExp
    | expression AND expression   #andExp
    | NOT '(' expression ')'      #notExp
    | '(' expression ')'          #braceExp
    | FIELD PRESENT               #simplePresentExp
    | FIELD OPERATOR VALUE        #simpleExp
    ;

/** Lexer: Interpreting character and turn them into tokens which will be organized by the parser in an tree */
OR
    : 'or'
    | 'Or'
    | 'oR'
    | 'OR'
    ;

AND
    : 'and'
    | 'And'
    | 'aNd'
    | 'anD'
    | 'ANd'
    | 'aND'
    | 'AND'
    ;

NOT
    : 'not'
    | 'Not'
    | 'nOt'
    | 'noT'
    | 'NOt'
    | 'nOT'
    | 'NOT'
    ;

PRESENT
    : 'pr'
    ;

OPERATOR
    : 'sw'
    | 'co'
    | 'eq'
    | 'gt'
    | 'ge'
    | 'lt'
    | 'le'
    ;

FIELD
    : ([a-z] | [A-Z] | [0-9] | '.' | ':' | '_' | '-')+ //match lower-case, upper-case, number identifier including dots, colons and quotation marks
    ;

ESCAPED_QUOTE
    : '\\"'
    ;

VALUE
    : '"'(ESCAPED_QUOTE | ~'"')*'"'
    ;

EXCLUDE
    : [\b | \t | \r | \n]+ -> skip //skipping backspaces, tabs, carriage returns and newlines
    ;



// handle characters which failed to match any other token
ErrorCharacter : . ;
