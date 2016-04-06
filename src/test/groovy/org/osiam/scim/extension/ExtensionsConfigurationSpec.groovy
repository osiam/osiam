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
package org.osiam.scim.extension

import org.osiam.Osiam
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.storage.ExtensionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.EnvironmentTestUtils
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.ConfigurableEnvironment
import spock.lang.Specification

class ExtensionsConfigurationSpec extends Specification {

    def extensionRepository = Mock(ExtensionRepository)
    def ExtensionsConfiguration extensionsConfig = new ExtensionsConfiguration(extensionRepository)

    def 'Creates the configured extension, if it does not exist'() {
        given:
        def extensionToCreate = new ExtensionsConfiguration.Extension()
        extensionToCreate.urn = 'urn'
        def fieldToCreate = new ExtensionsConfiguration.Field()
        fieldToCreate.name = 'name'
        fieldToCreate.type = 'string'
        fieldToCreate.required = true
        extensionToCreate.getFields().add(fieldToCreate)
        extensionsConfig.extensions.add(extensionToCreate)
        extensionRepository.existsByUrnIgnoreCase('urn') >> false

        when:
        extensionsConfig.createExtensions()

        then:
        1 * extensionRepository.saveAndFlush({ extension ->
            extension.urn == 'urn'
            def field = extension.fields.iterator().next()
            field.name == 'name'
            field.required == true
            field.type == ExtensionFieldType.STRING
        })
    }

    def 'Does not do anything, if the configured extension already exists'() {
        given:
        def extensionToCreate = new ExtensionsConfiguration.Extension()
        extensionToCreate.urn = 'urn'
        def fieldToCreate = new ExtensionsConfiguration.Field()
        fieldToCreate.name = 'name'
        fieldToCreate.type = 'string'
        extensionToCreate.getFields().add(fieldToCreate)
        extensionsConfig.extensions.add(extensionToCreate)
        extensionRepository.existsByUrnIgnoreCase('urn') >> true

        when:
        extensionsConfig.createExtensions()

        then:
        0 * extensionRepository.saveAndFlush(_)
    }
}
