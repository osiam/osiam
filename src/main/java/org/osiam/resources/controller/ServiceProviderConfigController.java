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
package org.osiam.resources.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.osiam.resources.scim.SCIMSearchResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "/ServiceProviderConfig")
public class ServiceProviderConfigController {

    @RequestMapping
    public ServiceProviderConfig getConfig() {
        return ServiceProviderConfig.INSTANCE;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static final class ServiceProviderConfig {

        public static final ServiceProviderConfig INSTANCE = new ServiceProviderConfig();
        public static final String SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig";
        public final Supported patch = new Supported(false);
        public final Supported bulk = new BulkSupported(false);
        public final Supported filter = new FilterSupported(true, SCIMSearchResult.MAX_RESULTS);
        public final Supported changePassword = new Supported(false);
        public final Supported sort = new Supported(true);
        public final Supported etag = new Supported(false);
        public final Supported xmlDataFormat = new Supported(false);
        public final AuthenticationSchemes authenticationSchemes = new AuthenticationSchemes(
                new AuthenticationSchemes.AuthenticationScheme("Oauth2 Bearer",
                        "OAuth2 Bearer access token is used for authorization.", "http://tools.ietf.org/html/rfc6749",
                        "http://oauth.net/2/"));
        public Set<String> schemas = new HashSet<>();

        private ServiceProviderConfig() {
            schemas.add(SCHEMA);
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Schemas {
            public final Set<String> schemas = new HashSet<>();

            public Schemas(String coreSchema) {
                schemas.add(coreSchema);
            }
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Supported {
            public final boolean supported;

            public Supported(boolean b) {
                supported = b;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class FilterSupported extends Supported {
            public final Integer maxResults;

            public FilterSupported(boolean b, Integer maxResults) {
                super(b);
                this.maxResults = maxResults;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class BulkSupported extends Supported {
            public final Integer maxOperations;
            public final Integer maxPayloadSize;

            public BulkSupported(boolean b) {
                super(b);
                this.maxOperations = null;
                this.maxPayloadSize = null;
            }

        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        static class AuthenticationSchemes {
            public AuthenticationScheme[] authenticationSchemes;

            public AuthenticationSchemes(AuthenticationScheme... authenticationScheme) {
                this.authenticationSchemes = authenticationScheme;

            }

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            public static class AuthenticationScheme {
                public final String name;
                public final String description;
                public final String specUrl;
                public final String documentationUrl;

                AuthenticationScheme(String name, String description, String specUrl, String documentationUrl) {
                    this.name = name;
                    this.description = description;
                    this.specUrl = specUrl;
                    this.documentationUrl = documentationUrl;
                }
            }
        }
    }
}
