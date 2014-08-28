-- Must be imported into database before starting resource server

-- This extension needed by the auth server

INSERT INTO scim_extension VALUES (1, 'urn:org.osiam:scim:extensions:auth-server');

INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
		VALUES (1, false, 'origin', 'STRING', 1);
