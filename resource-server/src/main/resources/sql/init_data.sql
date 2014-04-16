--
-- Inserting schema version
--

INSERT INTO database_scheme_version VALUES (0.05);

--
-- Auth Server Client
--
INSERT INTO osiam_client (internal_id, accesstokenvalidityseconds, client_secret, expiry, 
            id, implicit_approval, redirect_uri, refreshtokenvalidityseconds, 
            validityinseconds)
    VALUES (2, 2342, 'auth-secret', null, 
            'auth-server', FALSE, '', 4684,
            1);
            
INSERT INTO osiam_client_scopes (id, scope) VALUES (2, 'GET');
INSERT INTO osiam_client_scopes (id, scope) VALUES (2, 'POST');
INSERT INTO osiam_client_scopes (id, scope) VALUES (2, 'PATCH');

INSERT INTO osiam_client_grants (id, grants) VALUES (2, 'client_credentials');

INSERT INTO scim_extension VALUES (1, 'urn:scim:schemas:osiam:2.0:authentication:server');

INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
	VALUES (1, false, 'origin', 'STRING', 1);
