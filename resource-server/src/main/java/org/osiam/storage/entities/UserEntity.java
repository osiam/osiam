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

import org.osiam.storage.entities.extension.ExtensionEntity;
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity
 */
@Entity(name = "scim_user")
@NamedQueries({@NamedQuery(name = "getUserByUsername", query = "SELECT u FROM scim_user u WHERE u.userName = :username")})
public class UserEntity extends InternalIdSkeleton {

    private static final String MAPPING_NAME = "user";
    private static final long serialVersionUID = -6535056565639057058L;

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
    private Boolean active;

    @Column(nullable = false)
    private String password;

    @Column
    private String displayName;

    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmailEntity> emails;

    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhoneNumberEntity> phoneNumbers;

    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ImEntity> ims;

    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhotoEntity> photos;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AddressEntity> addresses;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntitlementsEntity> entitlements;

    //needs to be eager fetched due to authorization decisions
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<RolesEntity> roles;

    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<X509CertificateEntity> x509Certificates;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "scim_user_scim_extension", joinColumns = {@JoinColumn(name = "scim_user_internal_id", referencedColumnName = "internal_id")},
            inverseJoinColumns = {@JoinColumn(name = "registered_extensions_internal_id", referencedColumnName = "internal_id")})
    private Set<ExtensionEntity> registeredExtensions = new HashSet<>();

    @OneToMany(mappedBy = MAPPING_NAME, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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
     * @param name the name entity
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
     * @param nickName the nick name
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
     * @param profileUrl the profile url
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
     * @param title the title
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
     * @param userType the user type
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
     * @param preferredLanguage the preferred languages
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
     * @param locale the locale
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
     * @param timezone the timezone
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
     * @param active the active status
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
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

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
     * @param userName the user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the emails entity
     */
    public Set<EmailEntity> getEmails() {
        if (emails == null) {
            emails = new HashSet<>();
        }
        return emails;
    }

    /**
     * @param emails the emails entity
     */
    public void setEmails(Set<EmailEntity> emails) {
        //Setting Foreign key in child entity because hibernate did it not automatically
        if (emails != null) {
            for (EmailEntity emailEntity : emails) {
                emailEntity.setUser(this);
            }
        }
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
     * @param userExtensions the extension data of the user
     */
    public void setUserExtensions(Set<ExtensionFieldValueEntity> userExtensions) {
        if (userExtensions != null) {
            for (ExtensionFieldValueEntity extensionValue : userExtensions) {
                extensionValue.setUser(this);
            }
        }
        this.extensionFieldValues = userExtensions;
    }

    /**
     * @return the phone numbers entity
     */
    public Set<PhoneNumberEntity> getPhoneNumbers() {
        if (phoneNumbers == null) {
            phoneNumbers = new HashSet<>();
        }
        return phoneNumbers;
    }

    /**
     * @param phoneNumbers the phone numbers entity
     */
    public void setPhoneNumbers(Set<PhoneNumberEntity> phoneNumbers) {
        //Setting Foreign key in child entity because hibernate did it not automatically
        if (phoneNumbers != null) {
            for (PhoneNumberEntity phoneNumberEntity : phoneNumbers) {
                phoneNumberEntity.setUser(this);
            }
        }
        this.phoneNumbers = phoneNumbers;
    }

    /**
     * @return the instant messaging entity
     */
    public Set<ImEntity> getIms() {
        if (ims == null) {
            ims = new HashSet<>();
        }
        return ims;
    }

    /**
     * @param ims the instant messaging entity
     */
    public void setIms(Set<ImEntity> ims) {
        //Setting Foreign key in child entity because hibernate did it not automatically
        if (ims != null) {
            for (ImEntity imEntity : ims) {
                imEntity.setUser(this);
            }
        }
        this.ims = ims;
    }

    /**
     * @return the photos entity
     */
    public Set<PhotoEntity> getPhotos() {
        if (photos == null) {
            photos = new HashSet<>();
        }
        return photos;
    }

    /**
     * @param photos the photos entity
     */
    public void setPhotos(Set<PhotoEntity> photos) {
        //Setting Foreign key in child entity because hibernate did it not automatically
        if (photos != null) {
            for (PhotoEntity photoEntity : photos) {
                photoEntity.setUser(this);
            }
        }
        this.photos = photos;
    }

    /**
     * @return the addresses entity
     */
    public Set<AddressEntity> getAddresses() {
        if (addresses == null) {
            addresses = new HashSet<>();
        }
        return addresses;
    }

    /**
     * @param addresses the addresses entity
     */
    public void setAddresses(Set<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    /**
     * @return the entitlements
     */
    public Set<EntitlementsEntity> getEntitlements() {
        if (entitlements == null) {
            entitlements = new HashSet<>();
        }
        return entitlements;
    }

    /**
     * @param entitlements the entitlements
     */
    public void setEntitlements(Set<EntitlementsEntity> entitlements) {
        this.entitlements = entitlements;
    }

    /**
     * @return the roles
     */
    public Set<RolesEntity> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
        return roles;
    }

    /**
     * @param roles the roles
     */
    public void setRoles(Set<RolesEntity> roles) {
        this.roles = roles;
    }

    /**
     * @return the X509 certs
     */
    public Set<X509CertificateEntity> getX509Certificates() {
        if (x509Certificates == null) {
            x509Certificates = new HashSet<>();
        }
        return x509Certificates;
    }

    /**
     * @param x509Certificates the X509 certs
     */
    public void setX509Certificates(Set<X509CertificateEntity> x509Certificates) {
        //Setting Foreign key in child entity because hibernate did it not automatically
        if (x509Certificates != null) {
            for (X509CertificateEntity certificateEntity : x509Certificates) {
                certificateEntity.setUser(this);
            }
        }
        this.x509Certificates = x509Certificates;
    }

    /**
     * Registers a new extension for this User. If the given extension is already registered, it will be ignored.
     *
     * @param extension The extension to register
     */
    public void registerExtension(ExtensionEntity extension) {
        if (extension == null) {
            throw new IllegalArgumentException("extension must not be null");
        }

        registeredExtensions.add(extension);
    }

    /**
     * Read all registered user extensions.
     *
     * @return A set of all registered user extensions. Never null;
     */
    public Set<ExtensionEntity> getRegisteredExtensions() {
        return registeredExtensions;
    }

    /**
     * Adds or updates an extension field value for this User. When updating, the
     * old value of the extension field is removed from this user and the new
     * one will be added.
     *
     * @param extensionValue The extension field value to add or update
     */
    public void addOrUpdateExtensionValue(ExtensionFieldValueEntity extensionValue) {
        if (extensionValue == null) {
            throw new IllegalArgumentException("extensionValue must not be null");
        }


        if (extensionFieldValues.contains(extensionValue)) {
            extensionFieldValues.remove(extensionValue);
        }

        extensionValue.setUser(this);

        extensionFieldValues.add(extensionValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        UserEntity that = (UserEntity) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = prime * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "UUID='" + getId() + "\', " +
                "userName='" + userName + '\'' +
                '}';
    }
}
