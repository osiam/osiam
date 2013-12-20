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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.collect.ImmutableSet;

/**
 * User Entity
 */
@Entity
@Table(name = "scim_user")
public class UserEntity extends ResourceEntity {

    private static final String JOIN_COLUMN_NAME = "user_internal_id";

    @Column(nullable = false, unique = true)
    private String userName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private NameEntity name;

    @Column
    private String nickName;

    @Column
    private String profileUrl;

    @Column
    private String title;

    @Column
    private String userType;

    @Column
    private String preferredLanguage;

    @Column
    private String locale;

    @Column
    private String timezone;

    @Column
    private Boolean active = Boolean.FALSE;

    @Column(nullable = false)
    private String password;

    @Column
    private String displayName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<EmailEntity> emails = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<PhoneNumberEntity> phoneNumbers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<ImEntity> ims = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<PhotoEntity> photos = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<AddressEntity> addresses = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<EntitlementsEntity> entitlements = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<RolesEntity> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<X509CertificateEntity> x509Certificates = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = JOIN_COLUMN_NAME, nullable=false)
    private Set<ExtensionFieldValueEntity> extensionFieldValues = new HashSet<>();

    public UserEntity() {
        getMeta().setResourceType("User");
    }

    /**
     * @return the name entity
     */
    public NameEntity getName() {
        return name;
    }

    /**
     * @param name
     *            the name entity
     */
    public void setName(NameEntity name) {
        this.name = name;
    }

    /**
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName
     *            the nick name
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return the profile url
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * @param profileUrl
     *            the profile url
     */
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the user type
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType
     *            the user type
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * @return the preferred languages
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * @param preferredLanguage
     *            the preferred languages
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    /**
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * @param locale
     *            the locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * @return the timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * @param timezone
     *            the timezone
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * @return the active status
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active
     *            the active status
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns an immutable view of the list of emails
     *
     * @return the emails entity
     */
    public Set<EmailEntity> getEmails() {
        return ImmutableSet.copyOf(emails);
    }

    /**
     * Adds a new email to this user
     *
     * @param email
     *            the email to add
     */
    public void addEmail(EmailEntity email) {
        emails.add(email);
    }

    /**
     * Removes the given email from this user
     *
     * @param email
     *            the email to remove
     */
    public void removeEmail(EmailEntity email) {
        emails.remove(email);
    }

    /**
     * Removes all email's from this user
     */
    public void removeAllEmails(){
        emails.clear();
    }

    /**
     * @param emails
     *            the emails entity
     * @deprecated
     */
    @Deprecated
    public void setEmails(Set<EmailEntity> emails) {
        this.emails = emails;
    }

    /**
     * @return the extensions data of the user
     */
    public Set<ExtensionFieldValueEntity> getUserExtensions() {
        if (extensionFieldValues == null) {
            extensionFieldValues = new HashSet<>();
        }
        return extensionFieldValues;
    }

    /**
     * @param userExtensions
     *            the extension data of the user
     */
    public void setUserExtensions(Set<ExtensionFieldValueEntity> userExtensions) {
        this.extensionFieldValues = userExtensions;
    }

    /**
     * @return the phone numbers entity
     */
    public Set<PhoneNumberEntity> getPhoneNumbers() {
        return phoneNumbers;
    }

    /**
     * Adds a new phoneNumber to this user
     *
     * @param phoneNumber
     *            the phoneNumnber to add
     */
    public void addPhoneNumber(PhoneNumberEntity phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }

    /**
     * Removes the given phoneNumber from this user
     *
     * @param phoneNumber
     *            the phoneNumber to remove
     */
    public void removePhoneNumber(PhoneNumberEntity phoneNumber) {
        phoneNumbers.remove(phoneNumber);
    }

    /**
     * Removes all phoneNumber's from this user
     */
    public void removeAllPhoneNumbers(){
        phoneNumbers.clear();
    }

    /**
     * @param phoneNumbers
     *            the phone numbers entity
     * @deprecated
     */
    @Deprecated
    public void setPhoneNumbers(Set<PhoneNumberEntity> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    /**
     * @return the instant messaging entity
     */
    public Set<ImEntity> getIms() {
        return ims;
    }

    /**
     * @param ims
     *            the instant messaging entity
     * @deprecated
     */
    @Deprecated
    public void setIms(Set<ImEntity> ims) {
        this.ims = ims;
    }

    /**
     * Adds a new im to this user
     *
     * @param im
     *            the im to add
     */
    public void addIm(ImEntity im) {
        ims.add(im);
    }

    /**
     * Removes the given im from this user
     *
     * @param im
     *            the im to remove
     */
    public void removeIm(ImEntity im) {
        ims.remove(im);
    }

    /**
     * Removes all im's from this user
     */
    public void removeAllIms(){
        ims.clear();
    }

    /**
     * @return the photos entity
     */
    public Set<PhotoEntity> getPhotos() {
        return photos;
    }

    /**
     * @param photos
     *            the photos entity
     * @deprecated
     */
    @Deprecated
    public void setPhotos(Set<PhotoEntity> photos) {
        this.photos = photos;
    }

    /**
     * Adds a new photo to this user
     *
     * @param photo
     *            the photo to add
     */
    public void addPhoto(PhotoEntity photo) {
        photos.add(photo);
    }

    /**
     * Removes the given photo from this user
     *
     * @param photo
     *            the photo to remove
     */
    public void removePhoto(PhotoEntity photo) {
        photos.remove(photo);
    }

    /**
     * Removes all photo's from this user
     */
    public void removeAllPhotos(){
        photos.clear();
    }

    /**
     * @return the addresses entity
     */
    public Set<AddressEntity> getAddresses() {
        return addresses;
    }

    /**
     * @param addresses
     *            the addresses entity
     * @deprecated
     */
    @Deprecated
    public void setAddresses(Set<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    /**
     * Adds a new address to this user
     *
     * @param address
     *            the address to add
     */
    public void addAddress(AddressEntity address) {
        addresses.add(address);
    }

    /**
     * Removes the given address from this user
     *
     * @param address
     *            the address to remove
     */
    public void removeAddress(AddressEntity address) {
        addresses.remove(address);
    }

    /**
     * Removes all addresses from this user
     */
    public void removeAllAddresses(){
        addresses.clear();
    }

    /**
     * @return the entitlements
     */
    public Set<EntitlementsEntity> getEntitlements() {
        return entitlements;
    }

    /**
     * @param entitlements
     *            the entitlements
     * @deprecated
     */
    @Deprecated
    public void setEntitlements(Set<EntitlementsEntity> entitlements) {
        this.entitlements = entitlements;
    }

    /**
     * Adds a new entitlement to this user
     *
     * @param entitlement
     *            the entitlement to add
     */
    public void addEntitlement(EntitlementsEntity entitlement) {
        entitlements.add(entitlement);
    }

    /**
     * Removes the given entitlement from this user
     *
     * @param entitlement
     *            the entitlement to remove
     */
    public void removeEntitlement(EntitlementsEntity entitlement) {
        entitlements.remove(entitlement);
    }

    /**
     * Removes all entitlement's from this user
     */
    public void removeAllEntitlements(){
        entitlements.clear();
    }

    /**
     * @return the roles
     */
    public Set<RolesEntity> getRoles() {
        return roles;
    }

    /**
     * @param roles
     *            the roles
     * @deprecated
     */
    @Deprecated
    public void setRoles(Set<RolesEntity> roles) {
        this.roles = roles;
    }

    /**
     * Adds a new role to this user
     *
     * @param role
     *            the role to add
     */
    public void addRole(RolesEntity role) {
        roles.add(role);
    }

    /**
     * Removes the given role from this user
     *
     * @param role
     *            the role to remove
     */
    public void removeRole(RolesEntity role) {
        roles.remove(role);
    }

    /**
     * Removes all role's from this user
     */
    public void removeAllRoles(){
        roles.clear();
    }

    /**
     * @return the X509 certs
     */
    public Set<X509CertificateEntity> getX509Certificates() {
        return x509Certificates;
    }

    /**
     * @param x509Certificates
     *            the X509 certs
     * @deprecated
     */
    @Deprecated
    public void setX509Certificates(Set<X509CertificateEntity> x509Certificates) {
        this.x509Certificates = x509Certificates;
    }

    /**
     * Adds a new x509Certificate to this user
     *
     * @param x509Certificate
     *            the x509Certificate to add
     */
    public void addX509Certificate(X509CertificateEntity x509Certificate) {
        x509Certificates.add(x509Certificate);
    }

    /**
     * Removes the given x509Certificate from this user
     *
     * @param x509Certificate
     *            the x509Certificate to remove
     */
    public void removeX509Certificate(X509CertificateEntity x509Certificate) {
        x509Certificates.remove(x509Certificate);
    }

    /**
     * Removes all x509Certificate's from this user
     */
    public void removeAllX509Certificates(){
        x509Certificates.clear();
    }

    /**
     * Adds or updates an extension field value for this User. When updating, the old value of the extension field is
     * removed from this user and the new one will be added.
     *
     * @param extensionValue
     *            The extension field value to add or update
     */
    public void addOrUpdateExtensionValue(ExtensionFieldValueEntity extensionValue) {
        if (extensionValue == null) {
            throw new IllegalArgumentException("extensionValue must not be null");
        }

        if (extensionFieldValues.contains(extensionValue)) {
            extensionFieldValues.remove(extensionValue);
        }

        extensionFieldValues.add(extensionValue);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserEntity [userName=").append(userName).append(", getId()=").append(getId()).append("]");
        return builder.toString();
    }
}
