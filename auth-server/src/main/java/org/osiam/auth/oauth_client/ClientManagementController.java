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

package org.osiam.auth.oauth_client;

import java.io.*;
import java.util.*;

import org.osiam.auth.exception.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * ClientManagementController realizes the REST API for managing OAuth 2 clients.
 *
 * You can list, get, create, update and delete clients.
 */
@RestController
@RequestMapping(value = "/Client")
public class ClientManagementController {

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ClientEntity getClient(@PathVariable final String id) {
        return findClientOrThrow(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ClientEntity> getClients() {
        return clientRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ClientEntity create(@RequestBody ClientEntity client) throws IOException {
        if (clientRepository.existsById(client.getClientId())) {
            throw new ClientAlreadyExistsException(
                    String.format("The client with the id '%s' already exists", client.getClientId()));
        }
        return clientRepository.save(client);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable final String id) {
        clientRepository.deleteById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ClientEntity update(@PathVariable final String id, @RequestBody ClientEntity client) throws IOException {
        client.setInternalId(findClientOrThrow(id).getInternalId());
        return clientRepository.save(client);
    }

    private ClientEntity findClientOrThrow(@PathVariable String id) {
        ClientEntity client = clientRepository.findById(id);
        if (client == null) {
            throw new ClientNotFoundException("Client with the id '" + id + "' not found.");
        }
        return client;
    }
}
