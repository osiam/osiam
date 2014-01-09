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

package org.osiam.storage.query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SetAttribute;

import org.joda.time.format.ISODateTimeFormat;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Entitlement;
import org.osiam.resources.scim.Im;
import org.osiam.resources.scim.PhoneNumber;
import org.osiam.resources.scim.Photo;
import org.osiam.storage.entities.AddressEntity;
import org.osiam.storage.entities.AddressEntity_;
import org.osiam.storage.entities.BaseMultiValuedAttributeEntityWithValue_;
import org.osiam.storage.entities.BaseMultiValuedAttributeEntity_;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.EmailEntity_;
import org.osiam.storage.entities.EntitlementEntity;
import org.osiam.storage.entities.EntitlementsEntity_;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.GroupEntity_;
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.ImEntity_;
import org.osiam.storage.entities.MetaEntity_;
import org.osiam.storage.entities.NameEntity_;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.PhoneNumberEntity_;
import org.osiam.storage.entities.PhotoEntity;
import org.osiam.storage.entities.PhotoEntity_;
import org.osiam.storage.entities.ResourceEntity;
import org.osiam.storage.entities.ResourceEntity_;
import org.osiam.storage.entities.RoleEntity;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.UserEntity_;
import org.osiam.storage.entities.X509CertificateEntity;

public enum UserQueryField implements QueryField<UserEntity> {
    EXTERNALID("externalid") {
        @Override
        public Predicate addFilter(Root<UserEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(ResourceEntity_.externalId), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(ResourceEntity_.externalId);
        }
    },
    META_CREATED("meta.created") {
        @Override
        public Predicate addFilter(Root<UserEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(root.get(ResourceEntity_.meta).get(MetaEntity_.created),
                    date, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(ResourceEntity_.meta).get(MetaEntity_.created);
        }
    },
    META_LASTMODIFIED("meta.lastmodified") {
        @Override
        public Predicate addFilter(Root<UserEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Date date = ISODateTimeFormat.dateTimeParser().parseDateTime(value).toDate();
            return constraint.createPredicateForDateField(
                    root.get(ResourceEntity_.meta).get(MetaEntity_.lastModified),
                    date, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(ResourceEntity_.meta).get(MetaEntity_.lastModified);
        }
    },
    META_LOCATION("meta.location") {
        @Override
        public Predicate addFilter(Root<UserEntity> root,
                FilterConstraint constraint, String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(ResourceEntity_.meta)
                    .get(MetaEntity_.location), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(ResourceEntity_.meta).get(MetaEntity_.location);
        }
    },
    USERNAME("username") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.userName), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.userName);
        }

    },
    DISPLAYNAME("displayname") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.displayName), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.displayName);
        }

    },
    NICKNAME("nickname") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.nickName), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.nickName);
        }

    },
    PROFILEURL("profileurl") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.profileUrl), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.profileUrl);
        }

    },
    TITLE("title") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.title), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.title);
        }

    },
    USERTYPE("usertype") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.userType), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.userType);
        }

    },
    PREFERREDLANGUAGE("preferredlanguage") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.preferredLanguage), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.preferredLanguage);
        }

    },
    LOCALE("locale") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.locale), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.locale);
        }

    },
    TIMEZONE("timezone") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.timezone), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.timezone);
        }

    },
    ACTIVE("active") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForBooleanField(root.get(UserEntity_.active), Boolean.valueOf(value),
                    cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.active);
        }

    },
    NAME_FORMATTED("name.formatted") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.formatted),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.name).get(NameEntity_.formatted);
        }

    },
    NAME_FAMILYNAME("name.familyname") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.familyName),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.name).get(NameEntity_.familyName);
        }

    },
    NAME_GIVENNAME("name.givenname") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.givenName),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.name).get(NameEntity_.givenName);
        }

    },
    NAME_MIDDLENAME("name.middlename") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(root.get(UserEntity_.name).get(NameEntity_.middleName),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.name).get(NameEntity_.middleName);
        }

    },
    NAME_HONORIFICPREFIX("name.honorificprefix") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(
                    root.get(UserEntity_.name).get(NameEntity_.honorificPrefix), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.name).get(NameEntity_.honorificPrefix);
        }

    },
    NAME_HONORIFICSUFFIX("name.honorificsuffix") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            return constraint.createPredicateForStringField(
                    root.get(UserEntity_.name).get(NameEntity_.honorificSuffix), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            return root.get(UserEntity_.name).get(NameEntity_.honorificSuffix);
        }

    },
    EMAILS("emails") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {

            SetJoin<UserEntity, EmailEntity> join = createOrGetJoin(EMAIL_ALIAS, root, UserEntity_.emails);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }

    },
    EMAILS_VALUE("emails.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {

            SetJoin<UserEntity, EmailEntity> join = createOrGetJoin(EMAIL_ALIAS, root, UserEntity_.emails);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }

    },
    EMAILS_TYPE("emails.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            Email.Type emailType = null;

            if (constraint != FilterConstraint.PRESENT) {
                emailType = new Email.Type(value);
            }
            SetJoin<UserEntity, EmailEntity> join = createOrGetJoin(EMAIL_ALIAS, root, UserEntity_.emails);
            return constraint.createPredicateForMultiValuedAttributeTypeField(join.get(EmailEntity_.type), // NOSONAR -
                                                                                                           // XEntity_.X
                                                                                                           // will
                    // be filled by JPA provider
                    emailType, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }

    },
    EMAILS_PRIMARY("emails.primary") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {

            SetJoin<UserEntity, EmailEntity> join = createOrGetJoin(EMAIL_ALIAS, root, UserEntity_.emails);
            return constraint.createPredicateForBooleanField(join.get(BaseMultiValuedAttributeEntity_.primary), // NOSONAR
                                                                                                                // -
                                                                                                                // XEntity_.X
                    // will be filled by JPA provider
                    Boolean.valueOf(value), cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }

    },
    PHONENUMBERS("phoneNumbers") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, PhoneNumberEntity> join = createOrGetJoin(PHONENUMBERS_ALIAS, root,
                    UserEntity_.phoneNumbers);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    PHONENUMBERS_VALUE("phonenumbers.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, PhoneNumberEntity> join = createOrGetJoin(PHONENUMBERS_ALIAS, root,
                    UserEntity_.phoneNumbers);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    PHONENUMBERS_TYPE("phonenumbers.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            PhoneNumber.Type phoneNumberType = null;

            if (constraint != FilterConstraint.PRESENT) {
                phoneNumberType = new PhoneNumber.Type(value);
            }
            SetJoin<UserEntity, PhoneNumberEntity> join = createOrGetJoin(PHONENUMBERS_ALIAS, root,
                    UserEntity_.phoneNumbers);
            return constraint.createPredicateForMultiValuedAttributeTypeField(join.get(PhoneNumberEntity_.type), // NOSONAR -
                    // XEntity_.X will be filled by JPA provider
                    phoneNumberType, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    IMS("ims") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, ImEntity> join = createOrGetJoin(IMS_ALIAS, root, UserEntity_.ims);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);

        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    IMS_VALUE("ims.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, ImEntity> join = createOrGetJoin(IMS_ALIAS, root, UserEntity_.ims);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }

    },
    IMS_TYPE("ims.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            Im.Type imType = null;

            if (constraint != FilterConstraint.PRESENT) {
                imType = new Im.Type(value);
            }

            SetJoin<UserEntity, ImEntity> join = createOrGetJoin(IMS_ALIAS, root, UserEntity_.ims);
            return constraint.createPredicateForMultiValuedAttributeTypeField(join.get(ImEntity_.type), imType, cb); // NOSONAR -
            // XEntity_.X will be filled by JPA provider
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    PHOTOS("photos") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, PhotoEntity> join = createOrGetJoin(PHOTOS_ALIAS, root, UserEntity_.photos);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb); // NOSONAR -
            // XEntity_.X will be filled by JPA provider

        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    PHOTOS_VALUE("photos.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, PhotoEntity> join = createOrGetJoin(PHOTOS_ALIAS, root, UserEntity_.photos);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);

        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    PHOTOS_TYPE("photos.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            Photo.Type photoType = null;

            if (constraint != FilterConstraint.PRESENT) {
                photoType = new Photo.Type(value);
            }

            SetJoin<UserEntity, PhotoEntity> join = createOrGetJoin(PHOTOS_ALIAS, root, UserEntity_.photos);
            return constraint.createPredicateForMultiValuedAttributeTypeField(join.get(PhotoEntity_.type), photoType, cb); // NOSONAR -
            // XEntity_.X will be filled by JPA provider
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ADDRESS_REGION("address.region") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin(ADDRESS_ALIAS, root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.region), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ADDRESS_STREETADDRESS("address.streetaddress") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin(ADDRESS_ALIAS, root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.streetAddress), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ADDRESS_FORMATTED("address.formatted") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin(ADDRESS_ALIAS, root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.formatted), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ADDRESS_POSTALCODE("address.postalcode") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin(ADDRESS_ALIAS, root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.postalCode), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ADDRESS_LOCALITY("address.locality") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin(ADDRESS_ALIAS, root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.locality), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ADDRESS_TYPE("address.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Address.Type addressType = null;

            if (constraint != FilterConstraint.PRESENT) {
                addressType = new Address.Type(value);
            }

            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin(ADDRESS_ALIAS, root, UserEntity_.addresses);
            return constraint.createPredicateForMultiValuedAttributeTypeField(join.get(AddressEntity_.type),
                    addressType, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ADDRESS_COUNTRY("address.country") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, AddressEntity> join = createOrGetJoin(ADDRESS_ALIAS, root,
                    UserEntity_.addresses);
            return constraint.createPredicateForStringField(join.get(AddressEntity_.country), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ENTITLEMENTS("entitlements") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, EntitlementEntity> join = createOrGetJoin("entitlements", root,
                    UserEntity_.entitlements);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ENTITLEMENTS_VALUE("entitlements.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, EntitlementEntity> join = createOrGetJoin("entitlements", root,
                    UserEntity_.entitlements);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ENTITLEMENTS_TYPE("entitlements.type") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint, String value, CriteriaBuilder cb) {
            Entitlement.Type type = null;

            if (constraint != FilterConstraint.PRESENT) {
                type = new Entitlement.Type(value);
            }

            SetJoin<UserEntity, EntitlementEntity> join = createOrGetJoin(ENTITLEMENTS_ALIAS, root, UserEntity_.entitlements);
            return constraint.createPredicateForMultiValuedAttributeTypeField(join.get(EntitlementsEntity_.type),
                    type, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ROLES("roles") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, RoleEntity> join = createOrGetJoin("roles", root,
                    UserEntity_.roles);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    ROLES_VALUE("roles.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, RoleEntity> join = createOrGetJoin("roles", root,
                    UserEntity_.roles);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    X509CERTIFICATES("x509certificates") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, X509CertificateEntity> join = createOrGetJoin("x509Certificates", root,
                    UserEntity_.x509Certificates);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    X509CERTIFICATES_VALUE("x509certificates.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, X509CertificateEntity> join = createOrGetJoin("x509Certificates", root,
                    UserEntity_.x509Certificates);
            return constraint.createPredicateForStringField(join.get(BaseMultiValuedAttributeEntityWithValue_.value),
                    value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    GROUPS("groups") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, GroupEntity> join = createOrGetJoinForGroups(GROUPS_ALIAS, root,
                    ResourceEntity_.groups);
            return constraint.createPredicateForStringField(join.get(ResourceEntity_.id), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    GROUPS_VALUE("groups.value") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, GroupEntity> join = createOrGetJoinForGroups(GROUPS_ALIAS, root,
                    ResourceEntity_.groups);
            return constraint.createPredicateForStringField(join.get(ResourceEntity_.id), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    },
    GROUPS_DISPLAY("groups.display") {
        @Override
        public Predicate addFilter(Root<UserEntity> root, FilterConstraint constraint,
                String value, CriteriaBuilder cb) {
            SetJoin<UserEntity, GroupEntity> join = createOrGetJoinForGroups(GROUPS_ALIAS, root,
                    ResourceEntity_.groups);
            return constraint.createPredicateForStringField(join.get(GroupEntity_.displayName), value, cb);
        }

        @Override
        public Expression<?> createSortByField(Root<UserEntity> root, CriteriaBuilder cb) {
            throw handleSortByFieldNotSupported(toString());
        }
    };

    private static final Map<String, UserQueryField> STRING_TO_ENUM = new HashMap<>();

    static {
        for (UserQueryField filterField : values()) {
            STRING_TO_ENUM.put(filterField.toString(), filterField);
        }
    }

    private final String name;

    private static final String EMAIL_ALIAS = "emails";
    private static final String PHONENUMBERS_ALIAS = "phoneNumbers";
    private static final String IMS_ALIAS = "ims";
    private static final String PHOTOS_ALIAS = "photos";
    private static final String ADDRESS_ALIAS = "addresses";
    private static final String GROUPS_ALIAS = "groups";
    private static final String ENTITLEMENTS_ALIAS = "entitlements";

    private UserQueryField(String name) {
        this.name = name;
    }

    protected RuntimeException handleSortByFieldNotSupported(String fieldName) {
        throw new RuntimeException("Sorting by " + fieldName + " is not supported yet"); // NOSONAR - will be removed
                                                                                         // after implementing
    }

    @Override
    public String toString() {
        return name;
    }

    public static UserQueryField fromString(String name) {
        return STRING_TO_ENUM.get(name);
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
            SetAttribute<ResourceEntity, GroupEntity> attribute) {

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