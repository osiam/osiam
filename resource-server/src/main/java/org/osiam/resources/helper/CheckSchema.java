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

package org.osiam.resources.helper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.osiam.resources.exceptions.SchemaUnknownException;
import org.osiam.resources.scim.Constants;
import org.osiam.resources.scim.Resource;

@Aspect
public class CheckSchema {
    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controllerBean() {
    }

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {
    }

    @Before("controllerBean() && methodPointcut() ")
    public void checkUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            validateArguments(args);
        }
    }

    private void validateArguments(Object[] args) {
        for (Object o : args) {
            if (o instanceof Resource) {
                validateUser((Resource) o);
            }
        }
    }

    private void validateUser(Resource user) {
        if (user.getSchemas() == null || user.getSchemas().isEmpty()) {
            throw new SchemaUnknownException();
        }
        for (String s : user.getSchemas()) {
            if (!Constants.CORE_SCHEMAS.contains(s)) {
                throw new SchemaUnknownException();
            }
        }
    }
}
