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
