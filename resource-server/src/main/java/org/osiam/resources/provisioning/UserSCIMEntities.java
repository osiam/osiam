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

package org.osiam.resources.provisioning;

import org.osiam.storage.entities.*;

import java.util.HashMap;
import java.util.Map;

public final class UserSCIMEntities implements SCIMEntities {
    public static final UserSCIMEntities ENTITIES = new UserSCIMEntities();
    private Map<String, Entity> fromString = new HashMap<>();

    private UserSCIMEntities() {
        putEntity("emails", EmailEntity.class, true);
        putEntity("ims", ImEntity.class, true);
        putEntity("phonenumbers", PhoneNumberEntity.class, true);
        putEntity("photos", PhotoEntity.class, true);
        putEntity("entitlements", EntitlementsEntity.class, true);
        putEntity("roles", RolesEntity.class, true);
        putEntity("x509certificates", X509CertificateEntity.class, true);
        putEntity("addresses", AddressEntity.class, false);
    }

    private void putEntity(String key, Class<?> clazz, boolean multiValue) {
        fromString.put(key, new Entity(clazz, multiValue));
    }

    @Override
    public Entity fromString(String key) {
        return fromString.get(key);
    }
}
