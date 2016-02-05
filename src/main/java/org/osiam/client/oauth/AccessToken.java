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

package org.osiam.client.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import org.osiam.client.helper.ScopeDeserializer;
import org.osiam.client.helper.ScopeSerializer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Objects of this type represent an access token. Access tokens are granted by OSIAM and allows access to
 * restricted resources.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

    @JsonProperty("access_token")
    private String token;
    @JsonProperty("expires_at")
    private Date expiresAt;
    @JsonSerialize(using = ScopeSerializer.class)
    @JsonDeserialize(using = ScopeDeserializer.class)
    @JsonProperty("scope")
    private Set<Scope> scopes = new HashSet<>();
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("refresh_token_expires_at")
    private Date refreshTokenExpiresAt;
    @JsonProperty("token_type")
    private String type;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("user_name")
    private String userName;

    /**
     * Default constructor for Jackson
     */
    private AccessToken() {
    }

    private AccessToken(Builder builder) {
        token = builder.token;
        expiresAt = builder.expiresAt;
        scopes = builder.scopes;
        refreshToken = builder.refreshToken;
        refreshTokenExpiresAt = builder.refreshTokenExpiresAt;
        type = builder.type;
        clientId = builder.clientId;
        userId = builder.userId;
        userName = builder.userName;
    }

    /**
     * Retrieve the string value of the access token used to authenticate against the provider.
     *
     * @return The access token string
     */
    public String getToken() {
        return token;
    }

    /**
     * The Date when the access token is not valid anymore
     *
     * @return The Date when the access token is not valid anymore.
     */
    public Date getExpiresAt() {
        if (expiresAt != null) {
            return new Date(expiresAt.getTime());
        }
        return null;
    }

    /**
     * checks if the time the access token will be valid are over
     *
     * @return true if the access token is not valid anymore
     */
    public boolean isExpired() {
        return expiresAt.before(new Date());
    }

    /**
     * Retrieve the possible Scopes of this AccessToken
     *
     * @return The scopes
     */
    public Set<Scope> getScopes() {
        return scopes;
    }

    /**
     * Retrieve the refresh token for this access token
     *
     * @return The refresh token as String
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * The Date when the refresh access token is not valid anymore
     *
     * @return The Date when the refresh access token is not valid anymore.
     */
    public Date getRefreshTokenExpiresAt() {
        if (refreshTokenExpiresAt != null) {
            return new Date(refreshTokenExpiresAt.getTime());
        }
        return null;
    }

    /**
     * checks if the time the refresh access token will be valid are over
     *
     * @return true if the refresh access token is not valid anymore
     */
    public boolean isRefreshTokenExpired() {
        return refreshTokenExpiresAt.before(new Date());
    }

    /**
     * type of the access token
     *
     * @return the type of the access token
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @return the client Id where the AccessToken belongs to
     */
    public String getClientId() {
        return clientId;
    }

    /**
     *
     * @return the userId of the User the Access token belongs to. Will be empty if the AccessToken was created with an
     *         ClientCredentials Flow
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @return the userName of the User the Access token belongs to. Will be empty if the AccessToken was created with
     *         an ClientCredentials Flow
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @return if this access token is only refers to a client. Will be true if the AccessToken was created with
     *         an ClientCredentials Flow
     */
    public boolean isClientOnly() {
        return Strings.isNullOrEmpty(userName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (token == null ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccessToken other = (AccessToken) obj;
        if (token == null) {
            if (other.token != null) {
                return false;
            }
        } else if (!token.equals(other.token)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccessToken [token=" + token + ", expiresAt=" + expiresAt + ", scopes=" + scopes + ", refreshTokenId="
                + refreshToken + ", refreshTokenExpiresAt=" + refreshTokenExpiresAt + ", clientId=" + clientId
                + ", userId=" + userId + ", userName=" + userName + "]";
    }

    /**
     * The Builder class is used to construct instances of the {@link AccessToken}.
     */
    public static class Builder {

        private String token;
        private Date expiresAt = new Date(Long.MIN_VALUE);
        private Set<Scope> scopes = new HashSet<>();
        private String refreshToken = "";
        private Date refreshTokenExpiresAt = new Date(Long.MIN_VALUE);
        private String type = "";
        private String clientId = "";
        private String userId = "";
        private String userName = "";


        /**
         * Constructs a new builder with a token
         *
         * @param token
         *        The token string (See {@link AccessToken#getToken()})
         *
         * @throws IllegalArgumentException
         *         if the given token is null or empty
         */
        public Builder(String token) {
            this.token = token;
            if (Strings.isNullOrEmpty(token)) {
                throw new IllegalArgumentException("token must not be null or empty.");
            }
        }

        /**
         *
         * @param expireDate
         *        sets the expire Date of the AccessToken
         * @return the Builder itself
         */
        public Builder setExpiresAt(Date expireDate) {
            if (expireDate != null) {
                expiresAt = new Date(expireDate.getTime());
            }
            return this;
        }

        /**
         *
         * @param scope
         *        adds one scope of the AccessToken
         * @return the Builder itself
         */
        public Builder addScope(Scope scope) {
            scopes.add(scope);
            return this;
        }

        /**
         *
         * @param scopes
         *        adds an set of scopes to the AccessToken
         * @return the Builder itself
         */
        public Builder addScopes(Set<Scope> scopes) {
            for (Scope scope : scopes) {
                addScope(scope);
            }
            return this;
        }

        /**
         *
         * @param refreshToken
         *        the refresh token
         * @return the Builder itself
         */
        public Builder setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        /**
         *
         * @param expireDate
         *        sets the expire Date of the refresh token
         * @return the Builder itself
         */
        public Builder setRefreshTokenExpiresAt(Date expireDate) {
            if (expireDate != null) {
                refreshTokenExpiresAt = new Date(expireDate.getTime());
            }
            return this;
        }

        /**
         *
         * @param type
         *        the type of the access token
         * @return the Builder itself
         */
        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        /**
         *
         * @param clientId
         *        the client id of the client where the AccessToken belongs to
         * @return the Builder itself
         */
        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         *
         * @param userId
         *        the id of the user the AccessToken belongs to
         * @return the Builder itself
         */
        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        /**
         *
         * @param userName
         *        the userName of the user the AccessToken belongs to
         * @return the Builder itself
         */
        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        /**
         * Construct the {@link AccessToken} with the parameters passed to this builder.
         *
         * @return An AccessToken configured accordingly.
         */
        public AccessToken build() {
            return new AccessToken(this);
        }
    }
}
