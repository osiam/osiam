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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.RoleSpring;
import org.osiam.resources.UserSpring;
import org.osiam.storage.dao.UserDao;
import org.osiam.storage.entities.RoleEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This Controller is used to manage client and user authentication for spring security OAuth.
 */
@Controller
@RequestMapping(value = "/authentication")
@Transactional
public class AuthenticationController {

    @Inject
    private UserDao userDao;

    /*@Inject
    private ClientDao clientDao;*/

    @RequestMapping(value = "/user",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public UserSpring getUser(@RequestBody final String userName) {
        UserEntity dbUser = userDao.getByUsername(userName);
        return getUserSpring(dbUser);
    }

    private UserSpring getUserSpring(UserEntity dbUser) {

        Set<RoleSpring> springRoles = new HashSet<>();
        for (RoleEntity role : dbUser.getRoles()) {
            RoleSpring springRole = new RoleSpring();
            springRole.setValue(role.getValue());
            springRoles.add(springRole);
        }

        UserSpring springUser = new UserSpring();
        springUser.setUsername(dbUser.getUserName());
        springUser.setId(dbUser.getId().toString());
        springUser.setPassword(dbUser.getPassword());
        springUser.setRoles(springRoles);
        springUser.setActive(dbUser.getActive());
        return springUser;
    }

    /*@RequestMapping(value = "/client/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ClientSpring getClient(@PathVariable final String id) {

        ClientEntity dbClient = clientDao.getClient(id);
        return getClientSpring(dbClient);
    }*/

    /*private ClientSpring getClientSpring(ClientEntity dbClient) {
        ClientSpring springClient = new ClientSpring();
        springClient.setId(dbClient.getId());
        springClient.setClientSecret(dbClient.getClientSecret());
        springClient.setScope(dbClient.getScope());
        springClient.setGrants(dbClient.getGrants());
        springClient.setRedirectUri(dbClient.getRedirectUri());
        springClient.setAccessTokenValiditySeconds(dbClient.getAccessTokenValiditySeconds());
        springClient.setRefreshTokenValiditySeconds(dbClient.getRefreshTokenValiditySeconds());
        springClient.setImplicit(dbClient.isImplicit());
        springClient.setExpiry(dbClient.getExpiry());
        springClient.setValidityInSeconds(dbClient.getValidityInSeconds());
        return springClient;
    }*/

    /*@RequestMapping(value = "/client/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateClientExpiry(@PathVariable final String id, @RequestBody String body) throws IOException,
            ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        String theDate = body.split("=")[1];
        String expiry = URLDecoder.decode(theDate, "UTF-8");

        ClientEntity dbClient = clientDao.getClient(id);
        dbClient.setExpiry(sdf.parse(expiry));
        clientDao.update(dbClient, id);
    }*/
}