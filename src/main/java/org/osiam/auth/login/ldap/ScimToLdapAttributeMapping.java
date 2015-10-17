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

package org.osiam.auth.login.ldap;

import org.osiam.auth.exception.LdapConfigurationException;
import org.osiam.resources.scim.User;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScimToLdapAttributeMapping {

    private final Map<String, String> scimToLdapAttributes = new HashMap<>();

    public ScimToLdapAttributeMapping(final String[] attributeMapping) {
        for (String keyValuePair : attributeMapping) {
            if (!keyValuePair.contains(":")) {
                throw new LdapConfigurationException("The ldap attribute mapping value '" + keyValuePair
                        + "' could not be parsed. It doesn't contain a ':'");
            }
            String[] keyValue = keyValuePair.split(":");
            if (keyValue.length != 2) {
                throw new LdapConfigurationException("The ldap attribute mapping value '" + keyValuePair
                        + "' could not be parsed. It contains more than one ':'");
            }
            scimToLdapAttributes.put(keyValue[0].trim(), keyValue[1].trim());
        }

        if (!scimToLdapAttributes.containsKey("userName")) {
            scimToLdapAttributes.put("userName", "uid");
        }

        checkMapping();
    }

    public Collection<String> ldapAttributes() {
        return scimToLdapAttributes.values();
    }

    public Collection<String> scimAttributes() {
        return scimToLdapAttributes.keySet();
    }

    public String toLdapAttribute(final String scimAttribute) {
        return scimToLdapAttributes.get(scimAttribute);
    }

    private void checkMapping() {
        DirContextOperations ldapUserData = new DirContextAdapter();
        for (String scimAttribute : scimToLdapAttributes.keySet()) {
            if (scimAttribute.equalsIgnoreCase("password")) {
                throw new LdapConfigurationException(
                        "The password can not be mapped to the SCIM user. Please delete the password mapping from"
                                + "the configuration!"
                );
            }
            ldapUserData.setAttributeValue(scimToLdapAttributes.get(scimAttribute), "test@test.de");
        }
        OsiamLdapUserContextMapper contextMapper = new OsiamLdapUserContextMapper(this);
        User user = contextMapper.mapUser(ldapUserData);
        contextMapper.mapUpdateUser(user, ldapUserData);
    }
}
