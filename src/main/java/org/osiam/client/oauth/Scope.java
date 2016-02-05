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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.osiam.resources.scim.User;

/**
 * Scope represents an OAuth 2.0 scope.
 * <p>
 * Scopes defined by OSIAM are available as static constants of this class, e.g. {@link Scope#ME}.
 * </p>
 */
public class Scope {

    /**
     * @deprecated Use {@link Scope#ME} or {@link Scope#ADMIN}. This field will be removed in version 1.10 or 2.0.
     */
    @Deprecated
    public static final Scope GET = new Scope("GET");
    /**
     * @deprecated Use {@link Scope#ME} or {@link Scope#ADMIN}. This field will be removed in version 1.10 or 2.0.
     */
    @Deprecated
    public static final Scope POST = new Scope("POST");
    /**
     * @deprecated Use {@link Scope#ME} or {@link Scope#ADMIN}. This field will be removed in version 1.10 or 2.0.
     */
    @Deprecated
    public static final Scope PUT = new Scope("PUT");
    /**
     * @deprecated Use {@link Scope#ME} or {@link Scope#ADMIN}. This field will be removed in version 1.10 or 2.0.
     */
    @Deprecated
    public static final Scope PATCH = new Scope("PATCH");
    /**
     * @deprecated Use {@link Scope#ME} or {@link Scope#ADMIN}. This field will be removed in version 1.10 or 2.0.
     */
    @Deprecated
    public static final Scope DELETE = new Scope("DELETE");
    /**
     * @deprecated Use {@link Scope#ME} or {@link Scope#ADMIN}. This field will be removed in version 1.10 or 2.0.
     */
    @Deprecated
    public static final Scope ALL = new Scope(Scope.GET + " " + Scope.POST + " " + Scope.PUT + " "
            + Scope.PATCH + " " + Scope.DELETE);

    /**
     * {@code ME} is a scope that allows read and write access to the data of the user associated with the access token.
     * <p>
     * This includes:
     * <ul>
     * <li>Retrieving the complete {@link User} resource</li>
     * <li>Modifying all attributes of the {@link User} resource</li>
     * <li>Deleting the {@link User} resource</li>
     * <li>Revoking the access tokens of the {@link User}</li>
     * <li>Access the {@code /me} resource</li>
     * <li>Validate the access token</li>
     * </ul>
     * Note that this only works with a user-bound access token, i.e. a token with scope {@code ME} that has been
     * retrieved via client credentials grant <strong>CANNOT</strong> access any user's data.
     * </p>
     */
    public static final Scope ME = new Scope("ME");

    /**
     * {@code ADMIN} is a scope that allows full access to any resource.
     */
    public static final Scope ADMIN = new Scope("ADMIN");

    private String value;

    public Scope(String value) {
        this.value = value;
    }

    @JsonProperty
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Scope other = (Scope) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
