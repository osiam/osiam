ALTER TABLE scim_address CHANGE streetaddress street_address varchar(255) DEFAULT NULL;

ALTER TABLE scim_address CHANGE postalcode postal_code varchar(255) DEFAULT NULL;

ALTER TABLE scim_extension_field CHANGE extension_internal_id extension bigint(20) NOT NULL;

ALTER TABLE scim_extension_field CHANGE is_required required tinyint(1) DEFAULT NULL;

ALTER TABLE scim_extension_field_value CHANGE extension_field_internal_id extension_field bigint(20) NOT NULL;

ALTER TABLE scim_group CHANGE displayname display_name varchar(255) NOT NULL;

ALTER TABLE scim_group_scim_id CHANGE groups_internal_id groups bigint(20) NOT NULL;

ALTER TABLE scim_group_scim_id CHANGE members_internal_id  members bigint(20) NOT NULL;

ALTER TABLE scim_group_scim_id RENAME TO scim_group_members;

ALTER TABLE scim_id CHANGE meta_id meta bigint(20) NOT NULL;

ALTER TABLE scim_meta CHANGE lastmodified last_modified timestamp NULL DEFAULT NULL;

ALTER TABLE scim_meta CHANGE resourcetype resource_type varchar(255) DEFAULT NULL;

ALTER TABLE scim_name CHANGE familyname family_name varchar(255) DEFAULT NULL;

ALTER TABLE scim_name CHANGE givenname given_name varchar(255) DEFAULT NULL;

ALTER TABLE scim_name CHANGE middlename middle_name varchar(255) DEFAULT NULL;

ALTER TABLE scim_name CHANGE honorificprefix honorific_prefix varchar(255) DEFAULT NULL;

ALTER TABLE scim_name CHANGE honorificsuffix honorific_suffix varchar(255) DEFAULT NULL;

ALTER TABLE scim_user CHANGE username user_name varchar(255) DEFAULT NULL;

ALTER TABLE scim_user CHANGE nickname nick_name varchar(255) DEFAULT NULL;

ALTER TABLE scim_user CHANGE profileurl profile_url longtext;

ALTER TABLE scim_user CHANGE usertype user_type varchar(255) DEFAULT NULL;

ALTER TABLE scim_user CHANGE preferredlanguage preferred_language varchar(255) DEFAULT NULL;

ALTER TABLE scim_user CHANGE displayname display_name varchar(255) DEFAULT NULL;

ALTER TABLE scim_user CHANGE name_id name bigint(20) DEFAULT NULL;
