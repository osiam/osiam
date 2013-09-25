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

package org.osiam.storage.entities

import org.osiam.storage.entities.EnterpriseEntity
import org.osiam.storage.entities.ManagerEntity
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.03.13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
class EnterpriseEntitySpec extends Specification {

    EnterpriseEntity enterpriseEntity = new EnterpriseEntity()

    def "setter and getter for the Id should be present"() {
        when:
        enterpriseEntity.setId(123456)

        then:
        enterpriseEntity.getId() == 123456
    }

    def "setter and getter for the employee number should be present"() {
        when:
        enterpriseEntity.setEmployeeNumber("701984")

        then:
        enterpriseEntity.getEmployeeNumber() == "701984"
    }

    def "setter and getter for the cost center should be present"() {
        when:
        enterpriseEntity.setCostCenter("4130")

        then:
        enterpriseEntity.getCostCenter() == "4130"
    }

    def "setter and getter for the organization should be present"() {
        when:
        enterpriseEntity.setOrganization("Universal Studios")

        then:
        enterpriseEntity.getOrganization() == "Universal Studios"
    }

    def "setter and getter for the division should be present"() {
        when:
        enterpriseEntity.setDivision("Theme Park")

        then:
        enterpriseEntity.getDivision() == "Theme Park"
    }

    def "setter and getter for the department should be present"() {
        when:
        enterpriseEntity.setDepartment("Tour Operations")

        then:
        enterpriseEntity.getDepartment() == "Tour Operations"
    }

    def "setter and getter for the manager should be present"() {
        given:
        def manager = Mock(ManagerEntity)

        when:
        enterpriseEntity.setManager(manager)

        then:
        enterpriseEntity.getManager() == manager
    }

    def "mapping to scim should be present"() {
        when:
        def enterprise = enterpriseEntity.toScim()

        then:
        enterprise.costCenter == enterpriseEntity.costCenter
        enterprise.department == enterpriseEntity.department
        enterprise.division == enterpriseEntity.division
        enterprise.employeeNumber == enterpriseEntity.employeeNumber
        enterprise.manager == enterpriseEntity.manager
        enterprise.organization == enterpriseEntity.organization
    }

    def "should map manager if present"() {
        given:
        enterpriseEntity.setManager(new ManagerEntity())
        when:
        def enterprise = enterpriseEntity.toScim()

        then:
        enterprise.costCenter == enterpriseEntity.costCenter
        enterprise.department == enterpriseEntity.department
        enterprise.division == enterpriseEntity.division
        enterprise.employeeNumber == enterpriseEntity.employeeNumber

        enterprise.organization == enterpriseEntity.organization
        enterprise.manager
    }
}