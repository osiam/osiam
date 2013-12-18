drop table if exists database_scheme_version cascade;
drop table if exists osiam_client_scopes cascade;
drop table if exists osiam_client_grants cascade;
drop table if exists osiam_client cascade;
drop table if exists scim_group_scim_id cascade;
drop table if exists scim_extension_field_value cascade;
drop table if exists scim_im cascade;
drop table if exists scim_phonenumber cascade;
drop table if exists scim_photo cascade;
drop table if exists scim_name cascade;
drop table if exists scim_entitlements cascade;
drop table if exists scim_address cascade;
drop table if exists scim_roles cascade;
drop table if exists scim_certificate cascade;
drop table if exists scim_email cascade;
drop table if exists scim_user cascade;
drop table if exists scim_group cascade;
drop table if exists scim_extension_field cascade;
drop table if exists scim_extension cascade;
drop table if exists scim_meta cascade;
drop table if exists scim_id cascade;
drop table if exists scim_manager cascade;
drop table if exists scim_enterprise cascade;
drop table if exists hibernate_sequences cascade;
drop sequence if exists hibernate_sequence cascade;


CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

--
-- Name: database_scheme_version; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE database_scheme_version (
    version double precision NOT NULL
);


--
-- Inserting schema version
--

INSERT INTO database_scheme_version VALUES (0.05);


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
-- Name: hibernate_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE hibernate_sequences (
    sequence_name character varying(255),
    sequence_next_hi_value integer
);


--
-- Name: osiam_client; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE osiam_client (
    internal_id bigint NOT NULL,
    accesstokenvalidityseconds integer NOT NULL,
    client_secret text NOT NULL,
    expiry timestamp without time zone,
    id character varying(32) NOT NULL,
    implicit_approval boolean NOT NULL,
    redirect_uri text NOT NULL,
    refreshtokenvalidityseconds integer NOT NULL,
    validityinseconds bigint NOT NULL
);


--
-- Name: osiam_client_grants; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE osiam_client_grants (
    id bigint NOT NULL,
    grants text
);


--
-- Name: osiam_client_scopes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE osiam_client_scopes (
    id bigint NOT NULL,
    scope text
);


--
-- Name: scim_address; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_address (
    id bigint NOT NULL,
    country text,
    formatted text,
    locality text,
    postalcode text,
    postgresql_does_not_like_primary boolean,
    region text,
    streetaddress text,
    type text,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_certificate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_certificate (
    multi_value_id bigint NOT NULL,
    value text,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_email; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_email (
    multi_value_id bigint NOT NULL,
    value text,
    postgresql_does_not_like_primary boolean,
    type text,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_enterprise; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_enterprise (
    id bigint NOT NULL,
    costcenter text,
    department text,
    division text,
    employeenumber text,
    organization text,
    manager_id bigint
);


--
-- Name: scim_entitlements; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_entitlements (
    multi_value_id bigint NOT NULL,
    value text,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_extension; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension (
    internal_id bigint NOT NULL,
    urn text NOT NULL
);


--
-- Name: scim_extension_field; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension_field (
    internal_id bigint NOT NULL,
    is_required boolean,
    name text,
    type text,
    extension_internal_id bigint
);


--
-- Name: scim_extension_field_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension_field_value (
    internal_id bigint NOT NULL,
    value text NOT NULL,
    extension_field_internal_id bigint NOT NULL,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_group (
    displayname text NOT NULL,
    internal_id bigint NOT NULL
);


--
-- Name: scim_group_scim_id; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_group_scim_id (
    groups_internal_id bigint NOT NULL,
    members_internal_id bigint NOT NULL
);


--
-- Name: scim_id; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_id (
    internal_id bigint NOT NULL,
    externalid text,
    id text NOT NULL,
    meta_id bigint
);


--
-- Name: scim_im; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_im (
    multi_value_id bigint NOT NULL,
    value text,
    type text,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_manager; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_manager (
    id bigint NOT NULL,
    displayname text,
    managerid bytea
);


--
-- Name: scim_meta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_meta (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastmodified timestamp without time zone,
    location text,
    resourcetype text,
    version text
);


--
-- Name: scim_name; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_name (
    id bigint NOT NULL,
    familyname text,
    formatted text,
    givenname text,
    honorificprefix text,
    honorificsuffix text,
    middlename text
);


--
-- Name: scim_phonenumber; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_phonenumber (
    multi_value_id bigint NOT NULL,
    value text,
    type text,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_photo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_photo (
    multi_value_id bigint NOT NULL,
    value text,
    type text,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_roles (
    multi_value_id bigint NOT NULL,
    value text,
    user_internal_id bigint NOT NULL
);


--
-- Name: scim_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_user (
    active boolean,
    displayname text,
    locale text,
    nickname text,
    password text NOT NULL,
    preferredlanguage text,
    profileurl text,
    timezone text,
    title text,
    username text NOT NULL,
    usertype text,
    internal_id bigint NOT NULL,
    name_id bigint
);


--
-- Name: database_scheme_version_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY database_scheme_version
    ADD CONSTRAINT database_scheme_version_pkey PRIMARY KEY (version);


--
-- Name: osiam_client_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT osiam_client_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_address_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
    ADD CONSTRAINT scim_address_pkey PRIMARY KEY (id);


--
-- Name: scim_certificate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
    ADD CONSTRAINT scim_certificate_pkey PRIMARY KEY (multi_value_id);


--
-- Name: scim_email_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
    ADD CONSTRAINT scim_email_pkey PRIMARY KEY (multi_value_id);


--
-- Name: scim_enterprise_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_enterprise
    ADD CONSTRAINT scim_enterprise_pkey PRIMARY KEY (id);


--
-- Name: scim_entitlements_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_entitlements
    ADD CONSTRAINT scim_entitlements_pkey PRIMARY KEY (multi_value_id);


--
-- Name: scim_extension_field_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
    ADD CONSTRAINT scim_extension_field_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_extension_field_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT scim_extension_field_value_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_extension_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension
    ADD CONSTRAINT scim_extension_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT scim_group_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_group_scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT scim_group_scim_id_pkey PRIMARY KEY (groups_internal_id, members_internal_id);


--
-- Name: scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT scim_id_pkey PRIMARY KEY (internal_id);


--
-- Name: scim_im_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
    ADD CONSTRAINT scim_im_pkey PRIMARY KEY (multi_value_id);


--
-- Name: scim_manager_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_manager
    ADD CONSTRAINT scim_manager_pkey PRIMARY KEY (id);


--
-- Name: scim_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_meta
    ADD CONSTRAINT scim_meta_pkey PRIMARY KEY (id);


--
-- Name: scim_name_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_name
    ADD CONSTRAINT scim_name_pkey PRIMARY KEY (id);


--
-- Name: scim_phonenumber_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
    ADD CONSTRAINT scim_phonenumber_pkey PRIMARY KEY (multi_value_id);


--
-- Name: scim_photo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
    ADD CONSTRAINT scim_photo_pkey PRIMARY KEY (multi_value_id);


--
-- Name: scim_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_roles
    ADD CONSTRAINT scim_roles_pkey PRIMARY KEY (multi_value_id);


--
-- Name: scim_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT scim_user_pkey PRIMARY KEY (internal_id);


--
-- Name: uk_164dcfif0r82xubvindi9vrnc; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT uk_164dcfif0r82xubvindi9vrnc UNIQUE (externalid);


--
-- Name: uk_1dt64mbf4gp83rwy18jofwwf; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT uk_1dt64mbf4gp83rwy18jofwwf UNIQUE (displayname);


--
-- Name: uk_1onynolltgwuk8a5ngjhkqcl1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT uk_1onynolltgwuk8a5ngjhkqcl1 UNIQUE (username);


--
-- Name: uk_60sysrrwavtwwnji8nw5tng2x; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension
    ADD CONSTRAINT uk_60sysrrwavtwwnji8nw5tng2x UNIQUE (urn);


--
-- Name: uk_c34iilt4h1ln91s9ro8m96hru; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT uk_c34iilt4h1ln91s9ro8m96hru UNIQUE (id);


--
-- Name: uk_jj3o15pxbkaf4p88paf4l6ax0; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT uk_jj3o15pxbkaf4p88paf4l6ax0 UNIQUE (redirect_uri);


--
-- Name: uk_ktjxo7vyfs0veopytnh1x68sm; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT uk_ktjxo7vyfs0veopytnh1x68sm UNIQUE (client_secret);


--
-- Name: uk_q4ya5m8v6tafgtvw1inqtmm42; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT uk_q4ya5m8v6tafgtvw1inqtmm42 UNIQUE (id);


--
-- Name: fk_6y0v7g2y69nkvody9jv5q3tuo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT fk_6y0v7g2y69nkvody9jv5q3tuo FOREIGN KEY (extension_field_internal_id) REFERENCES scim_extension_field(internal_id);


--
-- Name: fk_7jnl5vqcfg1j9plj4py1qvxcp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_entitlements
    ADD CONSTRAINT fk_7jnl5vqcfg1j9plj4py1qvxcp FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- Name: fk_b29y2qc2j5uu49wa9grpbulb0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT fk_b29y2qc2j5uu49wa9grpbulb0 FOREIGN KEY (members_internal_id) REFERENCES scim_id(internal_id);


--
-- Name: fk_byxttqfbmb2wcj4ud3hd53mw3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT fk_byxttqfbmb2wcj4ud3hd53mw3 FOREIGN KEY (meta_id) REFERENCES scim_meta(id);


--
-- Name: fk_ctvkl0udnj6jpn1p93vbwywte; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client_grants
    ADD CONSTRAINT fk_ctvkl0udnj6jpn1p93vbwywte FOREIGN KEY (id) REFERENCES osiam_client(internal_id);


--
-- Name: fk_d2ji7ipe62fbg8uu2ir7b9ls4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT fk_d2ji7ipe62fbg8uu2ir7b9ls4 FOREIGN KEY (name_id) REFERENCES scim_name(id);


--
-- Name: fk_dmfj3s46npn4p1pcrc3iur2mp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
    ADD CONSTRAINT fk_dmfj3s46npn4p1pcrc3iur2mp FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- Name: fk_eksek96tmtxkaqe5a7hfmoswo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
    ADD CONSTRAINT fk_eksek96tmtxkaqe5a7hfmoswo FOREIGN KEY (extension_internal_id) REFERENCES scim_extension(internal_id);


--
-- Name: fk_gct22972jrrv22crorixfdlmi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT fk_gct22972jrrv22crorixfdlmi FOREIGN KEY (groups_internal_id) REFERENCES scim_group(internal_id);


--
-- Name: fk_ghdpgmh1b8suimtfxdl8653bj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
    ADD CONSTRAINT fk_ghdpgmh1b8suimtfxdl8653bj FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- Name: fk_gl93uw092wua8dl5cpb5ysn3f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client_scopes
    ADD CONSTRAINT fk_gl93uw092wua8dl5cpb5ysn3f FOREIGN KEY (id) REFERENCES osiam_client(internal_id);


--
-- Name: fk_hmsah9dinhk7f8k4lf50h658; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
    ADD CONSTRAINT fk_hmsah9dinhk7f8k4lf50h658 FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- Name: fk_in6gs4safpkntvac3v88ke54r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT fk_in6gs4safpkntvac3v88ke54r FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- Name: fk_jxkq2wka34t20eejcvycluyr6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_enterprise
    ADD CONSTRAINT fk_jxkq2wka34t20eejcvycluyr6 FOREIGN KEY (manager_id) REFERENCES scim_manager(id);


--
-- Name: fk_n5und6lnrtblhgs2ococpglyi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_roles
    ADD CONSTRAINT fk_n5und6lnrtblhgs2ococpglyi FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- Name: fk_nx0839hyqd5yrfelxkr2fpr7a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT fk_nx0839hyqd5yrfelxkr2fpr7a FOREIGN KEY (internal_id) REFERENCES scim_id(internal_id);


--
-- Name: fk_oari88x9o5j9jmigtt5s20m4k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT fk_oari88x9o5j9jmigtt5s20m4k FOREIGN KEY (internal_id) REFERENCES scim_id(internal_id);


--
-- Name: fk_q3rk61yla08pvod7gq8av7i0l; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
    ADD CONSTRAINT fk_q3rk61yla08pvod7gq8av7i0l FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- Name: fk_qr6gtqi0h9r6yp034tarlry1k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
    ADD CONSTRAINT fk_qr6gtqi0h9r6yp034tarlry1k FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- Name: fk_rpqvdf1p9twdigaq1wclu5wm8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
    ADD CONSTRAINT fk_rpqvdf1p9twdigaq1wclu5wm8 FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);
