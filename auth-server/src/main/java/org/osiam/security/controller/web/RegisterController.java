package org.osiam.security.controller.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {

    /**
     * Generates a form with all needed fields for creating a new user.
     *
     * @param clientToken The OAuth-Client-Token
     * @return
     */
    @RequestMapping(value="/", method=RequestMethod.GET, produces = "text/html")
    public ResponseEntity<String> index(@RequestHeader final String clientToken) {
        String htmlForm = "<form>blabla</form>";
        return new ResponseEntity<>(htmlForm, HttpStatus.OK);
    }

    /**
     *
     * Creates a new User.
     *
     * Needs all data given by the 'index'-form. Saves the user in an inactivate-state. Sends an activation-email to
     * the registered email-address.
     * @param clientToken
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> create(@RequestHeader final String clientToken) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Activates a created user.
     *
     * @param clientToken
     * @param user
     * @param token
     * @return
     */
    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public ResponseEntity<String> activate(@RequestHeader final String clientToken,
                            @RequestParam("user") final String user, @RequestParam("token") final String token) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}


