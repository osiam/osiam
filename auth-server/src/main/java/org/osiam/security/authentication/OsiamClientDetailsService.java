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

package org.osiam.security.authentication;

import javax.inject.Inject;

import org.osiam.auth.oauth_client.ClientDao;
import org.osiam.auth.oauth_client.ClientEntity;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

/**
 * OSIAM {@link ClientDetailsService} implementation.
 */
@Service("osiamClientDetailsService")
public class OsiamClientDetailsService implements ClientDetailsService {

    @Inject
    private ClientDao clientDao;

    @Override
    public OsiamClientDetails loadClientByClientId(final String clientId) {
        return toOsiamClientDetails(clientDao.getClient(clientId));
    }

    private OsiamClientDetails toOsiamClientDetails(final ClientEntity client) {
        final OsiamClientDetails clientDetails = new OsiamClientDetails();

        clientDetails.setId(client.getId());
        clientDetails.setClientSecret(client.getClientSecret());
        clientDetails.setScope(client.getScope());
        clientDetails.setGrants(client.getGrants());
        clientDetails.setRedirectUri(client.getRedirectUri());
        clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        clientDetails.setImplicit(client.isImplicit());
        clientDetails.setValidityInSeconds(client.getValidityInSeconds());

        return clientDetails;
    }
}