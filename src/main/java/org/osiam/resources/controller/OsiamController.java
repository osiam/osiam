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
package org.osiam.resources.controller;

import org.osiam.resources.provisioning.SCIMExtensionProvisioning;
import org.osiam.resources.provisioning.model.ExtensionDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This controller contains osiam specific interfaces.
 */
@RestController
@RequestMapping(value = "/osiam")
public class OsiamController {

    private final SCIMExtensionProvisioning extensionProvisioning;

    @Autowired
    public OsiamController(SCIMExtensionProvisioning extensionProvisioning) {
        this.extensionProvisioning = extensionProvisioning;
    }

    /**
     * This handler is responsible for getting all existing extension definitions.
     *
     * @return The list with all extension definitions.
     */
    @RequestMapping(value = "/extension-definition", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<ExtensionDefinition> getAllExtensions() {
        return extensionProvisioning.getAllExtensionDefinitions();
    }
}
