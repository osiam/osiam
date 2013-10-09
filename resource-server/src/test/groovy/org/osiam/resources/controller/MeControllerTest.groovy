package org.osiam.resources.controller

import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.osiam.security.authorization.AccessTokenValidationService
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.UserEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class   MeControllerTest extends Specification {
    def accessTokenValidationService = Mock(AccessTokenValidationService)
    def underTest = new MeController(accessTokenValidationService: accessTokenValidationService)
    OAuth2Authentication authentication = Mock(OAuth2Authentication)
    HttpServletRequest request = Mock(HttpServletRequest)
    Authentication userAuthentication = Mock(Authentication)
    def name = new NameEntity(familyName: "Prefect", givenName: "Fnord", formatted: "Fnord Prefect")
    def user = new UserEntity(active: true, emails: [new EmailEntity(primary: true, value: "test@test.de")],
            name: name, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
            locale: "de_DE", userName: "fpref")
    DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime()

    def setup() {
        authentication.getUserAuthentication() >> userAuthentication
    }

    def "should return correct facebook representation"() {
        when:
        def result = underTest.getInformation(request)

        then:
        1 * request.getParameter("access_token") >> "access_token"
        1 * accessTokenValidationService.loadAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> user
        result.email == "test@test.de"
        result.first_name == user.getName().getGivenName()
        result.last_name == user.getName().getFamilyName()
        result.gender == "not supported."
        result.link == "not supported."
        result.locale == "de_DE"
        result.name == user.getName().getFormatted()
        result.timezone == 2
        result.updated_time == dateTimeFormatter.print(user.getMeta().getLastModified().time)
        result.userName == "fpref"
        result.id == user.getId().toString()
        result.isVerified()
    }

    def "should not provide an email address if no primary email exists"() {
        given:
        def user = new UserEntity(active: true, name: name, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
                emails: [new EmailEntity(primary: false, value: "test@test.de")], locale: "de_DE", userName: "fpref")
        request.getParameter("access_token") >> "access_token"
        accessTokenValidationService.loadAuthentication("access_token") >> authentication
        userAuthentication.getPrincipal() >> user

        when:
        def result = underTest.getInformation(request)

        then:
        result.getEmail() == null
    }

    def "should throw exception when no access_token was submitted"() {
        given:
        request.getParameter("access_token") >> null
        when:
        underTest.getInformation(request)
        then:
        def e = thrown(IllegalArgumentException)
        e.message == "No access_token submitted!"

    }

    def "should get access_token in bearer format"() {
        when:
        def result = underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> null
        1 * request.getHeader("Authorization") >> "Bearer access_token"
        1 * accessTokenValidationService.loadAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> user
        result

    }


    def "should throw exception if principal is not an UserEntity"() {
        when:
        underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> null
        1 * request.getHeader("Authorization") >> "Bearer access_token"
        1 * accessTokenValidationService.loadAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> new Object()
        def e = thrown(IllegalArgumentException)
        e.message == "User was not authenticated with OSIAM."
    }

    def "should not provide an email address if no emails was submitted"() {
        given:
        def user = new UserEntity(active: true, emails: null,
                name: name, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
                locale: "de_DE", userName: "fpref")
        when:
        def result = underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> null
        1 * request.getHeader("Authorization") >> "Bearer access_token"
        1 * accessTokenValidationService.loadAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> user
        result.getEmail() == null
    }

    def "should not provide name, first name, last name if no name was submitted"() {
        given:
        def user = new UserEntity(active: true, emails: [new EmailEntity(primary: true, value: "test@test.de")],
                name: null, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
                locale: "de_DE", userName: "fpref")

        when:
        def result = underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> null
        1 * request.getHeader("Authorization") >> "Bearer access_token"
        1 * accessTokenValidationService.loadAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> user
        result.getName() == null
        result.getFirst_name() == null
        result.getLast_name() == null
    }
}