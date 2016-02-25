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
package org.osiam.test.util

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import groovy.text.SimpleTemplateEngine
import org.osiam.resources.helper.UserDeserializer
import org.osiam.resources.scim.User

class JsonFixturesHelper {

    static String ENTERPRISE_URN = 'urn:scim:schemas:extension:enterprise:2.0:User'

    def tplJsonSimpleUser = getClass().getResourceAsStream('TPL_JSON_SIMPLE_USER.json').text
    def tplJsonPartialCollections = getClass().getResourceAsStream('TPL_JSON_PARTIAL_COLLECTIONS.json').text
    def tplJsonPartialExtension = getClass().getResourceAsStream('TPL_JSON_PARTIAL_EXTENSION.json').text
    def tplJsonPartialWrongTypeExtension = getClass().getResourceAsStream('TPL_JSON_PARTIAL_WRONG_TYPE_EXTENSION.json').text
    def tplJsonPartialEnterpriseUrn = ',"' + ENTERPRISE_URN + '"'

    def templateEngine = new SimpleTemplateEngine()
    def template = templateEngine.createTemplate(tplJsonSimpleUser)

    def jsonSimpleUser = template.make([schemasMore:'', dataMore:'']).toString()
    def jsonBasicUser = template.make([schemasMore:'', dataMore:tplJsonPartialCollections]).toString()
    def jsonExtendedUser = template.make([schemasMore:tplJsonPartialEnterpriseUrn, dataMore:tplJsonPartialCollections + tplJsonPartialExtension]).toString()
    def jsonExtendedUserWithoutExtensionData = template.make([schemasMore:tplJsonPartialEnterpriseUrn, dataMore:tplJsonPartialCollections]).toString()
    def jsonExtendedUserWithWrongFieldType = template.make([schemasMore:tplJsonPartialEnterpriseUrn, dataMore:tplJsonPartialCollections + tplJsonPartialWrongTypeExtension]).toString()

    public ObjectMapper configuredObjectMapper() {
        ObjectMapper mapper = new ObjectMapper()
        SimpleModule testModule = new SimpleModule('MyModule', new Version(1, 0, 0, null))
                .addDeserializer(User, new UserDeserializer())
        mapper.registerModule(testModule)
        return mapper
    }
}
