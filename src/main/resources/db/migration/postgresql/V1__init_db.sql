--
-- The MIT License (MIT)
--
-- Copyright (C) 2013-2016 tarent solutions GmbH
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in all
-- copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
-- SOFTWARE.
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = ON;
SET check_function_bodies = FALSE;
SET client_min_messages = WARNING;
SET default_with_oids = FALSE;

CREATE SEQUENCE resource_server_sequence_scim_extension
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE SEQUENCE resource_server_sequence_scim_extension_field
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE SEQUENCE resource_server_sequence_scim_extension_field_value
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE SEQUENCE resource_server_sequence_scim_id
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE SEQUENCE resource_server_sequence_scim_meta
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE SEQUENCE resource_server_sequence_scim_multi_valued_attribute
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE SEQUENCE resource_server_sequence_scim_name
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE SEQUENCE auth_server_sequence_osiam_client
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE TABLE osiam_client
(
  internal_id BIGINT PRIMARY KEY NOT NULL,
  access_token_validity_seconds INTEGER NOT NULL,
  client_secret VARCHAR(255) NOT NULL,
  id VARCHAR(32) NOT NULL,
  implicit_approval BOOLEAN NOT NULL,
  redirect_uri TEXT NOT NULL,
  refresh_token_validity_seconds INTEGER NOT NULL,
  validity_in_seconds BIGINT NOT NULL
);
CREATE TABLE osiam_client_grants
(
  id BIGINT NOT NULL,
  grants VARCHAR(255)
);
CREATE TABLE osiam_client_scopes
(
  id BIGINT NOT NULL,
  scope VARCHAR(255)
);
CREATE TABLE scim_address
(
  multi_value_id BIGINT PRIMARY KEY NOT NULL,
  is_primary BOOLEAN,
  country VARCHAR(255),
  formatted TEXT,
  locality VARCHAR(255),
  postal_code VARCHAR(255),
  region VARCHAR(255),
  street_address VARCHAR(255),
  type VARCHAR(255),
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_certificate
(
  multi_value_id BIGINT PRIMARY KEY NOT NULL,
  is_primary BOOLEAN,
  value TEXT,
  type VARCHAR(255),
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_email
(
  multi_value_id BIGINT PRIMARY KEY NOT NULL,
  is_primary BOOLEAN,
  value TEXT,
  type VARCHAR(255),
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_entitlements
(
  multi_value_id BIGINT PRIMARY KEY NOT NULL,
  is_primary BOOLEAN,
  value TEXT,
  type VARCHAR(255),
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_extension
(
  internal_id BIGINT PRIMARY KEY NOT NULL,
  urn TEXT NOT NULL
);
CREATE TABLE scim_extension_field
(
  internal_id BIGINT PRIMARY KEY NOT NULL,
  name VARCHAR(255),
  required BOOLEAN NOT NULL,
  type VARCHAR(255) NOT NULL,
  extension BIGINT
);
CREATE TABLE scim_extension_field_value
(
  internal_id BIGINT PRIMARY KEY NOT NULL,
  value TEXT NOT NULL,
  extension_field BIGINT NOT NULL,
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_group
(
  display_name VARCHAR(255) NOT NULL,
  internal_id BIGINT PRIMARY KEY NOT NULL
);
CREATE TABLE scim_group_members
(
  groups BIGINT NOT NULL,
  members BIGINT NOT NULL,
  CONSTRAINT scim_group_members_pkey PRIMARY KEY (groups, members)
);
CREATE TABLE scim_id
(
  internal_id BIGINT PRIMARY KEY NOT NULL,
  external_id VARCHAR(255),
  id VARCHAR(255) NOT NULL,
  meta BIGINT
);
CREATE TABLE scim_im
(
  multi_value_id BIGINT PRIMARY KEY NOT NULL,
  is_primary BOOLEAN,
  value TEXT,
  type VARCHAR(255),
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_meta
(
  id BIGINT PRIMARY KEY NOT NULL,
  created TIMESTAMP,
  last_modified TIMESTAMP,
  location TEXT,
  resource_type VARCHAR(255),
  version VARCHAR(255)
);
CREATE TABLE scim_name
(
  id BIGINT PRIMARY KEY NOT NULL,
  family_name VARCHAR(255),
  formatted TEXT,
  given_name VARCHAR(255),
  honorific_prefix VARCHAR(255),
  honorific_suffix VARCHAR(255),
  middle_name VARCHAR(255)
);
CREATE TABLE scim_phonenumber
(
  multi_value_id BIGINT PRIMARY KEY NOT NULL,
  is_primary BOOLEAN,
  value TEXT,
  type VARCHAR(255),
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_photo
(
  multi_value_id BIGINT PRIMARY KEY NOT NULL,
  is_primary BOOLEAN,
  value TEXT,
  type VARCHAR(255),
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_roles
(
  multi_value_id BIGINT PRIMARY KEY NOT NULL,
  is_primary BOOLEAN,
  value TEXT,
  type VARCHAR(255),
  user_internal_id BIGINT NOT NULL
);
CREATE TABLE scim_user
(
  active BOOLEAN,
  display_name VARCHAR(255),
  locale VARCHAR(255),
  nick_name VARCHAR(255),
  password VARCHAR(255) NOT NULL,
  preferred_language VARCHAR(255),
  profile_url TEXT,
  timezone VARCHAR(255),
  title VARCHAR(255),
  user_name VARCHAR(255) NOT NULL,
  user_type VARCHAR(255),
  internal_id BIGINT PRIMARY KEY NOT NULL,
  name BIGINT
);

CREATE UNIQUE INDEX uk_c34iilt4h1ln91s9ro8m96hru ON osiam_client (id);
ALTER TABLE osiam_client_grants ADD FOREIGN KEY (id) REFERENCES osiam_client (internal_id);
ALTER TABLE osiam_client_scopes ADD FOREIGN KEY (id) REFERENCES osiam_client (internal_id);
ALTER TABLE scim_address ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_4jv7nlhvka8r583hodb6q7wnr ON scim_address (country, region, locality, postal_code, street_address);
CREATE INDEX uk_ie5406dj1t9i0f9hytgvbxjl2 ON scim_address (type);
ALTER TABLE scim_certificate ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_eplkwvpox52tjppj9oogkf6f2 ON scim_certificate (value, type);
CREATE INDEX uk_tb6nu6msjqh1qb2ne5e4ghnp0 ON scim_certificate (value);
CREATE INDEX uk_7k7tc0du5jucy4ranqn8uid4b ON scim_certificate (type);
ALTER TABLE scim_email ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_j86m6mxppkb3g2vx72a11xob1 ON scim_email (value, type);
CREATE INDEX uk_8snvn02x0for0fvcj8erir2k0 ON scim_email (value);
CREATE INDEX uk_hvpieto01a5c7b5edr1v9pom4 ON scim_email (type);
ALTER TABLE scim_entitlements ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_i0njmun17yqq9eslmg7dqehrf ON scim_entitlements (value, type);
CREATE INDEX uk_nxxhl5vhce96gwm0se9spjjjv ON scim_entitlements (value);
CREATE INDEX uk_75wo1phhovp2nbruh2dmfhcwk ON scim_entitlements (type);
CREATE UNIQUE INDEX uk_60sysrrwavtwwnji8nw5tng2x ON scim_extension (urn);
ALTER TABLE scim_extension_field ADD FOREIGN KEY (extension) REFERENCES scim_extension (internal_id);
CREATE UNIQUE INDEX uk_hnihinl5l3jacqliaj7xfm1i1 ON scim_extension_field (extension, name);
ALTER TABLE scim_extension_field_value ADD FOREIGN KEY (extension_field) REFERENCES scim_extension_field (internal_id);
ALTER TABLE scim_extension_field_value ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_f51qdsjk215o1whu8yuqil9x0 ON scim_extension_field_value (user_internal_id, extension_field);
ALTER TABLE scim_group ADD FOREIGN KEY (internal_id) REFERENCES scim_id (internal_id);
CREATE UNIQUE INDEX uk_1s4swwmx1udfpmb2pki9kcn3 ON scim_group (display_name);
ALTER TABLE scim_group_members ADD FOREIGN KEY (groups) REFERENCES scim_group (internal_id);
ALTER TABLE scim_group_members ADD FOREIGN KEY (members) REFERENCES scim_id (internal_id);
ALTER TABLE scim_id ADD FOREIGN KEY (meta) REFERENCES scim_meta (id);
CREATE UNIQUE INDEX uk_qn7vp62pgehnmgki8da06ao9i ON scim_id (external_id);
CREATE UNIQUE INDEX uk_q4ya5m8v6tafgtvw1inqtmm42 ON scim_id (id);
ALTER TABLE scim_im ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_da192a97ita9ygqdlmabnf4bw ON scim_im (value, type);
CREATE INDEX uk_31njuvoulynkorup0b5pjqni6 ON scim_im (value);
CREATE INDEX uk_88yyj57g5nisgp2trhs2yqa91 ON scim_im (type);
CREATE INDEX uk_1o8kevc2e2hfk24f19j3vcia4 ON scim_meta (created);
CREATE INDEX uk_3owu6ibjva850ut4e64p00xi5 ON scim_meta (last_modified);
ALTER TABLE scim_phonenumber ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_lcm5w71ggduyc6nl6d5o3ea8c ON scim_phonenumber (value, type);
CREATE INDEX uk_54x6jg3q2a4lxda68bugk1kw1 ON scim_phonenumber (value);
CREATE INDEX uk_9wnleik7n5uuksit3ls1msow8 ON scim_phonenumber (type);
ALTER TABLE scim_photo ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_iculqbamgtumwnjyjxseafy5h ON scim_photo (value, type);
CREATE INDEX uk_6y89p0fpcdcg2fq9k5u8h1173 ON scim_photo (value);
CREATE INDEX uk_1er38kw2ith4ewuf7b5rhh7br ON scim_photo (type);
ALTER TABLE scim_roles ADD FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);
CREATE INDEX uk_i7n6iwn2x3stgn9q515xn46gi ON scim_roles (value, type);
CREATE INDEX uk_mw914wc9rj4qsue2q60n4ktk4 ON scim_roles (value);
CREATE INDEX uk_8qwt29ewjm8urpi7vk10q2fb3 ON scim_roles (type);
ALTER TABLE scim_user ADD FOREIGN KEY (internal_id) REFERENCES scim_id (internal_id);
ALTER TABLE scim_user ADD FOREIGN KEY (name) REFERENCES scim_name (id);
CREATE UNIQUE INDEX uk_o2e5caak3gw9roil6nbgvew5o ON scim_user (user_name);

INSERT INTO scim_extension VALUES (1, 'urn:org.osiam:scim:extensions:auth-server');
INSERT INTO scim_extension_field (internal_id, required, name, type, extension) VALUES (1, FALSE, 'origin', 'STRING', 1);

INSERT INTO scim_meta (id, created, last_modified, location, resource_type, version)
VALUES (1, '2011-10-10', '2011-10-10', NULL, 'User', NULL);

INSERT INTO scim_id (internal_id, external_id, id, meta)
VALUES (1, NULL, 'cef9452e-00a9-4cec-a086-d171374ffbef', 1);

INSERT INTO scim_user (active, display_name, locale, nick_name, password, preferred_language,
                       profile_url, timezone, title, user_name, user_type, internal_id,
                       name)
VALUES (TRUE, NULL, NULL, NULL,
        'cbae73fac0893291c4792ef19d158a589402288b35cb18fb8406e951b9d95f6b8b06a3526ffebe96ae0d91c04ae615a7fe2af362763db386ccbf3b55c29ae800',
        NULL,
        NULL, NULL, NULL, 'admin', NULL, 1,
        NULL);

INSERT INTO osiam_client (internal_id, access_token_validity_seconds, client_secret,
            id, implicit_approval, redirect_uri, refresh_token_validity_seconds,
            validity_in_seconds)
    VALUES (1, 28800, 'secret',
            'example-client', FALSE, 'http://localhost:5000/oauth2', 86400,
            28800);
INSERT INTO osiam_client_scopes (id, scope) VALUES (1, 'GET');
INSERT INTO osiam_client_scopes (id, scope) VALUES (1, 'POST');
INSERT INTO osiam_client_scopes (id, scope) VALUES (1, 'PUT');
INSERT INTO osiam_client_scopes (id, scope) VALUES (1, 'PATCH');
INSERT INTO osiam_client_scopes (id, scope) VALUES (1, 'DELETE');

INSERT INTO osiam_client_grants (id, grants) VALUES (1, 'authorization_code');
INSERT INTO osiam_client_grants (id, grants) VALUES (1, 'refresh_token');
INSERT INTO osiam_client_grants (id, grants) VALUES (1, 'password');
INSERT INTO osiam_client_grants (id, grants) VALUES (1, 'client_credentials');
