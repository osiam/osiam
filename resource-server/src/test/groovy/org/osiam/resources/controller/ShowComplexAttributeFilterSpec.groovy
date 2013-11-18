/*
 * Copyright 2013
 *     tarent AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osiam.resources.controller

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.helper.RequestParamHelper
import org.osiam.resources.provisioning.SCIMUserProvisioning
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
        def dateTimeFormatter = ISODateTimeFormat.dateTime();
        def date = new DateTime(actualDate)
        def created = dateTimeFormatter.print(date)

        def user = new User.Builder("username").setMeta(new Meta.Builder(actualDate, null).build()).build()
        def scimSearchResult = new SCIMSearchResult([user] as List, 23, 100, 0, ["urn:scim:schemas:core:1.0"] as Set)
        when:
        def result = underTest.searchWithPost(servletRequestMock)

        then:
        2 * servletRequestMock.getParameter("attributes") >> "meta.created"
        1 * provisioning.search(_, _, _, _, _) >> scimSearchResult

        result.getResources() == [[meta: [created: created]]] as List
        result.getItemsPerPage() == 100
        result.getStartIndex() == 0
        result.getTotalResults() == 23
        result.getSchemas() == ["urn:scim:schemas:core:1.0"] as Set
    }
}