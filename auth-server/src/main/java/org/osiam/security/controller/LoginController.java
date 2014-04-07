package org.osiam.security.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osiam.resources.UserSpring;
import org.osiam.security.authentication.AuthenticationBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Inject
    LdapAuthenticationProvider ldapAuthProvider;
    
    @Inject
    AuthenticationBean userDetailsService;
    
    @Inject
    ShaPasswordEncoder passwordEncoder;
    
    @Inject
    HttpServletRequest request;
    
    @Inject
    SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler;

    @RequestMapping
    public String login(Model model) {
        List<String> loginModi = new ArrayList<String>();
        loginModi.add("Normal");
        loginModi.add("LDAP");
        model.addAttribute(loginModi);
        return "login";
    }

    @RequestMapping("/error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String loginCheck(@RequestParam String j_username,
            @RequestParam(required = false, defaultValue = "normal") String loginModus,
            @RequestParam String j_password, HttpServletResponse response) {
        Authentication authentication = null;
        String targetURL = "login/error";
        try {
            if(loginModus.equals("normal")) {
                UserSpring user = (UserSpring) userDetailsService.loadUserByUsername(j_username);
                if(!passwordEncoder.isPasswordValid(user.getPassword(), j_password, user.getId())) {
                    return "redirect:login/error";
                }
                targetURL = getTargetURL(request, response);
                authentication = new UsernamePasswordAuthenticationToken(user, j_password, user.getAuthorities());
            }
            else if(loginModus.equals("ldap")) {
                targetURL = getTargetURL(request, response);
                Authentication authRequest = new UsernamePasswordAuthenticationToken(j_username, j_password);
                authentication = ldapAuthProvider.authenticate(authRequest);
                userDetailsService.createNewUser(j_username, getClientId(request, response));
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            
            return "redirect:" + targetURL;
        } catch (Exception e) {
            e.printStackTrace();
            SecurityContextHolder.getContext().setAuthentication(null);
            return "redirect:login/error";
        }
    }
    
    private String getClientId(HttpServletRequest request, HttpServletResponse response) {
        RequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if(savedRequest != null) {
            String[] clientIds = savedRequest.getParameterMap().get("client_id");
            if(clientIds.length > 0) {
                return clientIds[0];
            }
        }
        return "client_id";
    }
    
    private String getTargetURL(HttpServletRequest request, HttpServletResponse response) {
        RequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if(savedRequest != null) {
            return savedRequest.getRedirectUrl();
        }
        return request.getRequestURL().toString();
    }
}