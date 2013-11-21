DROP TABLE IF EXISTS scim_user_scim_roles CASCADE;
DROP TABLE IF EXISTS scim_user_scim_entitlements CASCADE;
DROP TABLE IF EXISTS scim_user_scim_address CASCADE;
DROP TABLE IF EXISTS scim_user CASCADE;
DROP TABLE IF EXISTS scim_roles CASCADE;
DROP TABLE IF EXISTS scim_photo CASCADE;
DROP TABLE IF EXISTS scim_phonenumber CASCADE;
DROP TABLE IF EXISTS scim_name CASCADE;
DROP TABLE IF EXISTS scim_meta CASCADE;
DROP TABLE IF EXISTS scim_manager CASCADE;
DROP TABLE IF EXISTS scim_im CASCADE;
DROP TABLE IF EXISTS scim_id CASCADE;
DROP TABLE IF EXISTS scim_group_scim_id CASCADE;
DROP TABLE IF EXISTS scim_group CASCADE;
DROP TABLE IF EXISTS scim_entitlements CASCADE;
DROP TABLE IF EXISTS scim_enterprise CASCADE;
DROP TABLE IF EXISTS scim_email CASCADE;
DROP TABLE IF EXISTS scim_certificate CASCADE;
DROP TABLE IF EXISTS scim_address CASCADE;
DROP TABLE IF EXISTS database_scheme_version CASCADE;
DROP TABLE IF EXISTS osiam_client_scopes CASCADE;
DROP TABLE IF EXISTS osiam_client_grants CASCADE;
DROP TABLE IF EXISTS osiam_client CASCADE;
DROP TABLE IF EXISTS scim_extension_field_value CASCADE;
DROP TABLE IF EXISTS scim_extension_field CASCADE;
DROP TABLE IF EXISTS scim_extension CASCADE;
DROP TABLE IF EXISTS scim_user_scim_extension CASCADE;
DROP TABLE IF EXISTS hibernate_sequences CASCADE;
DROP SEQUENCE IF EXISTS hibernate_sequence CASCADE;


CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

CREATE TABLE database_scheme_version (
  version DOUBLE PRECISION NOT NULL
);


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE hibernate_sequence
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


--
-- This table is for the sequence strategy 'per table' for multiValue attributes
--
CREATE TABLE hibernate_sequences (
  SEQUENCE_NAME varchar(255),
  SEQUENCE_NEXT_HI_VALUE integer
);

--
-- Name: scim_address; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_address (
  id                               BIGINT NOT NULL,
  country                          TEXT,
  formatted                        TEXT,
  locality                         TEXT,
  postalcode                       TEXT,
  postgresql_does_not_like_primary BOOLEAN,
  region                           TEXT,
  streetaddress                    TEXT,
  type                             TEXT,
  user_internal_id                 BIGINT
);


--
-- Name: scim_certificate; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_certificate (
  multiValueId     BIGINT NOT NULL,
  value            TEXT   NOT NULL,
  user_internal_id BIGINT
);


--
-- Name: scim_email; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_email (
  multiValueId                     BIGINT NOT NULL,
  value                            TEXT   NOT NULL,
  postgresql_does_not_like_primary BOOLEAN,
  type                             TEXT,
  user_internal_id                 BIGINT NOT NULL
);


--
-- Name: scim_enterprise; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_enterprise (
  id             BIGINT NOT NULL,
  costcenter     TEXT,
  department     TEXT,
  division       TEXT,
  employeenumber TEXT,
  organization   TEXT,
  manager_id     BIGINT
);


--
-- Name: scim_entitlements; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_entitlements (
  multiValueId BIGINT NOT NULL,
  value        TEXT   NOT NULL
);


--
-- Name: scim_group; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_group (
  displayname TEXT   NOT NULL UNIQUE,
  internal_id BIGINT NOT NULL
);


CREATE TABLE osiam_client (
  internal_id                 BIGINT PRIMARY KEY,
  id                          VARCHAR(34) NOT NULL UNIQUE,
  redirect_uri                TEXT        NOT NULL UNIQUE,
  client_secret               TEXT        NOT NULL UNIQUE,
  accessTokenValiditySeconds  INT,
  refreshTokenValiditySeconds INT,
  validityInSeconds           BIGINT      NOT NULL,
  implicit_approval           BOOLEAN     NOT NULL,
  expiry                      TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE osiam_client_scopes (
  id    BIGINT REFERENCES osiam_client (internal_id),
  scope TEXT NOT NULL
);

CREATE TABLE osiam_client_grants (
  id     BIGINT REFERENCES osiam_client (internal_id),
  grants TEXT NOT NULL
);

CREATE TABLE scim_meta (
  id           BIGINT NOT NULL UNIQUE,
  created      TIMESTAMP WITHOUT TIME ZONE,
  lastmodified TIMESTAMP WITHOUT TIME ZONE,
  location     TEXT,
  version      TEXT,
  resourceType TEXT
);

CREATE TABLE scim_group_scim_id (
  groups_internal_id  BIGINT NOT NULL,
  members_internal_id BIGINT NOT NULL
);


--
-- Name: scim_id; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_id (
  internal_id BIGINT NOT NULL,
  externalid  TEXT UNIQUE,
  meta_id     BIGINT REFERENCES scim_meta (id),
  id          TEXT   NOT NULL
);


--
-- Name: scim_im; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_im (
  multiValueId     BIGINT NOT NULL,
  value            TEXT   NOT NULL,
  type             TEXT,
  user_internal_id BIGINT
);


--
-- Name: scim_manager; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_manager (
  id          BIGINT NOT NULL,
  displayname TEXT,
  managerid   BYTEA
);


--
-- Name: scim_name; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_name (
  id              BIGINT NOT NULL,
  familyname      TEXT,
  formatted       TEXT,
  givenname       TEXT,
  honorificprefix TEXT,
  honorificsuffix TEXT,
  middlename      TEXT
);


--
-- Name: scim_phonenumber; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_phonenumber (
  multiValueId     BIGINT NOT NULL,
  value            TEXT   NOT NULL,
  type             TEXT,
  user_internal_id BIGINT
);


--
-- Name: scim_photo; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_photo (
  multiValueId     BIGINT NOT NULL,
  value            TEXT   NOT NULL,
  type             TEXT,
  user_internal_id BIGINT
);


--
-- Name: scim_roles; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_roles (
  multiValueId BIGINT NOT NULL,
  value        TEXT   NOT NULL
);


--
-- Name: scim_user; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_user (
  active            BOOLEAN,
  displayname       TEXT,
  locale            TEXT,
  nickname          TEXT,
  password          TEXT   NOT NULL,
  preferredlanguage TEXT,
  profileurl        TEXT,
  timezone          TEXT,
  title             TEXT,
  username          TEXT   NOT NULL UNIQUE,
  usertype          TEXT,
  internal_id       BIGINT NOT NULL,
  name_id           BIGINT
);

--
-- Name: scim_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_user
ADD CONSTRAINT scim_user_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_user_scim_address; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_user_scim_address (
  scim_user_internal_id BIGINT NOT NULL,
  addresses_id          BIGINT NOT NULL
);


--
-- Name: scim_user_scim_entitlements; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_user_scim_entitlements (
  scim_user_internal_id     BIGINT NOT NULL,
  entitlements_multiValueId BIGINT NOT NULL
);


--
-- Name: scim_user_scim_roles; Type: TABLE; Schema: public; Owner: -; Tablespace:
--

CREATE TABLE scim_user_scim_roles (
  scim_user_internal_id BIGINT NOT NULL,
  roles_multiValueId    BIGINT NOT NULL
);


--
-- Extension section: table, constraints, indexes
--

CREATE TABLE scim_extension
(
  internal_id BIGINT                 NOT NULL,
  urn         CHARACTER VARYING(255) NOT NULL,
  CONSTRAINT scim_extension_pkey PRIMARY KEY (internal_id),
  CONSTRAINT urn_ UNIQUE (urn)
);

--
-- Extension field section: table, constraints, indexes
--

CREATE TABLE scim_extension_field
(
  internal_id           BIGINT NOT NULL,
  is_required           BOOLEAN,
  name                  CHARACTER VARYING(255),
  type                  CHARACTER VARYING(255),
  extension_internal_id BIGINT,
  CONSTRAINT scim_extension_field_pkey PRIMARY KEY (internal_id),
  CONSTRAINT fka8ad6a2f30b87d5d FOREIGN KEY (extension_internal_id)
  REFERENCES scim_extension (internal_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);
--
-- Extension field value section: table, constraints, indexes
--

CREATE TABLE scim_extension_field_value
(
  internal_id                 BIGINT NOT NULL,
  value                       TEXT   NOT NULL,
  extension_field_internal_id BIGINT NOT NULL,
  user_internal_id            BIGINT NOT NULL,
  CONSTRAINT scim_extension_field_value_pkey PRIMARY KEY (internal_id),
  CONSTRAINT fk6683bf6124a90fb9 FOREIGN KEY (extension_field_internal_id)
  REFERENCES scim_extension_field (internal_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk6683bf6146402c6a FOREIGN KEY (user_internal_id)
  REFERENCES scim_user (internal_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE scim_user_scim_extension
(
  scim_user_internal_id             BIGINT NOT NULL,
  registered_extensions_internal_id BIGINT NOT NULL,
  CONSTRAINT scim_user_scim_extension_pkey PRIMARY KEY (scim_user_internal_id, registered_extensions_internal_id),
  CONSTRAINT fk12ff42dd595826b4 FOREIGN KEY (registered_extensions_internal_id)
  REFERENCES scim_extension (internal_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk12ff42dd7e951dd5 FOREIGN KEY (scim_user_internal_id)
  REFERENCES scim_user (internal_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

--
-- Inserting default role, scheme version and updating table sequence for multivalues
--

INSERT INTO database_scheme_version VALUES (0.05);

INSERT INTO scim_roles VALUES (1, 'USER');

INSERT INTO hibernate_sequences VALUES ('MultiValueAttributeEntitySkeleton', 2);


--
-- Adding primary key constraint for scheme version table
--

ALTER TABLE ONLY database_scheme_version
ADD CONSTRAINT database_scheme_version_pkey PRIMARY KEY (version);


--
-- Name: scim_address_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_address
ADD CONSTRAINT scim_address_pkey PRIMARY KEY (id);


--
-- Name: scim_certificate_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_certificate
ADD CONSTRAINT scim_certificate_pkey PRIMARY KEY (multiValueId);


--
-- Name: scim_email_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_email
ADD CONSTRAINT scim_email_pkey PRIMARY KEY (multiValueId);


--
-- Name: scim_enterprise_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_enterprise
ADD CONSTRAINT scim_enterprise_pkey PRIMARY KEY (id);


--
-- Name: scim_entitlements_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_entitlements
ADD CONSTRAINT scim_entitlements_pkey PRIMARY KEY (multiValueId);


--
-- Name: scim_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_group
ADD CONSTRAINT scim_group_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_group_scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_group_scim_id
ADD CONSTRAINT scim_group_scim_id_pkey PRIMARY KEY (groups_internal_id, members_internal_id);


--
-- Name: scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_id
ADD CONSTRAINT scim_id_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_im_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_im
ADD CONSTRAINT scim_im_pkey PRIMARY KEY (multiValueId);


--
-- Name: scim_manager_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_manager
ADD CONSTRAINT scim_manager_pkey PRIMARY KEY (id);


--
-- Name: scim_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_meta
ADD CONSTRAINT scim_meta_pkey PRIMARY KEY (id);


--
-- Name: scim_name_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_name
ADD CONSTRAINT scim_name_pkey PRIMARY KEY (id);


--
-- Name: scim_phonenumber_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_phonenumber
ADD CONSTRAINT scim_phonenumber_pkey PRIMARY KEY (multiValueId);


--
-- Name: scim_photo_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_photo
ADD CONSTRAINT scim_photo_pkey PRIMARY KEY (multiValueId);


--
-- Name: scim_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_roles
ADD CONSTRAINT scim_roles_pkey PRIMARY KEY (multiValueId);


--
-- Name: scim_user_scim_address_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_user_scim_address
ADD CONSTRAINT scim_user_scim_address_pkey PRIMARY KEY (scim_user_internal_id, addresses_id);


--
-- Name: scim_user_scim_entitlements_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_user_scim_entitlements
ADD CONSTRAINT scim_user_scim_entitlements_pkey PRIMARY KEY (scim_user_internal_id, entitlements_multiValueId);


--
-- Name: scim_user_scim_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace:
--

ALTER TABLE ONLY scim_user_scim_roles
ADD CONSTRAINT scim_user_scim_roles_pkey PRIMARY KEY (scim_user_internal_id, roles_multiValueId);


--
-- Name: fk2d322588abdb6640; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user_scim_entitlements
ADD CONSTRAINT fk2d322588abdb6640 FOREIGN KEY (scim_user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fk2d322588ef67251f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user_scim_entitlements
ADD CONSTRAINT fk2d322588ef67251f FOREIGN KEY (entitlements_multiValueId) REFERENCES scim_entitlements (multiValueId);


--
-- Name: fk340ed212353ef531; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user_scim_address
ADD CONSTRAINT fk340ed212353ef531 FOREIGN KEY (addresses_id) REFERENCES scim_address (id);


--
-- Name: fk340ed212abdb6640; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user_scim_address
ADD CONSTRAINT fk340ed212abdb6640 FOREIGN KEY (scim_user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fk38b265b627b5137b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
ADD CONSTRAINT fk38b265b627b5137b FOREIGN KEY (name_id) REFERENCES scim_name (id);


--
-- Name: fk38b265b6e9e686e0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
ADD CONSTRAINT fk38b265b6e9e686e0 FOREIGN KEY (internal_id) REFERENCES scim_id (internal_id);

--
-- Name: fk70e4b45babdb6640; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user_scim_roles
ADD CONSTRAINT fk70e4b45babdb6640 FOREIGN KEY (scim_user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fk70e4b45be638e451; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user_scim_roles
ADD CONSTRAINT fk70e4b45be638e451 FOREIGN KEY (roles_multiValueId) REFERENCES scim_roles (multiValueId);


--
-- Name: fk725705cf738674d5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
ADD CONSTRAINT fk725705cf738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fk8d2c327b6d23d136; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
ADD CONSTRAINT fk8d2c327b6d23d136 FOREIGN KEY (groups_internal_id) REFERENCES scim_group (internal_id);


--
-- Name: fk8d2c327bc347d0ba; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
ADD CONSTRAINT fk8d2c327bc347d0ba FOREIGN KEY (members_internal_id) REFERENCES scim_id (internal_id);


--
-- Name: fk956dd94c738674d5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
ADD CONSTRAINT fk956dd94c738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fka4a85629738674d5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
ADD CONSTRAINT fka4a85629738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fkd9f3520c738674d5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
ADD CONSTRAINT fkd9f3520c738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fkdcb60f11738674d5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
ADD CONSTRAINT fkdcb60f11738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fkdcd4b9f4e9e686e0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
ADD CONSTRAINT fkdcd4b9f4e9e686e0 FOREIGN KEY (internal_id) REFERENCES scim_id (internal_id);


--
-- Name: fkdd4f01a7738674d5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
ADD CONSTRAINT fkdd4f01a7738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- Name: fke1bc510cae52e63f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_enterprise
ADD CONSTRAINT fke1bc510cae52e63f FOREIGN KEY (manager_id) REFERENCES scim_manager (id);
