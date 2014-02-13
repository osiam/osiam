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

package org.osiam.resources.exceptions

import spock.lang.Specification

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