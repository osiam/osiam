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

import javax.servlet.http.HttpServletRequest

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.controller.UserController
import org.osiam.resources.provisioning.SCIMUserProvisioning
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User

import spock.lang.Specification

class IsoDateFormatSpec extends Specification{

    def provisioning = Mock(SCIMUserProvisioning)
    def userController = new UserController(scimUserProvisioning: provisioning, requestParamHelper: new RequestParamHelper())
    def servletRequestMock = Mock(HttpServletRequest)

    def "should return the date fields in iso format"() {
        given:
        def actualDate = GregorianCalendar.getInstance().getTime()
        def dateTimeFormatter = ISODateTimeFormat.dateTime();
        def date = new DateTime(actualDate)
        def created = dateTimeFormatter.print(date)

        def user = new User.Builder("username").setMeta(new Meta.Builder(actualDate, null).build()).build()
        def scimSearchResult = new SCIMSearchResult([user] as List, 23, 100, 0, ["urn:scim:schemas:core:1.0"] as Set)

        when:
        def result = userController.searchWithGet(servletRequestMock)

        then:
        2 * servletRequestMock.getParameter("attributes") >> "meta.created"
        1 * provisioning.search(_, _, _, _, _) >> scimSearchResult

        result.getResources() == [[meta:[created:created]]] as List
        result.getItemsPerPage() == 100
        result.getStartIndex() == 0
        result.getTotalResults() == 23
        result.getSchemas() == ["urn:scim:schemas:core:1.0"] as Set
    }
}