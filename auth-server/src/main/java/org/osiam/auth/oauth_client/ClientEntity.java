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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Entity
@Table(name = "osiam_client")
@NamedQueries({ @NamedQuery(name = "getClientById", query = "SELECT i FROM ClientEntity i WHERE i.id= :id") })
public class ClientEntity {

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
    @Column(name = "internal_id")
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
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    @JsonProperty("client_secret")
    @Column(name = "client_secret", nullable = false)
    private String clientSecret = generateSecret();

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_scopes", joinColumns = @JoinColumn(name = "id"))
    private Set<String> scope;

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_grants", joinColumns = @JoinColumn(name = "id"))
    private Set<String> grants = generateGrants();

    @JsonProperty
    @Column(name = "implicit_approval", nullable = false)
    private boolean implicit;

    @JsonProperty
    @Column(nullable = false)
    private long validityInSeconds;

    public ClientEntity() {
    }

    /**
     * Used to Map Json to ClientEntity, because some Fields are generated.
     *
     */
    public ClientEntity(ClientEntity entity) {
        if (entity.getId() != null) {
            id = entity.getId();
        }

        if (entity.getClientSecret() != null) {
            clientSecret = entity.getClientSecret();

        }

        accessTokenValiditySeconds = entity.getAccessTokenValiditySeconds();
        refreshTokenValiditySeconds = entity.getRefreshTokenValiditySeconds();
        redirectUri = entity.getRedirectUri();
        scope = entity.getScope();
        implicit = entity.isImplicit();
        validityInSeconds = entity.getValidityInSeconds();
        grants = !entity.getGrants().isEmpty() ? entity.getGrants() : generateGrants();
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public int getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Set<String> getScope() {
        return scope;
    }

    public Set<String> getGrants() {
        return grants;
    }

    private Set<String> generateGrants() {
        Set<String> result = new HashSet<>();
        Collections.addAll(result, "authorization_code", "refresh-token");
        return result;
    }

    private String generateSecret() {
        return UUID.randomUUID().toString();
    }

    public void setGrants(Set<String> grants) {
        this.grants = grants;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public boolean isImplicit() {
        return implicit;
    }

    public void setImplicit(boolean implicit) {
        this.implicit = implicit;
    }

    public long getValidityInSeconds() {
        return validityInSeconds;
    }

    public void setValidityInSeconds(long validity) {
        this.validityInSeconds = validity;
    }
}
