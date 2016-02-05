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

package org.osiam.resources.scim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to create a UpdateUser Object to update a existing User
 */
public final class UpdateUser {

    private User user;

    private UpdateUser(Builder builder) {
        user = builder.updateUser.build();
    }

    /**
     * the Scim conform User to be used to update a existing User
     *
     * @return User to update
     */
    public User getScimConformUpdateUser() {
        return user;
    }

    /**
     * The Builder is used to construct instances of the {@link UpdateUser}
     */
    public static class Builder {

        private static final String DELETE = "delete";
        private String userName;
        private String nickName;
        private String externalId;
        private String locale;
        private String password;
        private String preferredLanguage;
        private String profileUrl;
        private String timezone;
        private String title;
        private Name name;
        private String userType;
        private String displayName;
        private Boolean active;
        private User.Builder updateUser = null;
        private Set<String> deleteFields = new HashSet<>();
        private List<Email> emails = new ArrayList<>();
        private List<Im> ims = new ArrayList<>();
        private List<PhoneNumber> phoneNumbers = new ArrayList<>();
        private List<Address> addresses = new ArrayList<>();
        private List<Entitlement> entitlements = new ArrayList<>();
        private List<Photo> photos = new ArrayList<>();
        private List<Role> roles = new ArrayList<>();
        private List<X509Certificate> certificates = new ArrayList<>();
        private Set<Extension> extensions = new HashSet<>();

        public Builder() {
        }

        /**
         * updates the nickName of a existing user
         *
         * @param userName the new user name
         * @return The builder itself
         */
        public Builder updateUserName(String userName) {
            this.userName = userName;
            return this;
        }

        /**
         * adds a new address to the existing addresses of a existing user
         *
         * @param address the new address
         * @return The builder itself
         */
        public Builder addAddress(Address address) {
            addresses.add(address);
            return this;
        }

        /**
         * deletes the given address from the list of existing addresses of a existing user
         *
         * @param address address to be deleted
         * @return The builder itself
         */
        public Builder deleteAddress(Address address) {
            Address deleteAddress = new Address.Builder(address)
                    .setOperation(DELETE)
                    .build();
            addresses.add(deleteAddress);
            return this;
        }

        /**
         * deletes all existing addresses of the a existing user
         *
         * @return The builder itself
         */
        public Builder deleteAddresses() {
            deleteFields.add("addresses");
            return this;
        }

        /**
         * updates the old Address with the new one
         *
         * @param oldAddress to be replaced
         * @param newAddress new Address
         * @return The builder itself
         */
        public Builder updateAddress(Address oldAddress, Address newAddress) {
            deleteAddress(oldAddress);
            addAddress(newAddress);
            return this;
        }

        /**
         * deletes the nickName of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteNickName() {
            deleteFields.add("nickName");
            return this;
        }

        /**
         * updates the nickName of a existing user
         *
         * @param nickName the new nickName
         * @return The builder itself
         */
        public Builder updateNickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        /**
         * delete the external Id of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteExternalId() {
            deleteFields.add("externalId");
            return this;
        }

        /**
         * updates the external id of a existing user
         *
         * @param externalId new external id
         * @return The builder itself
         */
        public Builder updateExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * delete the local value of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteLocal() {
            deleteFields.add("locale");
            return this;
        }

        /**
         * updates the local of a existing user
         *
         * @param locale new local
         * @return The builder itself
         */
        public Builder updateLocale(String locale) {
            this.locale = locale;
            return this;
        }

        /**
         * updates the password of a existing user
         *
         * @param password new password
         * @return The builder itself
         */
        public Builder updatePassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * delete the preferred Language of a existing user
         *
         * @return The builder itself
         */
        public Builder deletePreferredLanguage() {
            deleteFields.add("preferredLanguage");
            return this;
        }

        /**
         * updates the preferred language of a existing user
         *
         * @param preferredLanguage new preferred language
         * @return The builder itself
         */
        public Builder updatePreferredLanguage(String preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
            return this;
        }

        /**
         * deletes the profil Url of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteProfileUrl() {
            deleteFields.add("profileUrl");
            return this;
        }

        /**
         * updates the profil URL of a existing user
         *
         * @param profileUrl new profilUrl
         * @return The builder itself
         */
        public Builder updateProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
            return this;
        }

        /**
         * deletes the timezone of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteTimezone() {
            deleteFields.add("timezone");
            return this;
        }

        /**
         * updates the timezone of a existing user
         *
         * @param timezone new timeZone
         * @return The builder itself
         */
        public Builder updateTimezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        /**
         * deletes the title of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteTitle() {
            deleteFields.add("title");
            return this;
        }

        /**
         * updates the title of a existing user
         *
         * @param title new tile
         * @return The builder itself
         */
        public Builder updateTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * deletes the name of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteName() {
            deleteFields.add("name");
            return this;
        }

        /**
         * updates the name of a existing user
         *
         * @param name new Name
         * @return The builder itself
         */
        public Builder updateName(Name name) {
            this.name = name;
            return this;
        }

        /**
         * deletes the user type of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteUserType() {
            deleteFields.add("userType");
            return this;
        }

        /**
         * updates the user type of a existing user
         *
         * @param userType new user type
         * @return The builder itself
         */
        public Builder updateUserType(String userType) {
            this.userType = userType;
            return this;
        }

        /**
         * deletes the display name of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteDisplayName() {
            deleteFields.add("displayName");
            return this;
        }

        /**
         * updates the display name of a existing user
         *
         * @param displayName new display name
         * @return The builder itself
         */
        public Builder updateDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * deletes all emails of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteEmails() {
            deleteFields.add("emails");
            return this;
        }

        /**
         * deletes the given email of a existing user
         *
         * @param email to be deleted
         * @return The builder itself
         */
        public Builder deleteEmail(Email email) {
            Email deleteEmail = new Email.Builder()
                    .setValue(email.getValue())
                    .setType(email.getType())
                    .setOperation(DELETE).build();
            emails.add(deleteEmail);
            return this;
        }

        /**
         * adds or updates a emil of an existing user if the .getValue() already exists a update will be done. If not a
         * new one will be added
         *
         * @param email new email
         * @return The builder itself
         */
        public Builder addEmail(Email email) {
            emails.add(email);
            return this;
        }

        /**
         * updates the old Email with the new one
         *
         * @param oldEmail to be replaced
         * @param newEmail new Email
         * @return The builder itself
         */
        public Builder updateEmail(Email oldEmail, Email newEmail) {
            deleteEmail(oldEmail);
            addEmail(newEmail);
            return this;
        }

        /**
         * deletes all X509Certificates of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteX509Certificates() {
            deleteFields.add("x509Certificates");
            return this;
        }

        /**
         * deletes the given certificate of a existing user
         *
         * @param certificate to be deleted
         * @return The builder itself
         */
        public Builder deleteX509Certificate(X509Certificate certificate) {
            X509Certificate deleteCertificates = new X509Certificate.Builder()
                    .setValue(certificate.getValue())
                    .setOperation(DELETE).build();
            certificates.add(deleteCertificates);
            return this;
        }

        /**
         * adds or updates certificate to an existing user if the .getValue() already exists a update will be done. If
         * not a new one will be added
         *
         * @param certificate new certificate
         * @return The builder itself
         */
        public Builder addX509Certificate(X509Certificate certificate) {
            certificates.add(certificate);
            return this;
        }

        /**
         * updates the old X509Certificate with the new one
         *
         * @param oldCertificate to be replaced
         * @param newCertificate new X509Certificate
         * @return The builder itself
         */
        public Builder updateX509Certificate(X509Certificate oldCertificate, X509Certificate newCertificate) {
            deleteX509Certificate(oldCertificate);
            addX509Certificate(newCertificate);
            return this;
        }

        /**
         * deletes all roles of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteRoles() {
            deleteFields.add("roles");
            return this;
        }

        /**
         * deletes the given role of a existing user
         *
         * @param role to be deleted
         * @return The builder itself
         */
        public Builder deleteRole(Role role) {
            Role deleteRole = new Role.Builder()
                    .setValue(role.getValue())
                    .setOperation(DELETE).build();
            roles.add(deleteRole);
            return this;
        }

        /**
         * deletes the given role of a existing user
         *
         * @param role to be deleted
         * @return The builder itself
         */
        public Builder deleteRole(String role) {
            Role deleteRole = new Role.Builder()
                    .setValue(role)
                    .setOperation(DELETE).build();
            roles.add(deleteRole);
            return this;
        }

        /**
         * adds or updates a role of an existing user if the .getValue() already exists a update will be done. If not a
         * new one will be added
         *
         * @param role new role
         * @return The builder itself
         */
        public Builder addRole(Role role) {
            roles.add(role);
            return this;
        }

        /**
         * updates the old Role with the new one
         *
         * @param oldRole to be replaced
         * @param newRole new Role
         * @return The builder itself
         */
        public Builder updateRole(Role oldRole, Role newRole) {
            deleteRole(oldRole);
            addRole(newRole);
            return this;
        }

        /**
         * deletes all ims of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteIms() {
            deleteFields.add("ims");
            return this;
        }

        /**
         * deletes the ims of a existing user
         *
         * @param im to be deleted
         * @return The builder itself
         */
        public Builder deleteIm(Im im) {
            Im deleteIms = new Im.Builder()
                    .setValue(im.getValue())
                    .setType(im.getType())
                    .setOperation(DELETE).build();
            this.ims.add(deleteIms);
            return this;
        }

        /**
         * adds or updates a ims to an existing user if the .getValue() already exists a update will be done. If not a
         * new one will be added
         *
         * @param im new ims
         * @return The builder itself
         */
        public Builder addIm(Im im) {
            this.ims.add(im);
            return this;
        }

        /**
         * updates the old Ims with the new one
         *
         * @param oldIm to be replaced
         * @param newIm new Ims
         * @return The builder itself
         */
        public Builder updateIm(Im oldIm, Im newIm) {
            deleteIm(oldIm);
            addIm(newIm);
            return this;
        }

        /**
         * adds or updates a phoneNumber to an existing user if the .getValue() already exists a update will be done. If
         * not a new one will be added
         *
         * @param phoneNumber new phoneNumber
         * @return The builder itself
         */
        public Builder addPhoneNumber(PhoneNumber phoneNumber) {
            phoneNumbers.add(phoneNumber);
            return this;
        }

        /**
         * deletes the phonenumber of a existing user
         *
         * @param phoneNumber to be deleted
         * @return The builder itself
         */
        public Builder deletePhoneNumber(PhoneNumber phoneNumber) {
            PhoneNumber deletePhoneNumber = new PhoneNumber.Builder()
                    .setValue(phoneNumber.getValue())
                    .setType(phoneNumber.getType())
                    .setOperation(DELETE).build();
            phoneNumbers.add(deletePhoneNumber);
            return this;
        }

        /**
         * deletes all phonenumbers of a existing user
         *
         * @return The builder itself
         */
        public Builder deletePhoneNumbers() {
            deleteFields.add("phoneNumbers");
            return this;
        }

        /**
         * updates the old PhoneNumber with the new one
         *
         * @param oldPhoneNumber to be replaced
         * @param newPhoneNumber new PhoneNumber
         * @return The builder itself
         */
        public Builder updatePhoneNumber(PhoneNumber oldPhoneNumber, PhoneNumber newPhoneNumber) {
            deletePhoneNumber(oldPhoneNumber);
            addPhoneNumber(newPhoneNumber);
            return this;
        }

        /**
         * adds or updates a photo to an existing user if the .getValue() already exists a update will be done. If not a
         * new one will be added
         *
         * @param photo new photo
         * @return The builder itself
         */
        public Builder addPhoto(Photo photo) {
            photos.add(photo);
            return this;
        }

        /**
         * deletes the photo of a existing user
         *
         * @param photo to be deleted
         * @return The builder itself
         */
        public Builder deletePhoto(Photo photo) {
            Photo deletePhoto = new Photo.Builder()
                    .setValue(photo.getValueAsURI())
                    .setType(photo.getType())
                    .setOperation(DELETE)
                    .build();
            photos.add(deletePhoto);
            return this;
        }

        /**
         * deletes all photos of a existing user
         *
         * @return The builder itself
         */
        public Builder deletePhotos() {
            deleteFields.add("photos");
            return this;
        }

        /**
         * updates the old Photo with the new one
         *
         * @param oldPhoto to be replaced
         * @param newPhoto new Photo
         * @return The builder itself
         */
        public Builder updatePhoto(Photo oldPhoto, Photo newPhoto) {
            deletePhoto(oldPhoto);
            addPhoto(newPhoto);
            return this;
        }

        /**
         * deletes all entitlements of a existing user
         *
         * @return The builder itself
         */
        public Builder deleteEntitlements() {
            deleteFields.add("entitlements");
            return this;
        }

        /**
         * deletes the entitlement of a existing user
         *
         * @param entitlement to be deleted
         * @return The builder itself
         */
        public Builder deleteEntitlement(Entitlement entitlement) {
            Entitlement deleteEntitlement = new Entitlement.Builder()
                    .setValue(entitlement.getValue())
                    .setType(entitlement.getType())
                    .setOperation(DELETE)
                    .build();
            entitlements.add(deleteEntitlement);
            return this;
        }

        /**
         * adds or updates a entitlement to an existing user if the .getValue() already exists a update will be done. If
         * not a new one will be added
         *
         * @param entitlement new entitlement
         * @return The builder itself
         */
        public Builder addEntitlement(Entitlement entitlement) {
            entitlements.add(entitlement);
            return this;
        }

        /**
         * updates the old Entitlement with the new one
         *
         * @param oldEntitlement to be replaced
         * @param newEntitlement new Entitlement
         * @return The builder itself
         */
        public Builder updateEntitlement(Entitlement oldEntitlement, Entitlement newEntitlement) {
            deleteEntitlement(oldEntitlement);
            addEntitlement(newEntitlement);
            return this;
        }

        /**
         * updates the active status of a existing User to the given value
         *
         * @param active new active status
         * @return The builder itself
         */
        public Builder updateActive(boolean active) {
            this.active = active;
            return this;
        }

        /**
         * deletes the given extension of a existing user
         *
         * @param urn the id of the extension to be deleted
         * @return The builder itself
         */
        public Builder deleteExtension(String urn) {
            deleteFields.add(urn);
            return this;
        }

        /**
         * deletes the given extension field of a existing user
         *
         * @param urn       the id of the extension to be deleted
         * @param fieldName the fieldName of a the extension to be deleted
         * @return The builder itself
         */
        public Builder deleteExtensionField(String urn, String fieldName) {
            deleteFields.add(urn + "." + fieldName);
            return this;
        }

        /**
         * updates the given fields in the extension. If the User doesn't have the given extension fields, they will be
         * added.
         *
         * @param extension extension with all fields that need to be updated or added
         * @return The builder itself
         */
        public Builder updateExtension(Extension extension) {
            extensions.add(extension);
            return this;
        }

        /**
         * constructs a UpdateUser with the given values
         *
         * @return a valid {@link UpdateUser}
         */
        public UpdateUser build() {
            if (userName == null || userName.isEmpty()) {
                updateUser = new User.Builder();
            } else {
                updateUser = new User.Builder(userName);
            }
            if (nickName != null) {
                updateUser.setNickName(nickName);
            }
            if (externalId != null) {
                updateUser.setExternalId(externalId);
            }
            if (locale != null) {
                updateUser.setLocale(locale);
            }
            if (password != null) {
                updateUser.setPassword(password);
            }
            if (preferredLanguage != null) {
                updateUser.setPreferredLanguage(preferredLanguage);
            }
            if (profileUrl != null) {
                updateUser.setProfileUrl(profileUrl);
            }
            if (timezone != null) {
                updateUser.setTimezone(timezone);
            }
            if (title != null) {
                updateUser.setTitle(title);
            }
            if (name != null) {
                updateUser.setName(name);
            }
            if (userType != null) {
                updateUser.setUserType(userType);
            }
            if (displayName != null) {
                updateUser.setDisplayName(displayName);
            }
            if (active != null) {
                updateUser.setActive(active);
            }
            if (deleteFields.size() > 0) {
                Meta meta = new Meta.Builder()
                        .setAttributes(deleteFields).build();
                updateUser.setMeta(meta);
            }
            if (emails.size() > 0) {
                updateUser.addEmails(emails);
            }
            if (phoneNumbers.size() > 0) {
                updateUser.addPhoneNumbers(phoneNumbers);
            }
            if (addresses.size() > 0) {
                updateUser.addAddresses(addresses);
            }
            if (entitlements.size() > 0) {
                updateUser.addEntitlements(entitlements);
            }
            if (ims.size() > 0) {
                updateUser.addIms(ims);
            }
            if (photos.size() > 0) {
                updateUser.addPhotos(photos);
            }
            if (roles.size() > 0) {
                updateUser.addRoles(roles);
            }
            if (certificates.size() > 0) {
                updateUser.addX509Certificates(certificates);
            }
            if (extensions.size() > 0) {
                updateUser.addExtensions(extensions);
            }

            return new UpdateUser(this);
        }
    }
}
