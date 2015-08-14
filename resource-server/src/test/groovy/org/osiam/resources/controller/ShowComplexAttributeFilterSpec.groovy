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

package org.osiam.resources.controller

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.helper.RequestParamHelper
import org.osiam.resources.provisioning.SCIMUserProvisioning
import org.osiam.resources.scim.Constants
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class ShowComplexAttributeFilterSpec extends Specification {
    def provisioning = Mock(SCIMUserProvisioning)
    def underTest = new UserController(scimUserProvisioning: provisioning, requestParamHelper: new RequestParamHelper())
    def servletRequestMock = Mock(HttpServletRequest)

    def "should be able to just show a field of an complex type"() {
        given:
        def actualDate = GregorianCalendar.getInstance().getTime()
        def dateTimeFormatter = ISODateTimeFormat.dateTime()
        def date = new DateTime(actualDate)
        def created = dateTimeFormatter.print(date)

        def user = new User.Builder("username").setMeta(new Meta.Builder(actualDate, null).build()).build()
        def scimSearchResult = new SCIMSearchResult([user] as List, 23, 100, 0, [Constants.USER_CORE_SCHEMA] as Set)
        when:
        def result = underTest.searchWithPost(servletRequestMock)

        then:
        2 * servletRequestMock.getParameter("attributes") >> "meta.created"
        1 * provisioning.search(_, _, _, _, _) >> scimSearchResult

        result.getResources() == [[meta: [created: created], schemas: [Constants.USER_CORE_SCHEMA]]] as List
        result.getItemsPerPage() == 100
        result.getStartIndex() == 0
        result.getTotalResults() == 23
        result.getSchemas() == [Constants.USER_CORE_SCHEMA] as Set
    }
}
