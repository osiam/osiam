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
import org.osiam.auth.token.TokenService
import org.osiam.resources.helper.AttributesRemovalHelper
import org.osiam.resources.provisioning.SCIMUserProvisioning
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import spock.lang.Specification

class ShowComplexAttributeFilterSpec extends Specification {

    def provisioning = Mock(SCIMUserProvisioning)
    def tokenService = Mock(TokenService)
    def userController = new UserController(provisioning, tokenService, new AttributesRemovalHelper())

    def "should be able to just show a field of an complex type"() {
        given:
        def parameterMap = ['attributes': 'meta.created']

        def currentDate = GregorianCalendar.getInstance().getTime()
        def dateTimeFormatter = ISODateTimeFormat.dateTime()
        def date = new DateTime(currentDate)
        def created = dateTimeFormatter.print(date)

        def user = new User.Builder("username").setMeta(new Meta.Builder(currentDate, null).build()).build()
        def scimSearchResult = new SCIMSearchResult([user] as List, 23, 100, 0)
        when:
        def result = userController.searchWithPost(parameterMap)

        then:
        1 * provisioning.search(_, _, _, _, _) >> scimSearchResult

        result.getResources() == [[meta: [created: created], schemas: [User.SCHEMA]]] as List
        result.getItemsPerPage() == 100
        result.getStartIndex() == 0
        result.getTotalResults() == 23
        result.getSchemas() == [SCIMSearchResult.SCHEMA] as Set
    }
}
