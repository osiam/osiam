package org.osiam.resources.helper

import org.hibernate.criterion.Criterion
import org.osiam.resources.helper.CombinedFilterChain
import org.osiam.resources.helper.SingularFilterChain
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

class CombinedFilteredSearchTest extends Specification {

    def aClass = UserEntity.class

    def "should be able to parse title pr and userType eq Employee"() {
        given:
        def filter = 'title pr and userType eq "Employee"'
        when:
        def cf = new CombinedFilterChain(filter, aClass)

        then:
        cf.term1 instanceof SingularFilterChain
        cf.combinedWith == CombinedFilterChain.Combiner.AND
        cf.term2 instanceof SingularFilterChain
    }

    def "should be able to parse title pr or userType eq Intern"() {
        given:
        def filter = 'title pr or userType eq "Intern"'
        when:
        def cf = new CombinedFilterChain(filter, aClass)

        then:
        cf.term1 instanceof SingularFilterChain
        cf.term1.key == 'title'
        cf.term1.constraint == SingularFilterChain.Constraints.PRESENT
        cf.term1.value == ""
        cf.combinedWith == CombinedFilterChain.Combiner.OR
        cf.term2 instanceof SingularFilterChain
        cf.term2.key == 'userType'
        cf.term2.constraint == SingularFilterChain.Constraints.EQUALS
        cf.term2.value == 'Intern'

    }

    def "should be able to parse userType eq Employee and (emails co example.com or emails co example.org)"() {
        given:
        def filter = 'userType eq "Employee" and (emails.value co "example.com" or emails.value co "example.org")'
        when:
        def cf = new CombinedFilterChain(filter, aClass)
        then:
        cf.term1 instanceof SingularFilterChain
        cf.term1.key == 'userType'
        cf.term1.constraint == SingularFilterChain.Constraints.EQUALS
        cf.term1.value == 'Employee'
        cf.combinedWith == CombinedFilterChain.Combiner.AND
        cf.term2 instanceof CombinedFilterChain
        cf.term2.combinedWith == CombinedFilterChain.Combiner.OR
        cf.term2.term1 instanceof SingularFilterChain
        cf.term2.term1.key == 'emails.value'
        cf.term2.term1.constraint == SingularFilterChain.Constraints.CONTAINS
        cf.term2.term1.value == 'example.com'
        cf.term2.term2 instanceof SingularFilterChain
        cf.term2.term2.key == 'emails.value'
        cf.term2.term2.constraint == SingularFilterChain.Constraints.CONTAINS
        cf.term2.term2.value == 'example.org'
    }

    def "should be able to parse userType eq Employee aNd (emails co example.com Or emails co example.org)"() {
        given:
        def filter = 'userType eq "Employee" aNd (emails.value co "example.com" Or emails.value co "example.org")'
        when:
        def cf = new CombinedFilterChain(filter, aClass)
        then:
        cf.term1 instanceof SingularFilterChain
        cf.term1.key == 'userType'
        cf.term1.constraint == SingularFilterChain.Constraints.EQUALS
        cf.term1.value == 'Employee'
        cf.combinedWith == CombinedFilterChain.Combiner.AND
        cf.term2 instanceof CombinedFilterChain
        cf.term2.combinedWith == CombinedFilterChain.Combiner.OR
        cf.term2.term1 instanceof SingularFilterChain
        cf.term2.term1.key == 'emails.value'
        cf.term2.term1.constraint == SingularFilterChain.Constraints.CONTAINS
        cf.term2.term1.value == 'example.com'
        cf.term2.term2 instanceof SingularFilterChain
        cf.term2.term2.key == 'emails.value'
        cf.term2.term2.constraint == SingularFilterChain.Constraints.CONTAINS
        cf.term2.term2.value == 'example.org'
    }

    def "should throw an exception when filter does not match"(){
        when:
        new CombinedFilterChain("xxx NOR xxx", aClass)
        then:
        def e = thrown(IllegalArgumentException)
        e.message == "xxx NOR xxx is not a CombinedFilterChain."
    }

    def "should build an criterion"() {
        given:
        def filter = 'userType eq "Employee" AND (emails.value co "example.com" OR emails.value co "example.org")'
        def chain = new CombinedFilterChain(filter, aClass)
        when:
        def cf = chain.buildCriterion()
        then:
        cf instanceof Criterion
    }
}
