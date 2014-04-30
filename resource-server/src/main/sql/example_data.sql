--
-- Example User: marissa, pw: koala, Roles: 'USER'
--
INSERT INTO scim_meta (id, created, lastmodified, location, resourcetype, version)
    VALUES (1, '2011-10-10', '2011-10-10', NULL, 'User', NULL);

INSERT INTO scim_id (internal_id, externalid, id, meta_id)
    VALUES (1, NULL, 'cef9452e-00a9-4cec-a086-d171374ffbef', 1);

INSERT INTO scim_user (active, displayname, locale, nickname, password, preferredlanguage, 
            profileurl, timezone, title, username, usertype, internal_id, 
            name_id)
    VALUES (TRUE, NULL, NULL, NULL, 'cbae73fac0893291c4792ef19d158a589402288b35cb18fb8406e951b9d95f6b8b06a3526ffebe96ae0d91c04ae615a7fe2af362763db386ccbf3b55c29ae800', NULL, 
            NULL, NULL, NULL, 'marissa', NULL, 1, 
            NULL);

INSERT INTO scim_roles (multi_value_id, value, user_internal_id)
    VALUES (1, 'USER', 1);
