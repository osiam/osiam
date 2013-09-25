package org.osiam.resources.helper

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Pointcut
import spock.lang.Specification

import java.util.logging.Logger

class MeasureDurationTimeOfMethodsTest extends Specification {
    def underTest = new MeasureDurationTimeOfMethods()
    def logger = Mock(Logger)
    def joinPoint = Mock(ProceedingJoinPoint)
    def resultOfMethod = new Object()

    def setup() {
        MeasureDurationTimeOfMethods.LOGGER = logger
        joinPoint.proceed() >> resultOfMethod

    }

    def "should just return result when not enabled"() {
        given:
        underTest.enabled = false
        when:
        def result = underTest.measureTime(joinPoint)
        then:
        result == resultOfMethod
        0 * logger.info(_)
    }

    def "should measure time when enabled"() {
        given:
        underTest.enabled = true
        when:
        def result = underTest.measureTime(joinPoint)
        then:
        result == resultOfMethod
        1 * logger.info(_)
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
