package org.osiam.security.controller.web;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {

    @Inject
    ServletContext context;

    /**
     * Generates a form with all needed fields for creating a new user.
     *
     * @param authorization
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public void index(@RequestHeader final String authorization, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        InputStream in = context.getResourceAsStream("/WEB-INF/registration/registration.html");
        IOUtils.copy(in, response.getOutputStream());
    }

    /**
     *
     * Creates a new User.
     *
     * Needs all data given by the 'index'-form. Saves the user in an inactivate-state. Sends an activation-email to
     * the registered email-address.
     * @param authorization
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> create(@RequestHeader final String authorization) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Activates a created user.
     *
     * @param authorization
     * @param user
     * @param token
     * @return
     */
    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public ResponseEntity<String> activate(@RequestHeader final String authorization,
                            @RequestParam("user") final String user, @RequestParam("token") final String token) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}


