package org.osiam.resources.helper

import org.hibernate.criterion.Criterion
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.criteria.AbstractQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class UserCombinedFilterChainSpec extends Specification {


    def em = Mock(EntityManager)

    def 'should be able to parse title pr and userType eq Employee'() {
        given:
        def filter = 'title pr and userType eq "Employee"'

        when:
        def filterChain = new UserCombinedFilterChain(em, filter)

        then:
        filterChain.leftTerm instanceof UserSimpleFilterChain
        filterChain.combinedWith == UserCombinedFilterChain.Combiner.AND
        filterChain.rightTerm instanceof UserSimpleFilterChain
    }

    def 'should be able to parse title pr or userType eq Intern'() {
        given:
        def filter = 'title pr or userType eq "Intern"'   // TODO: strip quotes
        when:
        def filterChain = new UserCombinedFilterChain(em, filter)

        then:
        filterChain.leftTerm instanceof UserSimpleFilterChain
        filterChain.leftTerm.field == 'title'
        filterChain.leftTerm.constraint == FilterConstraint.PRESENT
        filterChain.leftTerm.value == ""
        filterChain.combinedWith == UserCombinedFilterChain.Combiner.OR
        filterChain.rightTerm instanceof UserSimpleFilterChain
        filterChain.rightTerm.field == 'userType'
        filterChain.rightTerm.constraint == FilterConstraint.EQUALS
        filterChain.rightTerm.value == '"Intern"'

    }

    def 'should be able to parse userType eq Employee and (emails co example.com or emails co example.org)'() {
        given:
        def filter = 'userType eq "Employee" and (emails.value co "example.com" or emails.value co "example.org")'
        when:
        def filterChain = new UserCombinedFilterChain(em, filter)
        then:
        filterChain.leftTerm instanceof UserSimpleFilterChain
        filterChain.leftTerm.field == 'userType'
        filterChain.leftTerm.constraint == FilterConstraint.EQUALS
        filterChain.leftTerm.value == 'Employee'
        filterChain.combinedWith == UserCombinedFilterChain.Combiner.AND
        filterChain.rightTerm instanceof UserCombinedFilterChain
        filterChain.rightTerm.combinedWith == UserCombinedFilterChain.Combiner.OR
        filterChain.rightTerm.leftTerm instanceof UserSimpleFilterChain
        filterChain.rightTerm.leftTerm.field == 'emails.value'
        filterChain.rightTerm.leftTerm.constraint == FilterConstraint.CONTAINS
        filterChain.rightTerm.leftTerm.value == '"example.com"'
        filterChain.rightTerm.rightTerm instanceof UserSimpleFilterChain
        filterChain.rightTerm.rightTerm.key == 'emails.value'
        filterChain.rightTerm.rightTerm.constraint == FilterConstraint.CONTAINS
        filterChain.rightTerm.rightTerm.value == '"example.org"'
    }

    def 'should be able to parse userType eq Employee aNd (emails co example.com Or emails co example.org)'() {
        given:
        def filter = 'userType eq "Employee" aNd (emails.value co "example.com" Or emails.value co "example.org")'

        when:
        def cf = new UserCombinedFilterChain(em, filter)

        then:
        cf.leftTerm instanceof UserSimpleFilterChain
        cf.leftTerm.field == 'userType'
        cf.leftTerm.constraint == FilterConstraint.EQUALS
        cf.leftTerm.value == '"Employee"'
        cf.combinedWith == UserCombinedFilterChain.Combiner.AND
        cf.rightTerm instanceof UserCombinedFilterChain
        cf.rightTerm.combinedWith == UserCombinedFilterChain.Combiner.OR
        cf.rightTerm.leftTerm instanceof UserSimpleFilterChain
        cf.rightTerm.leftTerm.field == 'emails.value'
        cf.rightTerm.leftTerm.constraint == FilterConstraint.CONTAINS
        cf.rightTerm.leftTerm.value == '"example.com"'
        cf.rightTerm.rightTerm instanceof UserSimpleFilterChain
        cf.rightTerm.rightTerm.field == 'emails.value'
        cf.rightTerm.rightTerm.constraint == FilterConstraint.CONTAINS
        cf.rightTerm.rightTerm.value == '"example.org"'
    }

    def 'should throw an exception when filter does not match'() {
        given:
        def filter = 'xxx NOR xxx'

        when:
        new UserCombinedFilterChain(em, filter)

        then:
        thrown(IllegalArgumentException)
    }

    def "should build an Predicate"() {
        given:
        Root<UserEntity> root = Mock()
        AbstractQuery<Long> query = Mock()
        def filter = 'userType eq "Employee" AND (emails.value co "example.com" OR emails.value co "example.org")'
        def chain = new UserCombinedFilterChain(em, filter)

        when:
        def cf = chain.createPredicateAndJoin(query,root)
        then:
        1 * em.getCriteriaBuilder()
    }
}
