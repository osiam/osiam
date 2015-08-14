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

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
@Aspect
/**
 * This interceptor has the purpose to print out the duration time of all called methods in the package org.osiam.
 *
 */
public class MeasureDurationTimeOfMethods {

    private static Logger LOGGER = LoggerFactory.getLogger(MeasureDurationTimeOfMethods.class);

    @Value("${org.osiam.resource-server.profiling:false}")
    private boolean enabled;

    @Around("includeOrgOsiam()")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = 0, end;
        if (enabled) {
            start = System.currentTimeMillis();
        }
        Object result = wrapExceptionForSonar(joinPoint);
        if (enabled) {
            end = System.currentTimeMillis();
            String msg = joinPoint.toShortString() + " took " + (end - start) + "ms";
            LOGGER.info(msg);
        }
        return result;

    }

    private Object wrapExceptionForSonar(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        result = joinPoint.proceed();

        return result;
    }

    @Pointcut("within(org.osiam..*)")
    public void includeOrgOsiam() {
    }
}
