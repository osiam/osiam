ALTER TABLE scim_address RENAME streetaddress TO street_address;

ALTER TABLE scim_address RENAME postalcode TO postal_code;

ALTER TABLE scim_extension_field RENAME extension_internal_id TO extension;

ALTER TABLE scim_extension_field RENAME is_required TO required;

ALTER TABLE scim_extension_field_value RENAME extension_field_internal_id TO extension_field;

ALTER TABLE scim_group RENAME displayname TO display_name;

ALTER TABLE scim_group_scim_id RENAME groups_internal_id TO groups;

ALTER TABLE scim_group_scim_id RENAME members_internal_id TO members;

ALTER TABLE scim_group_scim_id RENAME TO scim_group_members;

ALTER TABLE scim_id RENAME meta_id TO meta;

ALTER TABLE scim_meta RENAME lastmodified TO last_modified;

ALTER TABLE scim_meta RENAME resourcetype TO resource_type;

ALTER TABLE scim_name RENAME familyname TO family_name;

ALTER TABLE scim_name RENAME givenname TO given_name;

ALTER TABLE scim_name RENAME middlename TO middle_name;

ALTER TABLE scim_name RENAME honorificprefix TO honorific_prefix;

ALTER TABLE scim_name RENAME honorificsuffix TO honorific_suffix;

ALTER TABLE scim_user RENAME username TO user_name;

ALTER TABLE scim_user RENAME nickname TO nick_name;

ALTER TABLE scim_user RENAME profileurl TO profile_url;

ALTER TABLE scim_user RENAME usertype TO user_type;

ALTER TABLE scim_user RENAME preferredlanguage TO preferred_language;

ALTER TABLE scim_user RENAME displayname TO display_name;

ALTER TABLE scim_user RENAME name_id TO name;
