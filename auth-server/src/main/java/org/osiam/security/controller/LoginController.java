package org.osiam.security.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Value("${org.osiam.auth.ldap.enabled:false}")
    private boolean isLdapConfigured;

    @RequestMapping
    public String login(Model model) {
        model.addAttribute("isLdapConfigured", isLdapConfigured);
        return "login";
    }

    @RequestMapping("/error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("isLdapConfigured", isLdapConfigured);
        model.addAttribute("errorKey", "login.error");
        return "login";
    }
}