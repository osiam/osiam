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
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller for retrieving the model for and displaying the confirmation page for access to a protected resource.
 *
 */
@Controller
@SessionAttributes("authorizationRequest")
public class AccessConfirmationController {

    private ClientDetailsService clientDetailsService;

    @RequestMapping("/oauth/confirm_access")
    public String getAccessConfirmation(Model model, HttpServletRequest request) {
        Map<String, Object> modelMap = model.asMap();
        AuthorizationRequest clientAuth = (AuthorizationRequest) modelMap.remove("authorizationRequest");
        ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId()); // NOSONAR - clientDetailsService is initialized via setter injection
        model.addAttribute("auth_request", clientAuth);
        model.addAttribute("client", client);
        model.addAttribute("hasUserRole", request.isUserInRole("ROLE_USER"));
        return "access_confirmation";
    }

    @RequestMapping("/oauth/error")
    public String handleError(Map<String, Object> model) {
        // We can add more stuff to the model here for JSP rendering. If the client was a machine then
        // the JSON will already have been rendered.
        model.put("message", "There was a problem with the OAuth2 protocol");
        return "oauth_error";
    }

    @Inject
    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }
}