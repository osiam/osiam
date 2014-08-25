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

package org.osiam.security.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for retrieving the model for and displaying the confirmation page for access to a protected resource.
 *
 */
@Controller
@SessionAttributes("authorizationRequest")
@RequestMapping("/oauth")
public class AccessConfirmationController {

    @Inject
    private ClientDetailsService clientDetailsService;

    @RequestMapping("/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model) {

        AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
        if (clientAuth == null) {
            return new ModelAndView("redirect:/oauth/error");
        }
        ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId()); // NOSONAR
        // Sonar message: clientDetailsService is initialized via setter injection
        if (client == null) {
            return new ModelAndView("redirect:/oauth/error");
        }
        model.put("auth_request", clientAuth);
        model.put("client", client);
        model.put("loginError", false);

        return new ModelAndView("access_confirmation", model);
    }

    @RequestMapping("/error")
    public String handleError() {
        return "oauth_error";
    }

}