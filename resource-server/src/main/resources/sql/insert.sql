--
-- Updating hibernate sequence start value
--
SELECT
  pg_catalog.setval('hibernate_sequence', 4, FALSE);


--
-- Default data for user, role, meta and client
--

INSERT INTO scim_meta VALUES (2, '2011-10-10', '2011-10-10', NULL, NULL, 'User');
INSERT INTO scim_id VALUES (1, NULL, 2, 'cef9452e-00a9-4cec-a086-d171374ffbef');
INSERT INTO osiam_client VALUES (3, 'example-client', 'http://localhost:5000/oauth2', 'secret', 2342, 2342, 1337, FALSE, null);
INSERT INTO osiam_client_scopes VALUES (3, 'GET');
INSERT INTO osiam_client_scopes VALUES (3, 'POST');
INSERT INTO osiam_client_scopes VALUES (3, 'PUT');
INSERT INTO osiam_client_scopes VALUES (3, 'PATCH');
INSERT INTO osiam_client_scopes VALUES (3, 'DELETE');

INSERT INTO osiam_client_grants VALUES (3, 'authorization_code');
INSERT INTO osiam_client_grants VALUES (3, 'refresh-token');
INSERT INTO osiam_client_grants VALUES (3, 'password');
INSERT INTO osiam_client_grants VALUES (3, 'client_credentials');

INSERT INTO scim_user VALUES (TRUE, NULL, NULL, NULL, 'cbae73fac0893291c4792ef19d158a589402288b35cb18fb8406e951b9d95f6b8b06a3526ffebe96ae0d91c04ae615a7fe2af362763db386ccbf3b55c29ae800', NULL, NULL, NULL, NULL, 'marissa', NULL, 1, NULL);

--
-- TODO: insert right role for marissa
--
INSERT INTO scim_roles VALUES (1, 1);