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
package org.osiam.resources.scim;

/**
 * Java class for extension enterprise.
 */
public final class Enterprise {

    private final String employeeNumber;
    private final String costCenter;
    private final String organization;
    private final String division;
    private final String department;
    private final Manager manager;

    private Enterprise(Builder builder) {
        this.employeeNumber = builder.employeeNumber;
        this.costCenter = builder.costCenter;
        this.organization = builder.organization;
        this.division = builder.division;
        this.department = builder.department;
        this.manager = builder.manager;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public String getOrganization() {
        return organization;
    }

    public String getDivision() {
        return division;
    }

    public String getDepartment() {
        return department;
    }

    public Manager getManager() {
        return manager;
    }

    public static class Builder {
        private String employeeNumber;
        private String costCenter;
        private String organization;
        private String division;
        private String department;
        private Manager manager;

        public Builder setEmployeeNumber(String employeeNumber) {
            this.employeeNumber = employeeNumber;
            return this;
        }

        public Builder setCostCenter(String costCenter) {
            this.costCenter = costCenter;
            return this;

        }

        public Builder setOrganization(String organization) {
            this.organization = organization;
            return this;

        }

        public Builder setDivision(String division) {
            this.division = division;
            return this;

        }

        public Builder setDepartment(String department) {
            this.department = department;
            return this;

        }

        public Builder setManager(Manager manager) {
            this.manager = manager;
            return this;
        }

        public Enterprise build() {
            return new Enterprise(this);
        }
    }

}
