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

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Inject
    private HttpSession session;

    @Value("${org.osiam.auth-server.ldap.enabled:false}")
    private boolean isLdapConfigured;

    @Value("${org.osiam.auth-server.tempLock.timeout:0}")
    private int templockTimeout;

    @RequestMapping
    public String login(Model model) {
        model.addAttribute("isLdapConfigured", isLdapConfigured);

        if (userIsLocked()) {
            model.addAttribute("locked", true);
        }

        return "login";
    }

    @RequestMapping("/error")
    public String loginError(Model model, Exception e) {
        model.addAttribute("loginError", true);
        model.addAttribute("isLdapConfigured", isLdapConfigured);

        if(userIsLocked()) {
            model.addAttribute("errorKey", "login.lock");
            model.addAttribute("templockTimeout", templockTimeout);
        } else {
            model.addAttribute("errorKey", "login.error");
        }

        return "login";
    }

    private boolean userIsLocked() {
        if (session.getAttribute("IS_LOCKED") != null && session.getAttribute("IS_LOCKED") instanceof Boolean) {
            if ((Boolean) session.getAttribute("IS_LOCKED")) {
                return true;
            }
        }
        return false;
    }
}
