--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.9
-- Dumped by pg_dump version 9.1.11
-- Started on 2014-02-04 16:21:10 CET

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 185 (class 3079 OID 11647)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

SET search_path = public, pg_catalog;

SET default_with_oids = false;

--
-- TOC entry 161 (class 1259 OID 17809)
-- Dependencies: 5
-- Name: database_scheme_version; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE database_scheme_version (
    version double precision NOT NULL
);


--
-- TOC entry 184 (class 1259 OID 18081)
-- Dependencies: 5
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 162 (class 1259 OID 17814)
-- Dependencies: 5
-- Name: osiam_client; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE osiam_client (
    internal_id bigint NOT NULL,
    accesstokenvalidityseconds integer NOT NULL,
    client_secret character varying(255) NOT NULL,
    expiry timestamp without time zone,
    id character varying(32) NOT NULL,
    implicit_approval boolean NOT NULL,
    redirect_uri text NOT NULL,
    refreshtokenvalidityseconds integer NOT NULL,
    validityinseconds bigint NOT NULL
);


--
-- TOC entry 163 (class 1259 OID 17822)
-- Dependencies: 5
-- Name: osiam_client_grants; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE osiam_client_grants (
    id bigint NOT NULL,
    grants character varying(255)
);


--
-- TOC entry 164 (class 1259 OID 17825)
-- Dependencies: 5
-- Name: osiam_client_scopes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE osiam_client_scopes (
    id bigint NOT NULL,
    scope character varying(255)
);


--
-- TOC entry 165 (class 1259 OID 17828)
-- Dependencies: 5
-- Name: scim_address; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_address (
    multi_value_id bigint NOT NULL,
    is_primary boolean,
    country character varying(255),
    formatted text,
    locality character varying(255),
    postalcode character varying(255),
    region character varying(255),
    streetaddress character varying(255),
    type character varying(255),
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 166 (class 1259 OID 17836)
-- Dependencies: 5
-- Name: scim_certificate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_certificate (
    multi_value_id bigint NOT NULL,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 167 (class 1259 OID 17844)
-- Dependencies: 5
-- Name: scim_email; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_email (
    multi_value_id bigint NOT NULL,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 168 (class 1259 OID 17852)
-- Dependencies: 5
-- Name: scim_enterprise; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_enterprise (
    id bigint NOT NULL,
    costcenter character varying(255),
    department character varying(255),
    division character varying(255),
    employeenumber character varying(255),
    organization character varying(255),
    manager_id bigint
);


--
-- TOC entry 169 (class 1259 OID 17857)
-- Dependencies: 5
-- Name: scim_entitlements; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_entitlements (
    multi_value_id bigint NOT NULL,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 170 (class 1259 OID 17865)
-- Dependencies: 5
-- Name: scim_extension; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension (
    internal_id bigint NOT NULL,
    urn text NOT NULL
);


--
-- TOC entry 171 (class 1259 OID 17873)
-- Dependencies: 5
-- Name: scim_extension_field; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension_field (
    internal_id bigint NOT NULL,
    name character varying(255),
    is_required boolean,
    type character varying(255) NOT NULL,
    extension_internal_id bigint
);


--
-- TOC entry 172 (class 1259 OID 17878)
-- Dependencies: 5
-- Name: scim_extension_field_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension_field_value (
    internal_id bigint NOT NULL,
    value text NOT NULL,
    extension_field_internal_id bigint NOT NULL,
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 173 (class 1259 OID 17886)
-- Dependencies: 5
-- Name: scim_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_group (
    displayname character varying(255) NOT NULL,
    internal_id bigint NOT NULL
);


--
-- TOC entry 174 (class 1259 OID 17891)
-- Dependencies: 5
-- Name: scim_group_scim_id; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_group_scim_id (
    groups_internal_id bigint NOT NULL,
    members_internal_id bigint NOT NULL
);


--
-- TOC entry 175 (class 1259 OID 17896)
-- Dependencies: 5
-- Name: scim_id; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_id (
    internal_id bigint NOT NULL,
    externalid character varying(255),
    id character varying(255) NOT NULL,
    meta_id bigint
);


--
-- TOC entry 176 (class 1259 OID 17901)
-- Dependencies: 5
-- Name: scim_im; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_im (
    multi_value_id bigint NOT NULL,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 177 (class 1259 OID 17909)
-- Dependencies: 5
-- Name: scim_manager; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_manager (
    id bigint NOT NULL,
    displayname character varying(255),
    managerid bytea
);


--
-- TOC entry 178 (class 1259 OID 17917)
-- Dependencies: 5
-- Name: scim_meta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_meta (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastmodified timestamp without time zone,
    location text,
    resourcetype character varying(255),
    version character varying(255)
);


--
-- TOC entry 179 (class 1259 OID 17925)
-- Dependencies: 5
-- Name: scim_name; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_name (
    id bigint NOT NULL,
    familyname character varying(255),
    formatted text,
    givenname character varying(255),
    honorificprefix character varying(255),
    honorificsuffix character varying(255),
    middlename character varying(255)
);


--
-- TOC entry 180 (class 1259 OID 17933)
-- Dependencies: 5
-- Name: scim_phonenumber; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_phonenumber (
    multi_value_id bigint NOT NULL,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 181 (class 1259 OID 17941)
-- Dependencies: 5
-- Name: scim_photo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_photo (
    multi_value_id bigint NOT NULL,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 182 (class 1259 OID 17949)
-- Dependencies: 5
-- Name: scim_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_roles (
    multi_value_id bigint NOT NULL,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL
);


--
-- TOC entry 183 (class 1259 OID 17957)
-- Dependencies: 5
-- Name: scim_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_user (
    active boolean,
    displayname character varying(255),
    locale character varying(255),
    nickname character varying(255),
    password character varying(255) NOT NULL,
    preferredlanguage character varying(255),
    profileurl text,
    timezone character varying(255),
    title character varying(255),
    username character varying(255) NOT NULL,
    usertype character varying(255),
    internal_id bigint NOT NULL,
    name_id bigint
);


--
-- TOC entry 1857 (class 2606 OID 17813)
-- Dependencies: 161 161 2036
-- Name: database_scheme_version_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY database_scheme_version
    ADD CONSTRAINT database_scheme_version_pkey PRIMARY KEY (version);


--
-- TOC entry 1859 (class 2606 OID 17821)
-- Dependencies: 162 162 2036
-- Name: osiam_client_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT osiam_client_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1867 (class 2606 OID 17835)
-- Dependencies: 165 165 2036
-- Name: scim_address_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
    ADD CONSTRAINT scim_address_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1869 (class 2606 OID 17843)
-- Dependencies: 166 166 2036
-- Name: scim_certificate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
    ADD CONSTRAINT scim_certificate_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1871 (class 2606 OID 17851)
-- Dependencies: 167 167 2036
-- Name: scim_email_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
    ADD CONSTRAINT scim_email_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1873 (class 2606 OID 17856)
-- Dependencies: 168 168 2036
-- Name: scim_enterprise_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_enterprise
    ADD CONSTRAINT scim_enterprise_pkey PRIMARY KEY (id);


--
-- TOC entry 1875 (class 2606 OID 17864)
-- Dependencies: 169 169 2036
-- Name: scim_entitlements_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_entitlements
    ADD CONSTRAINT scim_entitlements_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1881 (class 2606 OID 17877)
-- Dependencies: 171 171 2036
-- Name: scim_extension_field_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
    ADD CONSTRAINT scim_extension_field_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1883 (class 2606 OID 17885)
-- Dependencies: 172 172 2036
-- Name: scim_extension_field_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT scim_extension_field_value_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1877 (class 2606 OID 17872)
-- Dependencies: 170 170 2036
-- Name: scim_extension_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension
    ADD CONSTRAINT scim_extension_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1885 (class 2606 OID 17890)
-- Dependencies: 173 173 2036
-- Name: scim_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT scim_group_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1889 (class 2606 OID 17895)
-- Dependencies: 174 174 174 2036
-- Name: scim_group_scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT scim_group_scim_id_pkey PRIMARY KEY (groups_internal_id, members_internal_id);


--
-- TOC entry 1891 (class 2606 OID 17900)
-- Dependencies: 175 175 2036
-- Name: scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT scim_id_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1897 (class 2606 OID 17908)
-- Dependencies: 176 176 2036
-- Name: scim_im_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
    ADD CONSTRAINT scim_im_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1899 (class 2606 OID 17916)
-- Dependencies: 177 177 2036
-- Name: scim_manager_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_manager
    ADD CONSTRAINT scim_manager_pkey PRIMARY KEY (id);


--
-- TOC entry 1901 (class 2606 OID 17924)
-- Dependencies: 178 178 2036
-- Name: scim_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_meta
    ADD CONSTRAINT scim_meta_pkey PRIMARY KEY (id);


--
-- TOC entry 1903 (class 2606 OID 17932)
-- Dependencies: 179 179 2036
-- Name: scim_name_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_name
    ADD CONSTRAINT scim_name_pkey PRIMARY KEY (id);


--
-- TOC entry 1905 (class 2606 OID 17940)
-- Dependencies: 180 180 2036
-- Name: scim_phonenumber_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
    ADD CONSTRAINT scim_phonenumber_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1907 (class 2606 OID 17948)
-- Dependencies: 181 181 2036
-- Name: scim_photo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
    ADD CONSTRAINT scim_photo_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1909 (class 2606 OID 17956)
-- Dependencies: 182 182 2036
-- Name: scim_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_roles
    ADD CONSTRAINT scim_roles_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1911 (class 2606 OID 17964)
-- Dependencies: 183 183 2036
-- Name: scim_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT scim_user_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1893 (class 2606 OID 17976)
-- Dependencies: 175 175 2036
-- Name: uk_164dcfif0r82xubvindi9vrnc; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT uk_164dcfif0r82xubvindi9vrnc UNIQUE (externalid);


--
-- TOC entry 1887 (class 2606 OID 17974)
-- Dependencies: 173 173 2036
-- Name: uk_1dt64mbf4gp83rwy18jofwwf; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT uk_1dt64mbf4gp83rwy18jofwwf UNIQUE (displayname);


--
-- TOC entry 1913 (class 2606 OID 17980)
-- Dependencies: 183 183 2036
-- Name: uk_1onynolltgwuk8a5ngjhkqcl1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT uk_1onynolltgwuk8a5ngjhkqcl1 UNIQUE (username);


--
-- TOC entry 1879 (class 2606 OID 17972)
-- Dependencies: 170 170 2036
-- Name: uk_60sysrrwavtwwnji8nw5tng2x; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension
    ADD CONSTRAINT uk_60sysrrwavtwwnji8nw5tng2x UNIQUE (urn);


--
-- TOC entry 1861 (class 2606 OID 17968)
-- Dependencies: 162 162 2036
-- Name: uk_c34iilt4h1ln91s9ro8m96hru; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT uk_c34iilt4h1ln91s9ro8m96hru UNIQUE (id);


--
-- TOC entry 1863 (class 2606 OID 17970)
-- Dependencies: 162 162 2036
-- Name: uk_jj3o15pxbkaf4p88paf4l6ax0; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT uk_jj3o15pxbkaf4p88paf4l6ax0 UNIQUE (redirect_uri);


--
-- TOC entry 1865 (class 2606 OID 17966)
-- Dependencies: 162 162 2036
-- Name: uk_ktjxo7vyfs0veopytnh1x68sm; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT uk_ktjxo7vyfs0veopytnh1x68sm UNIQUE (client_secret);


--
-- TOC entry 1895 (class 2606 OID 17978)
-- Dependencies: 175 175 2036
-- Name: uk_q4ya5m8v6tafgtvw1inqtmm42; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT uk_q4ya5m8v6tafgtvw1inqtmm42 UNIQUE (id);


--
-- TOC entry 1922 (class 2606 OID 18021)
-- Dependencies: 1880 171 172 2036
-- Name: fk_6y0v7g2y69nkvody9jv5q3tuo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT fk_6y0v7g2y69nkvody9jv5q3tuo FOREIGN KEY (extension_field_internal_id) REFERENCES scim_extension_field(internal_id);


--
-- TOC entry 1920 (class 2606 OID 18011)
-- Dependencies: 183 1910 169 2036
-- Name: fk_7jnl5vqcfg1j9plj4py1qvxcp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_entitlements
    ADD CONSTRAINT fk_7jnl5vqcfg1j9plj4py1qvxcp FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1925 (class 2606 OID 18036)
-- Dependencies: 174 175 1890 2036
-- Name: fk_b29y2qc2j5uu49wa9grpbulb0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT fk_b29y2qc2j5uu49wa9grpbulb0 FOREIGN KEY (members_internal_id) REFERENCES scim_id(internal_id);


--
-- TOC entry 1927 (class 2606 OID 18046)
-- Dependencies: 178 1900 175 2036
-- Name: fk_byxttqfbmb2wcj4ud3hd53mw3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT fk_byxttqfbmb2wcj4ud3hd53mw3 FOREIGN KEY (meta_id) REFERENCES scim_meta(id);


--
-- TOC entry 1914 (class 2606 OID 17981)
-- Dependencies: 162 163 1858 2036
-- Name: fk_ctvkl0udnj6jpn1p93vbwywte; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client_grants
    ADD CONSTRAINT fk_ctvkl0udnj6jpn1p93vbwywte FOREIGN KEY (id) REFERENCES osiam_client(internal_id);


--
-- TOC entry 1932 (class 2606 OID 18071)
-- Dependencies: 183 1902 179 2036
-- Name: fk_d2ji7ipe62fbg8uu2ir7b9ls4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT fk_d2ji7ipe62fbg8uu2ir7b9ls4 FOREIGN KEY (name_id) REFERENCES scim_name(id);


--
-- TOC entry 1918 (class 2606 OID 18001)
-- Dependencies: 1910 183 167 2036
-- Name: fk_dmfj3s46npn4p1pcrc3iur2mp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
    ADD CONSTRAINT fk_dmfj3s46npn4p1pcrc3iur2mp FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1921 (class 2606 OID 18016)
-- Dependencies: 171 170 1876 2036
-- Name: fk_eksek96tmtxkaqe5a7hfmoswo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
    ADD CONSTRAINT fk_eksek96tmtxkaqe5a7hfmoswo FOREIGN KEY (extension_internal_id) REFERENCES scim_extension(internal_id);


--
-- TOC entry 1926 (class 2606 OID 18041)
-- Dependencies: 173 1884 174 2036
-- Name: fk_gct22972jrrv22crorixfdlmi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT fk_gct22972jrrv22crorixfdlmi FOREIGN KEY (groups_internal_id) REFERENCES scim_group(internal_id);


--
-- TOC entry 1917 (class 2606 OID 17996)
-- Dependencies: 1910 166 183 2036
-- Name: fk_ghdpgmh1b8suimtfxdl8653bj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
    ADD CONSTRAINT fk_ghdpgmh1b8suimtfxdl8653bj FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1915 (class 2606 OID 17986)
-- Dependencies: 1858 164 162 2036
-- Name: fk_gl93uw092wua8dl5cpb5ysn3f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client_scopes
    ADD CONSTRAINT fk_gl93uw092wua8dl5cpb5ysn3f FOREIGN KEY (id) REFERENCES osiam_client(internal_id);


--
-- TOC entry 1928 (class 2606 OID 18051)
-- Dependencies: 183 1910 176 2036
-- Name: fk_hmsah9dinhk7f8k4lf50h658; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
    ADD CONSTRAINT fk_hmsah9dinhk7f8k4lf50h658 FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1923 (class 2606 OID 18026)
-- Dependencies: 1910 172 183 2036
-- Name: fk_in6gs4safpkntvac3v88ke54r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT fk_in6gs4safpkntvac3v88ke54r FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1919 (class 2606 OID 18006)
-- Dependencies: 177 168 1898 2036
-- Name: fk_jxkq2wka34t20eejcvycluyr6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_enterprise
    ADD CONSTRAINT fk_jxkq2wka34t20eejcvycluyr6 FOREIGN KEY (manager_id) REFERENCES scim_manager(id);


--
-- TOC entry 1931 (class 2606 OID 18066)
-- Dependencies: 183 182 1910 2036
-- Name: fk_n5und6lnrtblhgs2ococpglyi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_roles
    ADD CONSTRAINT fk_n5und6lnrtblhgs2ococpglyi FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1933 (class 2606 OID 18076)
-- Dependencies: 1890 175 183 2036
-- Name: fk_nx0839hyqd5yrfelxkr2fpr7a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT fk_nx0839hyqd5yrfelxkr2fpr7a FOREIGN KEY (internal_id) REFERENCES scim_id(internal_id);


--
-- TOC entry 1924 (class 2606 OID 18031)
-- Dependencies: 1890 173 175 2036
-- Name: fk_oari88x9o5j9jmigtt5s20m4k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT fk_oari88x9o5j9jmigtt5s20m4k FOREIGN KEY (internal_id) REFERENCES scim_id(internal_id);


--
-- TOC entry 1930 (class 2606 OID 18061)
-- Dependencies: 181 183 1910 2036
-- Name: fk_q3rk61yla08pvod7gq8av7i0l; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
    ADD CONSTRAINT fk_q3rk61yla08pvod7gq8av7i0l FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1916 (class 2606 OID 17991)
-- Dependencies: 1910 183 165 2036
-- Name: fk_qr6gtqi0h9r6yp034tarlry1k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
    ADD CONSTRAINT fk_qr6gtqi0h9r6yp034tarlry1k FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1929 (class 2606 OID 18056)
-- Dependencies: 180 1910 183 2036
-- Name: fk_rpqvdf1p9twdigaq1wclu5wm8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
    ADD CONSTRAINT fk_rpqvdf1p9twdigaq1wclu5wm8 FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


-- Completed on 2014-02-04 16:21:11 CET

--
-- PostgreSQL database dump complete
--

