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

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.osiam.resources.exceptions.SchemaUnknownException
import org.osiam.resources.helper.CheckSchema
import org.osiam.resources.scim.Constants
import org.osiam.resources.scim.User
import spock.lang.Specification

import java.lang.reflect.Method

class CheckSchemaTest extends Specification {
    def underTest = new CheckSchema()
    def joint = Mock(JoinPoint)


    def "should do nothing when no args are delivered"() {
        when:
        underTest.checkUser(joint)
        then:
        notThrown(SchemaUnknownException)

    }

    def "should do nothing when args doesn't contain org.osiam.resources.scim.User"() {
        given:
        joint.args >> ["haha"]
        when:
        underTest.checkUser(joint)
        then:
        notThrown(SchemaUnknownException)

    }


    def "should throw an org.osiam.resources.exceptions.SchemaUnknownException when User schema is empty"() {
        given:
        def schema = [] as Set
        User user = new User.Builder("test").setSchemas(schema).build()
        joint.args >> [user]
        when:
        underTest.checkUser(joint)
        then:
        thrown(SchemaUnknownException)
    }

    def "should throw an org.osiam.resources.exceptions.SchemaUnknownException when User schema is null"() {
        given:
        User user = new User.Builder("test").setSchemas(null).build()
        joint.args >> [user]
        when:
        underTest.checkUser(joint)
        then:
        thrown(SchemaUnknownException)
    }

    def "should throw an org.osiam.resources.exceptions.SchemaUnknownException when User schema is unknown"() {
        given:
        def schema = ["moep"] as Set
        User user = new User.Builder("test").setSchemas(schema).build()
        joint.args >> [user]
        when:
        underTest.checkUser(joint)
        then:
        thrown(SchemaUnknownException)

    }

    def "should do nothing when schema is correct"() {
        given:
        def schema = Constants.CORE_SCHEMAS
        User user = new User.Builder("test").setSchemas(schema).build()
        joint.args >> [user]
        when:
        underTest.checkUser(joint)
        then:
        notThrown(SchemaUnknownException)
    }

    def "should contain controller and method pointcut"(){
        when:
        underTest.methodPointcut()
        Method methodPointcut = CheckSchema.class.getDeclaredMethod("methodPointcut")
        underTest.controllerBean()
        Method controllerPointcut = CheckSchema.class.getDeclaredMethod("controllerBean")
        then:
        def mPointcut = methodPointcut.getAnnotation(Pointcut)
        mPointcut.value() == "execution(* *(..))"
        def cPointcut = controllerPointcut.getAnnotation(Pointcut)
        cPointcut.value() == "within(@org.springframework.stereotype.Controller *)"

    }

    def "checkUser should ref controller and method pointcut in before"(){
        when:
        Method checkUser = CheckSchema.class.getDeclaredMethod("checkUser", JoinPoint.class)
        then:
        def before = checkUser.getAnnotation(Before)
        before.value() == "controllerBean() && methodPointcut() "

    }

}
