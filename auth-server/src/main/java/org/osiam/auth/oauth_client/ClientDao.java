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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.osiam.auth.exception.ClientNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDao {

    @PersistenceContext
    private EntityManager em;

    public ClientEntity getClient(final String id) {
        return getClientById(id);
    }

    public List<ClientEntity> getClients() {
        return em.createNamedQuery("getClients", ClientEntity.class).getResultList();
    }

    public ClientEntity create(final ClientEntity client) {
        em.persist(client);
        return client;
    }

    public void delete(final String id) {
        em.remove(getClientById(id));
    }

    public ClientEntity update(final ClientEntity client, final String id) {
        return em.merge(mergeClient(client, id));
    }

    private ClientEntity mergeClient(final ClientEntity client, final String id) {
        final ClientEntity clientEntity = getClientById(id);

        clientEntity.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        clientEntity.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        clientEntity.setRedirectUri(client.getRedirectUri());
        clientEntity.setScope(client.getScope());
        clientEntity.setImplicit(client.isImplicit());
        clientEntity.setValidityInSeconds(client.getValidityInSeconds());
        clientEntity.setId(client.getId());
        clientEntity.setGrants(client.getGrants());

        return clientEntity;
    }

    private ClientEntity getClientById(final String id) {
        final List<ClientEntity> result = getClientResultList(id);
        if (result.isEmpty()) {
            throw new ClientNotFoundException("Client with the id '" + id + "' not found.");
        }
        return result.get(0);
    }

    public boolean clientIdAlreadyExists(String id) {
        final List<ClientEntity> result = getClientResultList(id);
        return result.size() > 0;
    }

    private List<ClientEntity> getClientResultList(String id) {
        return em.createNamedQuery("getClientById", ClientEntity.class).setParameter("id", id).getResultList();
    }
}
