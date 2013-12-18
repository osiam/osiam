--
-- Updating hibernate sequence start value
--
SELECT
  pg_catalog.setval('hibernate_sequence', 4, FALSE);


--
-- Default data for user, role, meta and client
--
INSERT INTO scim_meta(id, created, lastmodified, location, resourcetype, version)
    VALUES (2, '2011-10-10', '2011-10-10', NULL, 'User', NULL);

INSERT INTO scim_id(internal_id, externalid, id, meta_id)
    VALUES (1, 2, 'cef9452e-00a9-4cec-a086-d171374ffbef', NULL);

INSERT INTO osiam_client(internal_id, accesstokenvalidityseconds, client_secret, expiry, 
            id, implicit_approval, redirect_uri, refreshtokenvalidityseconds, 
            validityinseconds)
    VALUES (3, 2342, 'secret', null, 
            'example-client', FALSE, 'http://localhost:5000/oauth2', 2342, 
            1337);
INSERT INTO osiam_client_scopes VALUES (3, 'GET');
INSERT INTO osiam_client_scopes VALUES (3, 'POST');
INSERT INTO osiam_client_scopes VALUES (3, 'PUT');
INSERT INTO osiam_client_scopes VALUES (3, 'PATCH');
INSERT INTO osiam_client_scopes VALUES (3, 'DELETE');

INSERT INTO osiam_client_grants VALUES (3, 'authorization_code');
INSERT INTO osiam_client_grants VALUES (3, 'refresh_token');
INSERT INTO osiam_client_grants VALUES (3, 'password');
INSERT INTO osiam_client_grants VALUES (3, 'client_credentials');

INSERT INTO scim_user(active, displayname, locale, nickname, password, preferredlanguage, 
            profileurl, timezone, title, username, usertype, internal_id, 
            name_id)
    VALUES (TRUE, NULL, NULL, NULL, 'cbae73fac0893291c4792ef19d158a589402288b35cb18fb8406e951b9d95f6b8b06a3526ffebe96ae0d91c04ae615a7fe2af362763db386ccbf3b55c29ae800', NULL, 
            NULL, NULL, NULL, 'marissa', NULL, 1, 
            NULL);

INSERT INTO scim_roles(
            multi_value_id, value, user_internal_id)
    VALUES (1, 'USER', 1);