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

package org.osiam.resources.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.resources.ClientSpring;
import org.osiam.resources.RoleSpring;
import org.osiam.resources.UserSpring;
import org.osiam.storage.dao.ClientDao;
import org.osiam.storage.dao.UserDAO;
import org.osiam.storage.entities.ClientEntity;
import org.osiam.storage.entities.RolesEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This Controller is used to manage client authentication.
    TODO
 */
@Controller
@RequestMapping(value = "/authentication")
public class AuthenticationController {

    @Inject
    private UserDAO userDAO;

    @Inject
    private ClientDao clientDao;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public UserSpring getUser(@PathVariable final String id) {

        UserEntity dbUser = userDAO.getByUsername(id);
        Set<RoleSpring> springRoles = new HashSet<>();
        for (RolesEntity role : dbUser.getRoles()) {
            RoleSpring springRole = new RoleSpring();
            springRole.setValue(role.getValue());
            springRoles.add(springRole);
        }
        UserSpring springUser = new UserSpring();
        springUser.setUserName(dbUser.getUserName());
        springUser.setPassword(dbUser.getPassword());
        springUser.setRoles(springRoles);

        return springUser;
    }

    @RequestMapping(value = "/client/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ClientSpring getClient(@PathVariable final String id) {

        ClientEntity dbClient = clientDao.getClient(id);
        ClientSpring springClient = new ClientSpring();
        springClient.setId(dbClient.getId());
        springClient.setClientSecret(dbClient.getClientSecret());
        springClient.setScope(dbClient.getScope());
        springClient.setGrants(dbClient.getGrants());
        springClient.setRedirectUri(dbClient.getRedirectUri());
        springClient.setAccessTokenValiditySeconds(dbClient.getAccessTokenValiditySeconds());

        return springClient;
    }

    @RequestMapping(value = "/client/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateClientExpiry(@PathVariable final String id, @RequestBody Date expiry) throws IOException {

        ClientEntity dbClient = clientDao.getClient(id);
        dbClient.setExpiry(expiry);

        clientDao.update(dbClient, id);
    }

}
