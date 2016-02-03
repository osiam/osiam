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

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final String SESSION_ERROR_KEY = "ERROR_KEY";
    private static final String MODEL_ERROR_KEY = "errorKey";

    @Autowired
    private HttpSession session;

    @Value("${org.osiam.ldap.enabled:false}")
    private boolean isLdapConfigured;

    @Value("${org.osiam.tempLock.timeout:0}")
    private int templockTimeout;

    @RequestMapping
    public String login() {
        return "login";
    }

    @RequestMapping("/error")
    public ModelAndView loginError() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginError", true);

        if (userIsLocked()) {
            modelAndView.addObject(MODEL_ERROR_KEY, "login.lock");
            modelAndView.addObject("templockTimeout", templockTimeout);
            return modelAndView;
        }

        Optional<String> errorMessageFromSession = getErrorMessageFromSession();
        if (errorMessageFromSession.isPresent()) {
            modelAndView.addObject(MODEL_ERROR_KEY, errorMessageFromSession.get());
            return modelAndView;
        }

        modelAndView.addObject(MODEL_ERROR_KEY, "login.error");
        return modelAndView;
    }

    @ModelAttribute("isLdapConfigured")
    public boolean isLdapConfigured() {
        return isLdapConfigured;
    }

    private Optional<String> getErrorMessageFromSession() {
        Object attribute = session.getAttribute(SESSION_ERROR_KEY);
        if (attribute == null || !(attribute instanceof String)) {
            return Optional.empty();
        }
        String errorMessage = (String) attribute;
        session.removeAttribute(SESSION_ERROR_KEY);
        return Optional.ofNullable(Strings.emptyToNull(errorMessage));
    }

    private boolean userIsLocked() {
        return session.getAttribute("IS_LOCKED") != null && session.getAttribute("IS_LOCKED") instanceof Boolean
                && (Boolean) session.getAttribute("IS_LOCKED");

    }
}
