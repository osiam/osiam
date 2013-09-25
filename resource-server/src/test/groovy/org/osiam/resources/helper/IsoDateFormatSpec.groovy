package org.osiam.resources.helper

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.provisioning.SCIMUserProvisioning
import org.osiam.resources.controller.UserController
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 23.05.13
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */
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