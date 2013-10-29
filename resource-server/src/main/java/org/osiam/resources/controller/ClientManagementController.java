package org.osiam.resources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.osiam.storage.dao.ClientDao;
import org.osiam.storage.entities.ClientEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;

@Controller
@RequestMapping(value = "/Client")
/**
 * Is the http api for clients. You can get, create and delete a client.
 */
public class ClientManagementController {

    @Inject
    private ClientDao clientDao;

    private ObjectMapper mapper = new ObjectMapper();


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ClientEntity getClient(@PathVariable final String id) {
        return clientDao.getClient(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ClientEntity create(@RequestBody String client) throws IOException {
        return clientDao.create(getClientEntity(client));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable final String id) {
        clientDao.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ClientEntity update(@PathVariable final String id, @RequestBody String client) throws IOException {
        return clientDao.update(getClientEntity(client), id);
    }

    private ClientEntity getClientEntity(String client) throws IOException {
        return new ClientEntity(mapper.readValue(client, ClientEntity.class));
    }
}