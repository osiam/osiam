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

package org.osiam.resources.helper

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Pointcut
import spock.lang.Specification

class MeasureDurationTimeOfMethodsSpec extends Specification {

    def underTest = new MeasureDurationTimeOfMethods()
    ProceedingJoinPoint joinPoint = Mock()
    def resultOfMethod = new Object()

    def setup() {
        joinPoint.proceed() >> resultOfMethod
    }

    def "should just return result when not enabled"() {
        given:
        underTest.enabled = false

        when:
        def result = underTest.measureTime(joinPoint)

        then:
        result == resultOfMethod
    }

    def "should measure time when enabled"() {
        given:
        underTest.enabled = true
        when:
        def result = underTest.measureTime(joinPoint)
        then:
        result == resultOfMethod
    }

    def "should include org.osiam"() {
        given:
        def method = underTest.class.getMethod("includeOrgOsiam")

        when:
        def annotation = method.getAnnotation(Pointcut)
        underTest.includeOrgOsiam()
        then:
        annotation.value() == "within(org.osiam..*)"
    }

    def "should exclude org.osiam.security.authorization.DynamicHTTPMethodScopeEnhancer due to an error"() {
        given:
        def method = underTest.class.getMethod("excludeDynamicHTTPMethodScopeEnhancer")

        when:
        def annotation = method.getAnnotation(Pointcut)
        underTest.excludeDynamicHTTPMethodScopeEnhancer()
        then:
        annotation.value() == "!execution(* org.osiam.security.authorization.DynamicHTTPMethodScopeEnhancer.*(..))"
    }
}
