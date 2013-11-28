package org.osiam.storage.filter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SetAttribute;

import org.joda.time.format.ISODateTimeFormat;
import org.osiam.storage.entities.AddressEntity;
import org.osiam.storage.entities.AddressEntity.CanonicalAddressTypes;
import org.osiam.storage.entities.AddressEntity_;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.EmailEntity.CanonicalEmailTypes;
import org.osiam.storage.entities.EmailEntity_;
import org.osiam.storage.entities.EntitlementsEntity;
import org.osiam.storage.entities.EntitlementsEntity_;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.GroupEntity_;
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.ImEntity.CanonicalImTypes;
import org.osiam.storage.entities.ImEntity_;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.osiam.storage.entities.MetaEntity_;
import org.osiam.storage.entities.NameEntity_;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.PhoneNumberEntity.CanonicalPhoneNumberTypes;
import org.osiam.storage.entities.PhoneNumberEntity_;
import org.osiam.storage.entities.PhotoEntity;
import org.osiam.storage.entities.PhotoEntity.CanonicalPhotoTypes;
import org.osiam.storage.entities.PhotoEntity_;
import org.osiam.storage.entities.RolesEntity;
import org.osiam.storage.entities.RolesEntity_;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.UserEntity_;
import org.osiam.storage.entities.X509CertificateEntity;
import org.osiam.storage.entities.X509CertificateEntity_;

enum UserFilterField implements FilterField<UserEntity> {
    EXTERNALID("externalid") {
        @Override
        public Predicate addFilter(Root<UserEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.externalId), value, cb);
        }
    },
    META_CREATED("meta.created") {
        @Override
        public Predicate addFilter(Root<UserEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(root.get(UserEntity_.meta).get(MetaEntity_.created),
                    date, cb);
        }
    },
    META_LASTMODIFIED("meta.lastmodified") {
        @Override
        public Predicate addFilter(Root<UserEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(
                    root.get(UserEntity_.meta).get(MetaEntity_.lastModified),
                    date, cb);
        }
    },
    META_LOCATION("meta.location") {
        @Override
        public Predicate addFilter(Root<UserEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.meta)
                    .get(MetaEntity_.location), value, cb);
        }
    },
    USERNAME("username") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.userName), value, cb);
        }

    },
    DISPLAYNAME("displayname") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.displayName), value, cb);
        }

    },
    NICKNAME("nickname") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.nickName), value, cb);
        }

    },
    PROFILEURL("profileurl") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.profileUrl), value, cb);
        }

    },
    TITLE("title") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.title), value, cb);
        }

    },
    USERTYPE("usertype") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.userType), value, cb);
        }

    },
    PREFERREDLANGUAGE("preferredlanguage") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.preferredLanguage), value, cb);
        }

    },
    LOCALE("locale") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.locale), value, cb);
        }

    },
    TIMEZONE("timezone") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.timezone), value, cb);
        }

    },
    ACTIVE("active") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForBooleanField(root.get(UserEntity_.active), Boolean.valueOf(value),
                    cb);
        }

    },
    NAME_FORMATTED("name.formatted") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.formatted),
                    value, cb);
        }

    },
    NAME_FAMILYNAME("name.familyname") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.familyName),
                    value, cb);
        }

    },
    NAME_GIVENNAME("name.givenname") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.givenName),
                    value, cb);
        }

    },
    NAME_MIDDLENAME("name.middlename") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.middleName),
                    value, cb);
        }

    },
    NAME_HONORIFICPREFIX("name.honorificprefix") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(
                    root.get(UserEntity_.name).get(NameEntity_.honorificPrefix), value, cb);
        }

    },
    NAME_HONORIFICSUFFIX("name.honorificsuffix") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(
                    root.get(UserEntity_.name).get(NameEntity_.honorificSuffix), value, cb);
        }

    },
    EMAILS("emails") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {

            SetJoin<UserEntity, EmailEntity> join = createOrGetJoin("emails", root, UserEntity_.emails);
            return constraint.createPredicateForStringField(join.get(EmailEntity_.value), value, cb);
        }

    },
    EMAILS_VALUE("emails.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {

            SetJoin<UserEntity, EmailEntity> join = createOrGetJoin("emails", root, UserEntity_.emails);
            return constraint.createPredicateForStringField(join.get(EmailEntity_.value), value, cb);
        }

    },
    EMAILS_TYPE("emails.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            CanonicalEmailTypes emailType;
            if (constraint == FilterConstraint.PRESENT && (value == null || value.isEmpty())) {
                emailType = null;
            } else {
                emailType = CanonicalEmailTypes.valueOf(value);
            }
            SetJoin<UserEntity, EmailEntity> join = createOrGetJoin("emails", root, UserEntity_.emails);
            return constraint.createPredicateForEmailTypeField(join.get(EmailEntity_.type),
                    emailType, cb);
        }

    },
    EMAILS_PRIMARY("emails.primary") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {

            SetJoin<UserEntity, EmailEntity> join = createOrGetJoin("emails", root, UserEntity_.emails);
            return constraint.createPredicateForBooleanField(join.get(EmailEntity_.primary),
                    Boolean.valueOf(value), cb);
        }

    },
    PHONENUMBERS("phoneNumbers") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, PhoneNumberEntity> join = createOrGetJoin("phoneNumbers", root,
                    UserEntity_.phoneNumbers);
            return constraint.createPredicateForStringField(join.get(PhoneNumberEntity_.value), value, cb);
        }
    },
    PHONENUMBERS_VALUE("phonenumbers.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, PhoneNumberEntity> join = createOrGetJoin("phoneNumbers", root,
                    UserEntity_.phoneNumbers);
            return constraint.createPredicateForStringField(join.get(PhoneNumberEntity_.value), value, cb);
        }
    },
    PHONENUMBERS_TYPE("phonenumbers.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            CanonicalPhoneNumberTypes phoneNumberType;
            if (constraint == FilterConstraint.PRESENT && (value == null || value.isEmpty())) {
                phoneNumberType = null;
            } else {
                phoneNumberType = CanonicalPhoneNumberTypes.valueOf(value);
            }
            SetJoin<UserEntity, PhoneNumberEntity> join = createOrGetJoin("phoneNumbers", root,
                    UserEntity_.phoneNumbers);
            return constraint.createPredicateForPhoneNumberTypeField(join.get(PhoneNumberEntity_.type),
                    phoneNumberType, cb);
        }
    },
    IMS("ims") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, ImEntity> join = createOrGetJoin("ims", root, UserEntity_.ims);
            return constraint.createPredicateForStringField(join.get(ImEntity_.value), value, cb);

        }
    },
    IMS_VALUE("ims.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, ImEntity> join = createOrGetJoin("ims", root, UserEntity_.ims);
            return constraint.createPredicateForStringField(join.get(ImEntity_.value), value, cb);
        }

    },
    IMS_TYPE("ims.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            CanonicalImTypes imType;
            if (constraint == FilterConstraint.PRESENT && (value == null || value.isEmpty())) {
                imType = null;
            } else {
                imType = CanonicalImTypes.valueOf(value);
            }
            SetJoin<UserEntity, ImEntity> join = createOrGetJoin("ims", root, UserEntity_.ims);
            return constraint.createPredicateForImTypeField(join.get(ImEntity_.type),
                    imType, cb);
        }
    },
    PHOTOS("photos") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, PhotoEntity> join = createOrGetJoin("photos", root, UserEntity_.photos);
            return constraint.createPredicateForStringField(join.get(PhotoEntity_.value), value, cb);

        }
    },
    PHOTOS_VALUE("photos.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, PhotoEntity> join = createOrGetJoin("photos", root, UserEntity_.photos);
            return constraint.createPredicateForStringField(join.get(PhotoEntity_.value), value, cb);

        }
    },
    PHOTOS_TYPE("photos.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            CanonicalPhotoTypes photoType;
            if (constraint == FilterConstraint.PRESENT && (value == null || value.isEmpty())) {
                photoType = null;
            } else {
                photoType = CanonicalPhotoTypes.valueOf(value);
            }
            SetJoin<UserEntity, PhotoEntity> join = createOrGetJoin("photos", root, UserEntity_.photos);
            return constraint.createPredicateForPhotoTypeField(join.get(PhotoEntity_.type),
                    photoType, cb);
        }
    },
    ADDRESS_REGION("address.region") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin("addresses", root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.region), value, cb);
        }
    },
    ADDRESS_STREETADDRESS("address.streetaddress") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin("addresses", root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.streetAddress), value, cb);
        }
    },
    ADDRESS_FORMATTED("address.formatted") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin("addresses", root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.formatted), value, cb);
        }
    },
    ADDRESS_POSTALCODE("address.postalcode") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin("addresses", root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.postalCode), value, cb);
        }
    },
    ADDRESS_LOCALITY("address.locality") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin("addresses", root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.locality), value, cb);
        }
    },
    ADDRESS_TYPE("address.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            CanonicalAddressTypes addressType;
            if (constraint == FilterConstraint.PRESENT && (value == null || value.isEmpty())) {
                addressType = null;
            } else {
                addressType = CanonicalAddressTypes.valueOf(value);
            }
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin("addresses", root, UserEntity_.addresses);
            return constraint.createPredicateForAddressTypeField(join.get(AddressEntity_.type),
                    addressType, cb);
        }
    },
    ADDRESS_COUNTRY("address.country") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin("addresses", root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.country), value, cb);
        }
    },
    ENTITLEMENTS("entitlements") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, EntitlementsEntity> join = createOrGetJoin("entitlements", root,
                    UserEntity_.entitlements);
            return constraint.createPredicateForStringField(join.get(EntitlementsEntity_.value), value, cb);
        }
    },
    ENTITLEMENTS_VALUE("entitlements.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, EntitlementsEntity> join = createOrGetJoin("entitlements", root,
                    UserEntity_.entitlements);
            return constraint.createPredicateForStringField(join.get(EntitlementsEntity_.value), value, cb);
        }
    },
    ROLES("roles") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, RolesEntity> join = createOrGetJoin("roles", root,
                    UserEntity_.roles);
            return constraint.createPredicateForStringField(join.get(RolesEntity_.value), value, cb);
        }
    },
    ROLES_VALUE("roles.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, RolesEntity> join = createOrGetJoin("roles", root,
                    UserEntity_.roles);
            return constraint.createPredicateForStringField(join.get(RolesEntity_.value), value, cb);
        }
    },
    X509CERTIFICATES("x509certificates") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, X509CertificateEntity> join = createOrGetJoin("x509Certificates", root,
                    UserEntity_.x509Certificates);
            return constraint.createPredicateForStringField(join.get(X509CertificateEntity_.value), value, cb);
        }
    },
    X509CERTIFICATES_VALUE("x509certificates.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, X509CertificateEntity> join = createOrGetJoin("x509Certificates", root,
                    UserEntity_.x509Certificates);
            return constraint.createPredicateForStringField(join.get(X509CertificateEntity_.value), value, cb);
        }
    },
    GROUPS("groups") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, GroupEntity> join = createOrGetJoinForGroups("groups", root,
                    UserEntity_.groups);
            return constraint.createPredicateForStringField(join.get(GroupEntity_.id), value, cb);
        }
    };

    private static final Map<String, UserFilterField> stringToEnum = new HashMap<>();

    static {
        for (UserFilterField filterField : values()) {
            stringToEnum.put(filterField.toString(), filterField);
        }
    }

    private final String name;

    private UserFilterField(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static UserFilterField fromString(String name) {
        return stringToEnum.get(name);
    }

    @SuppressWarnings("unchecked")
    protected <T> SetJoin<UserEntity, T> createOrGetJoin(String alias, Root<UserEntity> root,
            SetAttribute<UserEntity, T> attribute) {

        for (Join<UserEntity, ?> currentJoin : root.getJoins()) {
            if (currentJoin.getAlias().equals(alias)) {
                return (SetJoin<UserEntity, T>) currentJoin;
            }
        }

        SetJoin<UserEntity, T> join = root.join(attribute, JoinType.LEFT);
        join.alias(alias);

        return join;
    }

    @SuppressWarnings("unchecked")
    protected SetJoin<UserEntity, GroupEntity> createOrGetJoinForGroups(String alias, Root<UserEntity> root,
            SetAttribute<InternalIdSkeleton, GroupEntity> attribute) {

        for (Join<UserEntity, ?> currentJoin : root.getJoins()) {
            if (currentJoin.getAlias().equals(alias)) {
                return (SetJoin<UserEntity, GroupEntity>) currentJoin;
            }
        }

        final SetJoin<UserEntity, GroupEntity> join = root.join(attribute, JoinType.LEFT);
        join.alias(alias);

        return join;
    }

}