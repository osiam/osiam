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
import org.osiam.resources.scim.UpdateUser
import org.osiam.resources.scim.User
import org.osiam.resources.scim.X509Certificate
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations

import spock.lang.Specification

class OsiamLdapUserContextMapperSpec extends Specification {

    DirContextOperations userData = new DirContextAdapter()
    Map<String, String> scimLdapAttributes = new HashMap<String, String>()
    OsiamLdapUserContextMapper osiamLdapUserContextMapper

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
        userData.setAttributeValue('userType', userNameValue)

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
    }
    
    def 'All user data from ldap will be mapped to scim user'() {
        given:
        osiamLdapUserContextMapper = new OsiamLdapUserContextMapper(scimLdapAttributes)

        when:
        User user = osiamLdapUserContextMapper.mapUser(userData)

        then:
        conformUserData(user)
    }
    
    def 'All user data from ldap will be mapped to scim update user'() {
        given:
        User user = new User.Builder().build()
        osiamLdapUserContextMapper = new OsiamLdapUserContextMapper(scimLdapAttributes)

        when:
        user = osiamLdapUserContextMapper.mapUpdateUser(user, userData).scimConformUpdateUser

        then:
        conformUserData(user)
    }
    
    def conformUserData(User user) {
        user.getUserName() == userNameValue
        user.getName().getFormatted() == nameFormattedValue
        user.getName().getFamilyName() == nameFamilyNameValue
        user.getName().getGivenName() == nameGivenNameValue
        user.getName().getMiddleName() == nameMiddleNameValue
        user.getName().getHonorificPrefix() == nameHonorificPrefixValue
        user.getName().getHonorificSuffix() == nameHonorificSuffixValue
        user.getDisplayName() == displayNameValue
        user.getNickName() == nickNameValue
        user.getTitle() == titleValue
        user.getPreferredLanguage() == preferredLanguageValue
        user.getLocale() == localeValue
        user.getTimezone() == timeZoneValue
        user.isActive() == true
        user.getPassword().length() > 0
        Email email = user.getEmails().iterator().next()
        email.getValue() == emailValue
        email.getType().toString() == type
        PhoneNumber phoneNumber = user.getPhoneNumbers().iterator().next()
        phoneNumber.getValue() == phoneNumberValue
        phoneNumber.getType().toString() == type
        Im im = user.getIms().iterator().next()
        im.getValue() == imValue
        im.getType().toString() == type
        Photo photo = user.getPhotos().iterator().next()
        photo.getValue() == photoValue
        photo.getType().toString() == type
        user.getAddresses().get(0).getFormatted() == addressFormattedValue
        user.getAddresses().get(0).getStreetAddress() == addressStreetValue
        user.getAddresses().get(0).getLocality() == addressLocalityValue
        user.getAddresses().get(0).getRegion() == addressRegionValue
        user.getAddresses().get(0).getPostalCode() == addressPostalCodeValue
        user.getAddresses().get(0).getCountry() == addressCountryValue
        user.getAddresses().get(0).getType().toString() == type
        Entitlement entitlements = user.getEntitlements().iterator().next()
        entitlements.getValue() == entitlementValue
        entitlements.getType().toString() == type
        Role roles = user.getRoles().iterator().next()
        roles.getValue() == roleValue
        roles.getType().toString() == type
        X509Certificate x509Certificates = user.getX509Certificates().iterator().next()
        x509Certificates.getValue() == x509CertificateValue
        x509Certificates.getType().toString() == type
    }
}
