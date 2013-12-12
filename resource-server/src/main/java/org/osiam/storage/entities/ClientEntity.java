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

package org.osiam.storage.entities;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Entity
@Table(name = "osiam_client")
@NamedQueries({ @NamedQuery(name = "getClientById", query = "SELECT i FROM ClientEntity i WHERE i.id= :id") })
public class ClientEntity {

    private static final int LENGTH = 32;

    @Id
    @GeneratedValue
    @JsonIgnore
    @Column(name = "internal_id")
    private long internalId;

    @JsonProperty
    @Column(unique = true, nullable = false, length = LENGTH)
    private String id;
    @JsonProperty
    private int accessTokenValiditySeconds;
    @JsonProperty
    private int refreshTokenValiditySeconds;
    @JsonProperty
    @Column(name = "redirect_uri", unique = true, nullable = false)
    private String redirectUri;

    @JsonProperty("client_secret")
    @Column(name = "client_secret", unique = true, nullable = false)
    private String clientSecret = generateSecret();

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_scopes", joinColumns = @JoinColumn(name = "id"))
    @Column
    private Set<String> scope;

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_grants", joinColumns = @JoinColumn(name = "id"))
    @Column
    private Set<String> grants = generateGrants();

    @JsonProperty
    @Column(name = "implicit_approval", nullable = false)
    private boolean implicit;

    @JsonProperty
    @Column(nullable = false)
    private long validityInSeconds;

    @JsonProperty
    @Column
    private Date expiry;

    public ClientEntity() {
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

    /* Used to Map Json to ClientEntity, because some Fields are generated. */
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

    private Set<String> generateGrants() {
        Set<String> result = new HashSet<>();
        Collections.addAll(result, "authorization_code", "refresh-token");
        return result;
    }

    private String generateSecret() {
        // TODO must be improved
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

    public Date getExpiry() {
        return expiry != null ? (Date) expiry.clone() : null;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry != null ? (Date) expiry.clone() : null;
    }
}