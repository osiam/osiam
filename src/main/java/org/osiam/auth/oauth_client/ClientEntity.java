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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Entity
@Table(name = "osiam_client")
public class ClientEntity implements ClientDetails {

    private static final int ID_LENGTH = 32;
    private static final int SEQUENCE_INITIAL_VALUE = 100;
    private static final int SEQUENCE_ALLOCATION_SIZE = 1;

    @Id
    @SequenceGenerator(name = "sequence_osiam_client",
            sequenceName = "auth_server_sequence_osiam_client",
            allocationSize = SEQUENCE_ALLOCATION_SIZE,
            initialValue = SEQUENCE_INITIAL_VALUE)
    @GeneratedValue(generator = "sequence_osiam_client")
    @JsonIgnore
    private long internalId;

    @JsonProperty
    @Column(unique = true, nullable = false, length = ID_LENGTH)
    private String id;

    @JsonProperty
    private int accessTokenValiditySeconds;

    @JsonProperty
    private int refreshTokenValiditySeconds;

    @JsonProperty
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(nullable = false)
    private String redirectUri;

    @JsonProperty("client_secret")
    @Column(nullable = false)
    private String clientSecret = UUID.randomUUID().toString();

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_scopes", joinColumns = @JoinColumn(name = "id"))
    private Set<String> scope;

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_grants", joinColumns = @JoinColumn(name = "id"))
    private Set<String> grants = Sets.newHashSet("authorization_code", "refresh_token");

    @JsonProperty
    @Column(name = "implicit_approval", nullable = false)
    private boolean implicit;

    @JsonProperty
    @Column(nullable = false)
    private long validityInSeconds;

    public long getInternalId() {
        return internalId;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    @JsonIgnore
    public boolean isAutoApprove(final String scope) {
        return implicit;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getAdditionalInformation() {
        return Collections.singletonMap("validityInSeconds", (Object) validityInSeconds);
    }

    @Override
    @JsonIgnore
    public String getClientId() {
        return id;
    }

    @Override
    @JsonIgnore
    public Set<String> getResourceIds() {
        return Collections.emptySet();
    }

    @Override
    @JsonIgnore
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    @JsonIgnore
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return Collections.unmodifiableSet(scope);
    }

    @Override
    @JsonIgnore
    public Set<String> getAuthorizedGrantTypes() {
        return Collections.unmodifiableSet(grants);
    }

    @Override
    @JsonIgnore
    public Set<String> getRegisteredRedirectUri() {
        return Collections.singleton(redirectUri);
    }

    @Override
    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.emptySet();
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public void setClientId(String clientId) {
        this.id = clientId;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setGrants(Collection<String> grants) {
        this.grants = new LinkedHashSet<>(grants);
    }

    public void setScope(Collection<String> scope) {
        this.scope = new LinkedHashSet<>(scope);
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public void setImplicit(boolean implicit) {
        this.implicit = implicit;
    }

    public void setValidityInSeconds(long validity) {
        this.validityInSeconds = validity;
    }
}
