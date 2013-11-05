package org.osiam.security.controller.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    /**
     * Generates a form with all needed fields for creating a new user.
     *
     * @param clientToken The OAuth-Client-Token
     * @return
     */
    @RequestMapping(value="/register/", method=RequestMethod.GET, produces = "text/html")
    public ResponseEntity<String> index(@RequestHeader String clientToken) {
        String htmlForm = "<form>blabla</form>";
        return new ResponseEntity<String>(htmlForm, HttpStatus.OK);
    }

    /**
     *
     * Creates a new User.
     *
     * Needs all data given by the 'index'-form. Saves the user in an inactivate-state. Sends an activation-email to
     * the registered email-address.
     * @param clientToken
     */
    @RequestMapping("/register/create")
    public ResponseEntity<String> create(@RequestHeader String clientToken) {
        return null;

    }

    /**
     * Activates a created user.
     *
     * @param clientToken
     * @param user
     * @param token
     */
    @RequestMapping("/register/activate")
    public void activate(@RequestHeader String clientToken, @RequestParam String user, @RequestParam String token) {

    }
}


