package org.osiam.security.controller;

import javax.inject.Inject;

import org.osiam.security.authentication.ClientDetailsLoadingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @Inject
    ClientDetailsLoadingBean clientDetailsLoadingBean;
    
    @RequestMapping("/loginform")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }
    
    @RequestMapping("/login-success")
    public String loginSuccess() {
        return "redirect:login";
    }
}