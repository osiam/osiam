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

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.osiam.resources.exception.SCIMDataValidationException;

import java.io.Serializable;
import java.util.*;

/**
 * User resources are meant to enable expression of common User information. It should be possible to express most user
 * data with the core attributes. If more information need to be saved in a user object the user extension can be
 * used to store all customized data.
 * <p>
 * For more detailed information please look at the
 * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
 * </p>
 * <p>
 * client info: The scim schema is mainly meant as a connection link between the OSIAM server and a client like the
 * connector4Java. Some values will not be accepted by the OSIAM server. These specific values have an own client info
 * documentation section.
 * </p>
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class User extends Resource implements Serializable {

    public static final String SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:User";
    private static final long serialVersionUID = -4076516708797425414L;

    private final String userName;
    private final Name name;
    private final String displayName;
    private final String nickName;
    private final String profileUrl;
    private final String title;
    private final String userType;
    private final String preferredLanguage;
    private final String locale;
    private final String timezone;
    private final Boolean active;
    private final String password;
    private final List<Email> emails;
    private final List<PhoneNumber> phoneNumbers;
    private final List<Im> ims;
    private final List<Photo> photos;
    private final List<Address> addresses;
    private final List<GroupRef> groups;
    private final List<Entitlement> entitlements;
    private final List<Role> roles;
    private final List<X509Certificate> x509Certificates;
    private final Map<String, Extension> extensions;

    @JsonCreator
    private User(@JsonProperty("id") String id,
                @JsonProperty("externalId") String externalId,
                @JsonProperty("meta") Meta meta,
                @JsonProperty(value = "schemas", required = true) Set<String> schemas,
                @JsonProperty("userName") String userName,
                @JsonProperty("name") Name name,
                @JsonProperty("displayName") String displayName,
                @JsonProperty("nickName") String nickName,
                @JsonProperty("profileUrl") String profileUrl,
                @JsonProperty("title") String title,
                @JsonProperty("userType") String userType,
                @JsonProperty("preferredLanguage") String preferredLanguage,
                @JsonProperty("locale") String locale,
                @JsonProperty("timezone") String timezone,
                @JsonProperty("active") Boolean active,
                @JsonProperty("emails") List<Email> emails,
                @JsonProperty("phoneNumbers") List<PhoneNumber> phoneNumbers,
                @JsonProperty("ims") List<Im> ims,
                @JsonProperty("photos") List<Photo> photos,
                @JsonProperty("addresses") List<Address> addresses,
                @JsonProperty("groups") List<GroupRef> groups,
                @JsonProperty("entitlements") List<Entitlement> entitlements,
                @JsonProperty("roles") List<Role> roles,
                @JsonProperty("x509Certificates") List<X509Certificate> x509Certificates,
                @JsonProperty("extensions") Map<String, Extension> extensions) {
        super(id, externalId, meta, schemas);
        this.userName = (userName != null ? userName : "");
        this.name = name;
        this.displayName = displayName;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
        this.title = title;
        this.userType = userType;
        this.preferredLanguage = preferredLanguage;
        this.locale = locale;
        this.timezone = timezone;
        this.active = active;
        this.password = "";
        this.emails = (emails != null ? emails : new ArrayList<Email>());
        this.phoneNumbers = (phoneNumbers != null ? phoneNumbers : new ArrayList<PhoneNumber>());
        this.ims = (ims != null ? ims : new ArrayList<Im>());
        this.photos = (photos != null ? photos : new ArrayList<Photo>());
        this.addresses = (addresses != null ? addresses : new ArrayList<Address>());
        this.groups = (groups != null ? groups : new ArrayList<GroupRef>());
        this.entitlements = (entitlements != null ? entitlements : new ArrayList<Entitlement>());
        this.roles = (roles != null ? roles : new ArrayList<Role>());
        this.x509Certificates = (x509Certificates != null ? x509Certificates : new ArrayList<X509Certificate>());
        this.extensions = (extensions != null ? extensions : new HashMap<String, Extension>());
    }

    private User(Builder builder) {
        super(builder);
        this.userName = builder.userName;
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.nickName = builder.nickName;
        this.profileUrl = builder.profileUrl;
        this.title = builder.title;
        this.userType = builder.userType;
        this.preferredLanguage = builder.preferredLanguage;
        this.locale = builder.locale;
        this.timezone = builder.timezone;
        this.active = builder.active;
        this.password = builder.password;

        this.emails = builder.emails;
        this.phoneNumbers = builder.phoneNumbers;
        this.ims = builder.ims;
        this.photos = builder.photos;
        this.addresses = builder.addresses;
        this.groups = builder.groups;
        this.entitlements = builder.entitlements;
        this.roles = builder.roles;
        this.x509Certificates = builder.x509Certificates;
        this.extensions = builder.extensions;
    }

    /**
     * Gets the unique identifier for the User.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the components of the User's real name.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the real {@link Name} of the {@link User}
     */
    public Name getName() {
        return name;
    }

    /**
     * Gets the name of the User, suitable for display to end-users.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the display name of the {@link User}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the casual way to address the user in real life,
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the nickname of the {@link User}
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Gets a fully qualified URL to a page representing the User's online profile.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the progile URL of the {@link User}
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * The user's title, such as "Vice President."
     *
     * @return the title of the {@link User}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the type of the {@link User}
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the type of the {@link User}
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Gets the preferred written or spoken language of the User in ISO 3166-1 alpha 2 format, e.g. "DE" or "US".
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the preferred language of the {@link User}
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * Gets the default location of the User in ISO 639-1 two letter language code, e.g. 'de_DE' or 'en_US'
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the default location of the {@link User}
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Gets the User's time zone in the "Olson" timezone database format
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the time zone of the {@link User}
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Gets a Boolean that indicates the User's administrative status.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     *
     * @return the active status of the {@link User}
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Gets the password from the User.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * <p>
     * client info: if the actual user is loaded from the OSIAM server the password of the user will always be null
     * </p>
     *
     * @return the password of the {@link User}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets all E-mail addresses for the User.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return the email addresses of the {@link User}
     */
    public List<Email> getEmails() {
        return ImmutableList.copyOf(emails);
    }

    /**
     * try to extract an email from the User.
     * If the User has a primary email address this email will be returned.
     * If not the first email address found will be returned.
     * If no Email has been found email.isPresent() == false
     *
     * @return an email if found
     */
    @JsonIgnore
    public Optional<Email> getPrimaryOrFirstEmail() {
        for (Email email : emails) {
            if (email.isPrimary()) {
                return Optional.of(email);
            }
        }

        if (emails.size() > 0) {
            return Optional.of(getEmails().get(0));
        }
        return Optional.absent();
    }

    /**
     * Gets the phone numbers for the user.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return the phone numbers of the {@link User}
     */
    public List<PhoneNumber> getPhoneNumbers() {
        return ImmutableList.copyOf(phoneNumbers);
    }

    /**
     * Gets the instant messaging address for the user.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return the ims of the {@link User}
     */
    public List<Im> getIms() {
        return ImmutableList.copyOf(ims);
    }

    /**
     * Gets the URL's of the photos of the user.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return the photo URL's of the {@link User}
     */
    public List<Photo> getPhotos() {
        return ImmutableList.copyOf(photos);
    }

    /**
     * Gets the physical mailing addresses for this user.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return the addresses of the {@link User}
     */
    public List<Address> getAddresses() {
        return ImmutableList.copyOf(addresses);
    }

    /**
     * Gets a list of groups that the user belongs to.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return a list of all {@link Group}s where the {@link User} is a member of
     */
    public List<GroupRef> getGroups() {
        return ImmutableList.copyOf(groups);
    }

    /**
     * Gets a list of entitlements for the user that represent a thing the User has.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return a list of all entitlements of the {@link User}
     */
    public List<Entitlement> getEntitlements() {
        return ImmutableList.copyOf(entitlements);
    }

    /**
     * Gets a list of roles for the user that collectively represent who the User is e.g., 'Student', "Faculty"
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return a list of the roles of the {@link User}
     */
    public List<Role> getRoles() {
        return ImmutableList.copyOf(roles);
    }

    /**
     * Gets a list of certificates issued to the user. Values are Binary and DER encoded x509.
     * <p>
     * For more detailed information please look at the
     * <a href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     *
     * @return a list of the certificates of the {@link User}
     */
    public List<X509Certificate> getX509Certificates() {
        return ImmutableList.copyOf(x509Certificates);
    }

    /**
     * Provides an unmodifiable view of all additional {@link Extension} fields of the user
     *
     * @return an unmodifiable view of the extensions
     */
    @JsonAnyGetter
    public Map<String, Extension> getExtensions() {
        return ImmutableMap.copyOf(extensions);
    }

    /**
     * Provides the {@link Extension} with the given URN
     *
     * @param urn The URN of the extension
     * @return The extension for the given URN
     * @throws IllegalArgumentException If urn is null or empty
     * @throws NoSuchElementException   If extension with given urn is not available
     */
    public Extension getExtension(String urn) {
        if (urn == null || urn.isEmpty()) {
            throw new IllegalArgumentException("urn must be neither null nor empty");
        }

        if (!extensions.containsKey(urn)) {
            throw new NoSuchElementException("extension " + urn + " is not available");
        }

        return extensions.get(urn);
    }

    /**
     * Checks if an extension with the given urn is present because an extension is not returned if no field is set
     *
     * @param urn urn of the extension
     * @return true if the given extension is present, else false
     */
    public boolean isExtensionPresent(String urn) {
        return extensions.containsKey(urn);
    }

    @Override
    public String toString() {
        return "User [userName=" + userName + ", name=" + name + ", displayName=" + displayName + ", nickName="
                + nickName + ", profileUrl=" + profileUrl + ", title=" + title + ", userType=" + userType
                + ", preferredLanguage=" + preferredLanguage + ", locale=" + locale + ", timezone=" + timezone
                + ", active=" + active + ", password=" + password + ", emails=" + emails + ", phoneNumbers="
                + phoneNumbers + ", ims=" + ims + ", photos=" + photos + ", addresses=" + addresses + ", groups="
                + groups + ", entitlements=" + entitlements + ", roles=" + roles + ", x509Certificates="
                + x509Certificates + ", extensions=" + extensions + ", getId()=" + getId() + ", getExternalId()="
                + getExternalId() + ", getMeta()=" + getMeta() + ", getSchemas()=" + getSchemas() + "]";
    }

    /**
     * Builder class that is used to build {@link User} instances
     */
    public static class Builder extends Resource.Builder {
        private String userName;
        private String password = "";
        private Boolean active;
        private String timezone;
        private String locale;
        private String preferredLanguage;
        private String userType;
        private String title;
        private String profileUrl;
        private String nickName;
        private String displayName;
        private Name name;
        private List<Email> emails = new ArrayList<>();
        private List<PhoneNumber> phoneNumbers = new ArrayList<>();
        private List<Im> ims = new ArrayList<>();
        private List<Photo> photos = new ArrayList<>();
        private List<Address> addresses = new ArrayList<>();
        private List<GroupRef> groups = new ArrayList<>();
        private List<Entitlement> entitlements = new ArrayList<>();
        private List<Role> roles = new ArrayList<>();
        private List<X509Certificate> x509Certificates = new ArrayList<>();
        private Map<String, Extension> extensions = new HashMap<>();

        /**
         * creates a new User.Builder based on the given userName and user. All values of the given user will be copied
         * expect the userName will be be overridden by the given one
         *
         * @param userName the new userName of the user
         * @param user     a existing user
         */
        public Builder(String userName, User user) {
            super(user);
            addSchema(SCHEMA);
            if (user != null) {
                this.userName = user.userName;
                this.name = user.name;
                this.displayName = user.displayName;
                this.nickName = user.nickName;
                this.profileUrl = user.profileUrl;
                this.title = user.title;
                this.userType = user.userType;
                this.preferredLanguage = user.preferredLanguage;
                this.locale = user.locale;
                this.timezone = user.timezone;
                this.active = user.active;
                this.password = user.password;
                this.emails = MoreObjects.firstNonNull(user.emails, this.emails);
                this.phoneNumbers = MoreObjects.firstNonNull(user.phoneNumbers, this.phoneNumbers);
                this.ims = MoreObjects.firstNonNull(user.ims, this.ims);
                this.photos = MoreObjects.firstNonNull(user.photos, this.photos);
                this.addresses = MoreObjects.firstNonNull(user.addresses, this.addresses);
                this.groups = MoreObjects.firstNonNull(user.groups, this.groups);
                this.entitlements = MoreObjects.firstNonNull(user.entitlements, this.entitlements);
                this.roles = MoreObjects.firstNonNull(user.roles, this.roles);
                this.x509Certificates = MoreObjects.firstNonNull(user.x509Certificates, this.x509Certificates);
                this.extensions = MoreObjects.firstNonNull(user.extensions, this.extensions);
            }
            if (!Strings.isNullOrEmpty(userName)) {
                this.userName = userName;
            }
        }

        /**
         * Constructs a new builder by with a set userName
         *
         * @param userName Unique identifier for the User (See {@link User#getUserName()})
         * @throws SCIMDataValidationException if the given userName is null or empty
         */
        public Builder(String userName) {
            this(userName, null);
            if (Strings.isNullOrEmpty(userName)) {
                throw new IllegalArgumentException("userName must not be null or empty.");
            }
        }

        /**
         * Creates a new builder without a userName
         */
        public Builder() {
            this(null, null);
        }

        /**
         * Constructs a new builder by copying all values from the given {@link User}
         *
         * @param user a old {@link User}
         * @throws SCIMDataValidationException if the given user is null
         */
        public Builder(User user) {
            this(null, user);
            if (user == null) {
                throw new SCIMDataValidationException("The given user must not be null");
            }
        }

        /**
         * Sets the components of the {@link User}'s real name (See {@link User#getName()}).
         *
         * @param name the name object of the {@link User}
         * @return the builder itself
         */
        public Builder setName(Name name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the display name (See {@link User#getDisplayName()}).
         *
         * @param displayName the display name of the {@link User}
         * @return the builder itself
         */
        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * Sets the nick name (See {@link User#getNickName()}).
         *
         * @param nickName the nick name of the {@link User}
         * @return the builder itself
         */
        public Builder setNickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        /**
         * Sets the profile URL (See {@link User#getProfileUrl()}).
         *
         * @param profileUrl the profil URL of the {@link User}
         * @return the builder itself
         */
        public Builder setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
            return this;
        }

        /**
         * Sets the user's title (See {@link User#getTitle()}).
         *
         * @param title the title of the {@link User}
         * @return the builder itself
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the user type (See {@link User#getUserType()}).
         *
         * @param userType the type of the {@link User}
         * @return the builder itself
         */
        public Builder setUserType(String userType) {
            this.userType = userType;
            return this;
        }

        /**
         * Sets the preferred language of the USer (See {@link User#getPreferredLanguage()}).
         *
         * @param preferredLanguage sets the preferred language of the {@link User}
         * @return the builder itself
         */
        public Builder setPreferredLanguage(String preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
            return this;
        }

        /**
         * Sets the default location of the User (See {@link User#getLocale()}).
         *
         * @param locale sets the local of the {@link User}
         * @return the builder itself
         */
        public Builder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Sets the User's time zone (See {@link User#getTimezone()}).
         *
         * @param timezone sets the time zone of the {@link User}
         * @return the builder itself
         */
        public Builder setTimezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        /**
         * Sets a Boolean value indicating the User's administrative status. (See {@link User#isActive()})
         *
         * @param active the active status of the {@link User}
         * @return the builder itself
         */
        public Builder setActive(Boolean active) {
            this.active = active;
            return this;
        }

        /**
         * Sets the User's clear text password (See {@link User#getPassword()}).
         *
         * @param password the password as clear text
         * @return the builder itself
         */
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Adds the E-mail addresses for the User (See {@link User#getEmails()}).
         *
         * @param emails a collection of email to be added
         * @return the builder itself
         */
        public Builder addEmails(Collection<Email> emails) {
            if (emails != null) {
                for (Email email : emails) {
                    addEmail(email);
                }
            }
            return this;
        }

        /**
         * adds an email to the User
         *
         * @param email an email to add
         * @return the builder itself
         */
        public Builder addEmail(Email email) {
            if (email != null) {
                this.emails.add(new Email.Builder(email).build());
            }
            return this;
        }

        /**
         * removes all email from the actual User
         *
         * @return the builder itself
         */
        public Builder removeEmails() {
            this.emails.clear();
            return this;
        }

        /**
         * removes one single email from the User
         *
         * @param email an email to be removed
         * @return the builder itself
         */
        public Builder removeEmail(Email email) {
            this.emails.remove(email);
            return this;
        }

        /**
         * Adds the phone numbers for the User (See {@link User#getPhoneNumbers()}).
         *
         * @param phoneNumbers the phone numbers of the the {@link User}
         * @return the builder itself
         */
        public Builder addPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
            if (phoneNumbers != null) {
                for (PhoneNumber phoneNumber : phoneNumbers) {
                    addPhoneNumber(phoneNumber);
                }
            }
            return this;
        }

        /**
         * adds an phoneNumber to the User
         *
         * @param phoneNumber a phoneNumber to add
         * @return the builder itself
         */
        public Builder addPhoneNumber(PhoneNumber phoneNumber) {
            if (phoneNumber != null) {
                this.phoneNumbers.add(new PhoneNumber.Builder(phoneNumber).build());
            }
            return this;
        }

        /**
         * removes all phoneNumbers from the actual User
         *
         * @return the builder itself
         */
        public Builder removePhoneNumbers() {
            this.phoneNumbers.clear();
            return this;
        }

        /**
         * removes one single phoneNumber from the User
         *
         * @param phoneNumber an phoneNumber to be removed
         * @return the builder itself
         */
        public Builder removePhoneNumber(PhoneNumber phoneNumber) {
            this.phoneNumbers.remove(phoneNumber);
            return this;
        }

        /**
         * Adds the instant messaging addresses for the User (See {@link User#getIms()}).
         *
         * @param ims a collection of the ims of the {@link User}
         * @return the builder itself
         */
        public Builder addIms(Collection<Im> ims) {
            if (ims != null) {
                for (Im im : ims) {
                    addIm(im);
                }
            }
            return this;
        }

        /**
         * adds an Im to the User
         *
         * @param im a Im to add
         * @return the builder itself
         */
        public Builder addIm(Im im) {
            if (im != null) {
                this.ims.add(new Im.Builder(im).build());
            }
            return this;
        }

        /**
         * removes all ims from the actual User
         *
         * @return the builder itself
         */
        public Builder removeIms() {
            this.ims.clear();
            return this;
        }

        /**
         * removes one single im from the User
         *
         * @param im a im to be removed
         * @return the builder itself
         */
        public Builder removeIm(Im im) {
            this.ims.remove(im);
            return this;
        }

        /**
         * Adds the URL's of photo's of the User (See {@link User#getPhotos()}).
         *
         * @param photos the photos of the {@link User}
         * @return the builder itself
         */
        public Builder addPhotos(Collection<Photo> photos) {
            if (photos != null) {
                for (Photo photo : photos) {
                    addPhoto(photo);
                }
            }
            return this;
        }

        /**
         * adds an Photo to the User
         *
         * @param photo a Photo to add
         * @return the builder itself
         */
        public Builder addPhoto(Photo photo) {
            if (photo != null) {
                this.photos.add(new Photo.Builder(photo).build());
            }
            return this;
        }

        /**
         * removes all Photos from the actual User
         *
         * @return the builder itself
         */
        public Builder removePhotos() {
            this.photos.clear();
            return this;
        }

        /**
         * removes one single Photo from the User
         *
         * @param photo a photo to be removed
         * @return the builder itself
         */
        public Builder removePhoto(Photo photo) {
            this.photos.remove(photo);
            return this;
        }

        /**
         * Adds the physical mailing addresses for this User (See {@link User#getAddresses()}).
         *
         * @param addresses a collection of the addresses of the {@link User}
         * @return the builder itself
         */
        public Builder addAddresses(Collection<Address> addresses) {
            if (addresses != null) {
                for (Address address : addresses) {
                    addAddress(address);
                }
            }
            return this;
        }

        /**
         * adds an Address to the User
         *
         * @param address a Address to add
         * @return the builder itself
         */
        public Builder addAddress(Address address) {
            if (address != null) {
                this.addresses.add(new Address.Builder(address).build());
            }
            return this;
        }

        /**
         * removes all Addresses from the actual User
         *
         * @return the builder itself
         */
        public Builder removeAddresses() {
            this.addresses.clear();
            return this;
        }

        /**
         * removes one single Photo from the User
         *
         * @param address a Address to be removed
         * @return the builder itself
         */
        public Builder removeAddress(Address address) {
            this.addresses.remove(address);
            return this;
        }

        /**
         * Sets a list of groups that the user belongs to (See {@link User#getGroups()})
         * <p>
         * client info: The groups where the user is a member of will only be set from the OSIAM server. If you want to
         * put a user into a group, you have to add the user as a member to the group. If a {@link User} which is send
         * to the OSIAM server has this value filled, the value will be ignored or the action will be rejected.
         * </p>
         *
         * @param groups groups of the User
         * @return the builder itself
         */
        public Builder setGroups(List<GroupRef> groups) {
            this.groups = groups;
            return this;
        }

        /**
         * Adds a collection of entitlements for the User (See {@link User#getEntitlements()}).
         *
         * @param entitlements the entitlements of the {@link User}
         * @return the builder itself
         */
        public Builder addEntitlements(Collection<Entitlement> entitlements) {
            if (entitlements != null) {
                for (Entitlement entitlement : entitlements) {
                    addEntitlement(entitlement);
                }
            }
            return this;
        }

        /**
         * adds an Entitlement to the User
         *
         * @param entitlement a Entitlement to add
         * @return the builder itself
         */
        public Builder addEntitlement(Entitlement entitlement) {
            if (entitlement != null) {
                this.entitlements.add(new Entitlement.Builder(entitlement).build());
            }
            return this;
        }

        /**
         * removes all Entitlements from the actual User
         *
         * @return the builder itself
         */
        public Builder removeEntitlements() {
            this.entitlements.clear();
            return this;
        }

        /**
         * removes one single Entitlement from the User
         *
         * @param entitlement a Entitlement to be removed
         * @return the builder itself
         */
        public Builder removeEntitlement(Entitlement entitlement) {
            this.entitlements.remove(entitlement);
            return this;
        }

        /**
         * Sets a list of roles for the User (See {@link User#getRoles()}).
         *
         * @param roles a list of roles
         * @return the builder itself
         */
        public Builder addRoles(Collection<Role> roles) {
            if (roles != null) {
                for (Role role : roles) {
                    addRole(role);
                }
            }
            return this;
        }

        /**
         * adds an Role to the User
         *
         * @param role a Role to add
         * @return the builder itself
         */
        public Builder addRole(Role role) {
            if (role != null) {
                this.roles.add(new Role.Builder(role).build());
            }
            return this;
        }

        /**
         * removes all Roles from the actual User
         *
         * @return the builder itself
         */
        public Builder removeRoles() {
            this.roles.clear();
            return this;
        }

        /**
         * removes one single Role from the User
         *
         * @param role a Role to be removed
         * @return the builder itself
         */
        public Builder removeRole(Role role) {
            this.roles.remove(role);
            return this;
        }

        /**
         * Sets a collection of certificates issued to the User (See {@link User#getX509Certificates()}).
         *
         * @param x509Certificates the certificates of the {@link User}
         * @return the builder itself
         */
        public Builder addX509Certificates(Collection<X509Certificate> x509Certificates) {
            if (x509Certificates != null) {
                for (X509Certificate x509Certificate : x509Certificates) {
                    addX509Certificate(x509Certificate);
                }
            }
            return this;
        }

        /**
         * adds an X509Certificate to the User
         *
         * @param x509Certificate a X509Certificate to add
         * @return the builder itself
         */
        public Builder addX509Certificate(X509Certificate x509Certificate) {
            if (x509Certificate != null) {
                this.x509Certificates.add(new X509Certificate.Builder(x509Certificate).build());
            }
            return this;
        }

        /**
         * removes all X509Certificates from the actual User
         *
         * @return the builder itself
         */
        public Builder removeX509Certificates() {
            this.x509Certificates.clear();
            return this;
        }

        /**
         * removes one single X509Certificate from the User
         *
         * @param x509Certificate a X509Certificate to be removed
         * @return the builder itself
         */
        public Builder removeX509Certificate(X509Certificate x509Certificate) {
            this.x509Certificates.remove(x509Certificate);
            return this;
        }

        /**
         * Adds a collection of Extension to the User (See {@link User#getExtensions()}).
         *
         * @param extensions a collection of extensions
         * @return the builder itself
         */
        public Builder addExtensions(Collection<Extension> extensions) {
            if (extensions != null) {
                for (Extension entry : extensions) {
                    this.addExtension(entry);
                }
            }
            return this;
        }

        /**
         * Sets a Extension to the User (See {@link User#getExtension(String)}).
         *
         * @param extension a single Extension
         * @return the builder itself
         */
        public Builder addExtension(Extension extension) {
            if (extension != null) {
                extensions.put(extension.getUrn(), extension);
                addSchema(extension.getUrn());
            }
            return this;
        }

        /**
         * removes all Extensions from the actual User
         *
         * @return the builder itself
         */
        public Builder removeExtensions() {
            this.extensions.clear();
            return this;
        }

        /**
         * removes one single Extension from the User
         *
         * @param urn the urn from the Extension to be removed
         * @return the builder itself
         */
        public Builder removeExtension(String urn) {
            this.extensions.remove(urn);
            return this;
        }

        @Override
        public Builder setMeta(Meta meta) {
            super.setMeta(meta);
            return this;
        }

        @Override
        public Builder setExternalId(String externalId) {
            super.setExternalId(externalId);
            return this;
        }

        @Override
        public Builder setId(String id) {
            super.setId(id);
            return this;
        }

        /**
         * @deprecated Don't use this method - let the extensions add their schema themselves. Will be removed in
         * version 1.8 or 2.0
         */
        @Override
        @Deprecated
        public Builder setSchemas(Set<String> schemas) {
            super.setSchemas(schemas);
            return this;
        }

        @Override
        public User build() {
            return new User(this);
        }
    }
}
