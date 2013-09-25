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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;

import java.util.logging.Logger;

@Aspect
/**
 * This interceptor has the purpose to print out the duration time of all called methods in the package org.osiam.
 *
 */
public class MeasureDurationTimeOfMethods {
    private static Logger LOGGER = Logger.getLogger(MeasureDurationTimeOfMethods.class.getName()); //NOSONAR excluded because of testing
    @Value("${osiam.profiling}")
    private boolean enabled;

    @Around("excludeDynamicHTTPMethodScopeEnhancer() && includeOrgOsiam()")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable { //NOSONAR the join point throws it and can't be omitted
        long start = 0, end;
        if (enabled) { start = System.currentTimeMillis(); }
        Object result = wrapExceptionForSonar(joinPoint);
        if (enabled) {
            end = System.currentTimeMillis();
            String msg = joinPoint.toShortString() + " took " + (end - start) + "ms";
            LOGGER.info(msg);
        }
        return result;

    }

    private Object wrapExceptionForSonar(ProceedingJoinPoint joinPoint) throws Throwable { //NOSONAR the join point throws it and can't be omitted
        Object result;
        result = joinPoint.proceed();

        return result;
    }

    @Pointcut("within(org.osiam..*)")
    public void includeOrgOsiam() {
    }

    /**
     * must exclude due to:
     * <p/>
     * nested exception is org.springframework.aop.framework.AopConfigException: Could not generate CGLIB subclass of
     * class [class org.osiam.security.authorization.DynamicHTTPMethodScopeEnhancer]
     * <p/>
     * error
     */
    @Pointcut("!execution(* org.osiam.security.authorization.DynamicHTTPMethodScopeEnhancer.*(..))")
    public void excludeDynamicHTTPMethodScopeEnhancer() {
    }


}
