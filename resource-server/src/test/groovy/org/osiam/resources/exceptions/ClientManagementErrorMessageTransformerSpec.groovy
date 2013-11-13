package org.osiam.resources.exceptions

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 11.07.13
 * Time: 08:44
 * To change this template use File | Settings | File Templates.
 */
class ClientManagementErrorMessageTransformerSpec extends Specification {

    def clientManagementErrorMessageTransformer = new ClientManagementErrorMessageTransformer()

    def "should return expected error message for client id uniqueness injury"() {
        given:
        def message = "FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »osiam_client_id_key«\n " +
                "Detail: Schlüssel »(id)=(ClientID)« existiert bereits.; SQL [n/a]; constraint [null]; " +
                "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                "FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »osiam_client_id_key«\n " +
                "Detail: Schlüssel »(id)=(ClientID)« existiert bereits."
        when:
        def result = clientManagementErrorMessageTransformer.transform(message)
        then:
        result == "The client with the Id ClientID already exists."
    }

    def "should return expected error message for client redirect uri uniqueness injury"() {
        given:
        def message = "FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »osiam_client_redirect_uri_key«\n " +
                "Detail: Schlüssel »(redirect_uri)=(http://localhost:5000/stuff)« existiert bereits.; " +
                "SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: " +
                "FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »osiam_client_redirect_uri_key«\n " +
                "Detail: Schlüssel »(redirect_uri)=(http://localhost:5000/stuff)« existiert bereits."
        when:
        def result = clientManagementErrorMessageTransformer.transform(message)
        then:
        result == "Another client already defines the redirect URI http://localhost:5000/stuff"
    }

    def "should return null if message is null"() {
        when:
        def result = clientManagementErrorMessageTransformer.transform(null)
        then:
        result == null
    }

    def "should return original message if nothing matches"() {
        when:
        def result = clientManagementErrorMessageTransformer.transform("nothing to match here")
        then:
        result == "nothing to match here"
    }
}