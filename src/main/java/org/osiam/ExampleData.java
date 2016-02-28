/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam;

import com.google.common.collect.Lists;
import org.osiam.auth.oauth_client.ClientEntity;
import org.osiam.auth.oauth_client.ClientRepository;
import org.osiam.resources.provisioning.SCIMUserProvisioning;
import org.osiam.resources.scim.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ExampleData {

    private static final Logger logger = LoggerFactory.getLogger(ExampleData.class);

    private final ClientRepository clientRepository;
    private final SCIMUserProvisioning userProvisioning;

    @Autowired
    public ExampleData(ClientRepository clientRepository, SCIMUserProvisioning userProvisioning) {
        this.clientRepository = clientRepository;
        this.userProvisioning = userProvisioning;
    }

    @PostConstruct
    public void create() {
        if (clientRepository.count() == 0) {
            ClientEntity exampleClient = new ClientEntity();
            exampleClient.setAccessTokenValiditySeconds(28800);
            exampleClient.setClientSecret("secret");
            exampleClient.setClientId("example-client");
            exampleClient.setImplicit(false);
            exampleClient.setRedirectUri("http://localhost:5000/oauth2");
            exampleClient.setRefreshTokenValiditySeconds(86400);
            exampleClient.setValidityInSeconds(28800);
            exampleClient.setScope(Lists.newArrayList("ADMIN", "ME"));
            exampleClient.setGrants(Lists.newArrayList(
                    "authorization_code", "refresh_token", "password", "client_credentials"
            ));
            clientRepository.saveAndFlush(exampleClient);
            logger.info("Created OAuth client 'example-client' with secret 'secret'");
        }
        if (userProvisioning.count() == 0) {
            User adminUser = userProvisioning.create(
                    new User.Builder("admin")
                            .setActive(true)
                            .setPassword("koala")
                            .build()
            );
            logger.info("Created admin user 'admin' with password 'koala' and id '{}'", adminUser.getId());
        }
    }
}
