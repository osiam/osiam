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

package org.osiam.auth.login.ldap

import org.osiam.resources.scim.Email
import org.osiam.resources.scim.Entitlement
import org.osiam.resources.scim.Im
import org.osiam.resources.scim.PhoneNumber
import org.osiam.resources.scim.Photo
import org.osiam.resources.scim.Role
import org.osiam.resources.scim.User
import org.osiam.resources.scim.X509Certificate
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations

import spock.lang.Specification

class OsiamLdapUserContextMapperSpec extends Specification {

    DirContextOperations userData = new DirContextAdapter()
    Map<String, String> scimLdapAttributes = new HashMap<String, String>()
    ScimToLdapAttributeMapping scimToLdapAttributeMapping = Mock()
    OsiamLdapUserContextMapper osiamLdapUserContextMapper = new OsiamLdapUserContextMapper(scimToLdapAttributeMapping)

    String type = 'ldap'
    String userNameValue = 'userNameValue'
    String nameFormattedValue = 'formattedValue'
    String nameFamilyNameValue = 'familyNameValue'
    String nameGivenNameValue = 'givenNameValue'
    String nameMiddleNameValue = 'givenMiddleValue'
    String nameHonorificPrefixValue = 'honorificPrefixValue'
    String nameHonorificSuffixValue = 'honorificSuffixValue'
    String displayNameValue = 'displayNameValue'
    String nickNameValue = 'nickNameValue'
    String titleValue = 'titleValue'
    String userTypeValue = 'userTypeValue'
    String preferredLanguageValue = 'preferredLanguage'
    String localeValue = 'locale'
    String timeZoneValue = 'timeZone'
    String emailValue = 'mail@mail.de'
    String phoneNumberValue = '123456789'
    String imValue = 'imValue'
    String photoValue = 'photoUrlValue'
    String addressFormattedValue = 'addressFormattedValue'
    String addressStreetValue = 'addressStreetValue'
    String addressLocalityValue = 'addressLocalityValue'
    String addressRegionValue = 'addressRegionValue'
    String addressPostalCodeValue = 'addressPostalCodeValue'
    String addressCountryValue = 'addressCountryValue'
    String entitlementValue = 'entitlementValue'
    String roleValue = 'roleValue'
    String x509CertificateValue = 'x509CertificateValue'

    def setup() {
        scimLdapAttributes.put('userName', 'uid')
        userData.setAttributeValue('uid', userNameValue)
        scimLdapAttributes.put('name.familyName', 'sn')
        scimLdapAttributes.put('name.givenName', 'givenName')
        scimLdapAttributes.put('name.formatted', 'formatted')
        scimLdapAttributes.put('name.middleName', 'middleName')
        scimLdapAttributes.put('name.honorificPrefix', 'honorificPrefix')
        scimLdapAttributes.put('name.honorificSuffix', 'honorificSuffix')
        userData.setAttributeValue('sn', nameFamilyNameValue)
        userData.setAttributeValue('formatted', nameFormattedValue)
        userData.setAttributeValue('givenName', nameGivenNameValue)
        userData.setAttributeValue('middleName', nameMiddleNameValue)
        userData.setAttributeValue('honorificPrefix', nameHonorificPrefixValue)
        userData.setAttributeValue('honorificSuffix', nameHonorificSuffixValue)
        scimLdapAttributes.put('displayName', 'displayName')
        userData.setAttributeValue('displayName', displayNameValue)
        scimLdapAttributes.put('nickName', 'nickName')
        userData.setAttributeValue('nickName', nickNameValue)
        scimLdapAttributes.put('title', 'title')
        userData.setAttributeValue('title', titleValue)
        scimLdapAttributes.put('userType', 'userType')
        userData.setAttributeValue('userType', userTypeValue)
        scimLdapAttributes.put('preferredLanguage', 'preferredLanguage')
        userData.setAttributeValue('preferredLanguage', preferredLanguageValue)
        scimLdapAttributes.put('locale', 'location')
        userData.setAttributeValue('location', localeValue)
        scimLdapAttributes.put('timezone', 'timeZone')
        userData.setAttributeValue('timeZone', timeZoneValue)
        scimLdapAttributes.put('email', 'mail')
        userData.setAttributeValue('mail', emailValue)
        scimLdapAttributes.put('phoneNumber', 'telephoneNumber')
        userData.setAttributeValue('telephoneNumber', phoneNumberValue)
        scimLdapAttributes.put('im', 'im')
        userData.setAttributeValue('im', imValue)
        scimLdapAttributes.put('photo', 'picture')
        userData.setAttributeValue('picture', photoValue)
        scimLdapAttributes.put('address.formatted', 'addressFormatted')
        scimLdapAttributes.put('address.streetAddress', 'streetAddress')
        scimLdapAttributes.put('address.locality', 'l')
        scimLdapAttributes.put('address.region', 'st')
        scimLdapAttributes.put('address.postalCode', 'postalCode')
        scimLdapAttributes.put('address.country', 'countryCode')
        userData.setAttributeValue('addressFormatted', addressFormattedValue)
        userData.setAttributeValue('streetAddress', addressStreetValue)
        userData.setAttributeValue('l', addressLocalityValue)
        userData.setAttributeValue('st', addressRegionValue)
        userData.setAttributeValue('postalCode', addressPostalCodeValue)
        userData.setAttributeValue('countryCode', addressCountryValue)
        scimLdapAttributes.put('entitlement', 'entitlement')
        userData.setAttributeValue('entitlement', entitlementValue)
        scimLdapAttributes.put('role', 'role')
        userData.setAttributeValue('role', roleValue)
        scimLdapAttributes.put('x509Certificate', 'x509Certificate')
        userData.setAttributeValue('x509Certificate', x509CertificateValue)

        scimToLdapAttributeMapping.toLdapAttribute(_) >> { String scim -> scimLdapAttributes.get(scim) }
        scimToLdapAttributeMapping.scimAttributes() >> scimLdapAttributes.keySet()
    }

    def 'all user data from LDAP will be mapped to SCIM user'() {
        when:
        User user = osiamLdapUserContextMapper.mapUser(userData)

        then:
        user.getUserName() == userNameValue
        user.getPassword().length() > 0
        user.isActive()
        userDataIsCorrectlyMapped(user)
    }

    def 'all user data from LDAP will be mapped to SCIM update user'() {
        given:
        User user = new User.Builder('userNameValue').build()

        when:
        user = osiamLdapUserContextMapper.mapUpdateUser(user, userData).scimConformUpdateUser

        then:
        userDataIsCorrectlyMapped(user)
    }

    void userDataIsCorrectlyMapped(User user) {
        assert user.getName().getFormatted() == nameFormattedValue
        assert user.getName().getFamilyName() == nameFamilyNameValue
        assert user.getName().getGivenName() == nameGivenNameValue
        assert user.getName().getMiddleName() == nameMiddleNameValue
        assert user.getName().getHonorificPrefix() == nameHonorificPrefixValue
        assert user.getName().getHonorificSuffix() == nameHonorificSuffixValue
        assert user.getDisplayName() == displayNameValue
        assert user.getNickName() == nickNameValue
        assert user.getTitle() == titleValue
        assert user.getPreferredLanguage() == preferredLanguageValue
        assert user.getLocale() == localeValue
        assert user.getTimezone() == timeZoneValue
        Email email = user.getEmails().iterator().next()
        assert email.getValue() == emailValue
        assert email.getType().toString() == type
        PhoneNumber phoneNumber = user.getPhoneNumbers().iterator().next()
        assert phoneNumber.getValue() == phoneNumberValue
        assert phoneNumber.getType().toString() == type
        Im im = user.getIms().iterator().next()
        assert im.getValue() == imValue
        assert im.getType().toString() == type
        Photo photo = user.getPhotos().iterator().next()
        assert photo.getValue() == photoValue
        assert photo.getType().toString() == type
        assert user.getAddresses().get(0).getFormatted() == addressFormattedValue
        assert user.getAddresses().get(0).getStreetAddress() == addressStreetValue
        assert user.getAddresses().get(0).getLocality() == addressLocalityValue
        assert user.getAddresses().get(0).getRegion() == addressRegionValue
        assert user.getAddresses().get(0).getPostalCode() == addressPostalCodeValue
        assert user.getAddresses().get(0).getCountry() == addressCountryValue
        assert user.getAddresses().get(0).getType().toString() == type
        Entitlement entitlements = user.getEntitlements().iterator().next()
        assert entitlements.getValue() == entitlementValue
        assert entitlements.getType().toString() == type
        Role roles = user.getRoles().iterator().next()
        assert roles.getValue() == roleValue
        assert roles.getType().toString() == type
        X509Certificate x509Certificates = user.getX509Certificates().iterator().next()
        assert x509Certificates.getValue() == x509CertificateValue
        assert x509Certificates.getType().toString() == type
    }
}
