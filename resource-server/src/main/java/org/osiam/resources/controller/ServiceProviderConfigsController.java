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

import java.util.HashSet;
import java.util.Set;

import org.osiam.resources.scim.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Controller
@RequestMapping(value = "/ServiceProviderConfigs")
@Transactional
public class ServiceProviderConfigsController {
    @RequestMapping
    @ResponseBody
    public ServiceProviderConfig getConfig() {
        return ServiceProviderConfig.INSTANCE;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    public static final class ServiceProviderConfig {

        public static final ServiceProviderConfig INSTANCE = new ServiceProviderConfig(); // NOSONAR - Needed public due to json serializing

        public Set<String> schemas = new HashSet<>(); // NOSONAR - Needed public due to json serializing
        public final Supported patch = new Supported(true); // NOSONAR - Needed public due to json serializing
        public final Supported bulk = new BulkSupported(false); // NOSONAR - Needed public due to json serializing
        public final Supported filter = new FilterSupported(true, Constants.MAX_RESULT); // NOSONAR - Needed public due to json serializing
        public final Supported changePassword = new Supported(false); // NOSONAR - Needed public due to json serializing
        public final Supported sort = new Supported(true); // NOSONAR - Needed public due to json serializing
        public final Supported etag = new Supported(false); // NOSONAR - Needed public due to json serializing
        public final Supported xmlDataFormat = new Supported(false); // NOSONAR - Needed public due to json serializing
        public final AuthenticationSchemes authenticationSchemes = new AuthenticationSchemes( // NOSONAR - Needed public due to json serializing
                new AuthenticationSchemes.AuthenticationScheme("Oauth2 Bearer", // NOSONAR - Field is readable after serializing
                        "OAuth2 Bearer access token is used for authorization.", "http://tools.ietf.org/html/rfc6749",
                        "http://oauth.net/2/"));

        private ServiceProviderConfig() {
            schemas.add(Constants.SERVICE_PROVIDER_CORE_SCHEMA);
        }

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class Schemas {
            public final Set<String> schemas = new HashSet<>(); // NOSONAR - Needed public due to json serializing

            public Schemas(String coreSchema) {
                schemas.add(coreSchema); // NOSONAR - Field is readable after serializing
            }
        }

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class Supported {
            public final boolean supported; // NOSONAR - Needed public due to json serializing

            public Supported(boolean b) {
                supported = b; // NOSONAR - Field is readable after serializing
            }
        }

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class FilterSupported extends Supported {
            public final Integer maxResults; // NOSONAR - Needed public due to json serializing

            public FilterSupported(boolean b, Integer maxresults) {
                super(b);
                this.maxResults = maxresults; // NOSONAR - Field is readable after serializing
            }
        }


        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class BulkSupported extends Supported {
            public final Integer maxOperations; // NOSONAR - Needed public due to json serializing
            public final Integer maxPayloadSize; // NOSONAR - Needed public due to json serializing

            public BulkSupported(boolean b) {
                super(b);
                this.maxOperations = null; // NOSONAR - Field is readable after serializing
                this.maxPayloadSize = null; // NOSONAR - Field is readable after serializing
            }

        }

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        static class AuthenticationSchemes {
            public AuthenticationScheme[] authenticationSchemes; // NOSONAR - Needed public due to json serializing

            public AuthenticationSchemes(AuthenticationScheme... authenticationScheme) {
                this.authenticationSchemes = authenticationScheme; // NOSONAR - Field is readable after serializing

            }

            @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
            public static class AuthenticationScheme {
                public final String name; // NOSONAR - Needed public due to json serializing
                public final String description; // NOSONAR - Needed public due to json serializing
                public final String specUrl; // NOSONAR - Needed public due to json serializing
                public final String documentationUrl; // NOSONAR - Needed public due to json serializing

                AuthenticationScheme(String name, String description, String specUrl, String documentationUrl) {
                    this.name = name; // NOSONAR - Field is readable after serializing
                    this.description = description; // NOSONAR - Field is readable after serializing
                    this.specUrl = specUrl; // NOSONAR - Field is readable after serializing
                    this.documentationUrl = documentationUrl; // NOSONAR - Field is readable after serializing
                }
            }
        }
    }
}
