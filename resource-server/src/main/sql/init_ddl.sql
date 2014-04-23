--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.11
-- Dumped by pg_dump version 9.1.11
-- Started on 2014-02-14 16:20:06 CET

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET default_with_oids = false;

--
-- TOC entry 184 (class 1259 OID 34929)
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
-- TOC entry 165 (class 1259 OID 34639)
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
-- TOC entry 166 (class 1259 OID 34647)
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
-- TOC entry 167 (class 1259 OID 34655)
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
-- TOC entry 168 (class 1259 OID 34663)
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
-- TOC entry 169 (class 1259 OID 34671)
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
-- TOC entry 170 (class 1259 OID 34679)
-- Dependencies: 5
-- Name: scim_extension; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension (
    internal_id bigint NOT NULL,
    urn text NOT NULL
);


--
-- TOC entry 171 (class 1259 OID 34687)
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
-- TOC entry 172 (class 1259 OID 34695)
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
-- TOC entry 173 (class 1259 OID 34703)
-- Dependencies: 5
-- Name: scim_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_group (
    displayname character varying(255) NOT NULL,
    internal_id bigint NOT NULL
);


--
-- TOC entry 174 (class 1259 OID 34708)
-- Dependencies: 5
-- Name: scim_group_scim_id; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_group_scim_id (
    groups_internal_id bigint NOT NULL,
    members_internal_id bigint NOT NULL
);


--
-- TOC entry 175 (class 1259 OID 34713)
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
-- TOC entry 176 (class 1259 OID 34721)
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
-- TOC entry 177 (class 1259 OID 34729)
-- Dependencies: 5
-- Name: scim_manager; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_manager (
    id bigint NOT NULL,
    displayname character varying(255),
    managerid bytea
);


--
-- TOC entry 178 (class 1259 OID 34737)
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
-- TOC entry 179 (class 1259 OID 34745)
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
-- TOC entry 180 (class 1259 OID 34753)
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
-- TOC entry 181 (class 1259 OID 34761)
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
-- TOC entry 182 (class 1259 OID 34769)
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
-- TOC entry 183 (class 1259 OID 34777)
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
-- TOC entry 1914 (class 2606 OID 34646)
-- Dependencies: 165 165 2111
-- Name: scim_address_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
    ADD CONSTRAINT scim_address_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1918 (class 2606 OID 34654)
-- Dependencies: 166 166 2111
-- Name: scim_certificate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
    ADD CONSTRAINT scim_certificate_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1923 (class 2606 OID 34662)
-- Dependencies: 167 167 2111
-- Name: scim_email_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
    ADD CONSTRAINT scim_email_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1928 (class 2606 OID 34670)
-- Dependencies: 168 168 2111
-- Name: scim_enterprise_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_enterprise
    ADD CONSTRAINT scim_enterprise_pkey PRIMARY KEY (id);


--
-- TOC entry 1930 (class 2606 OID 34678)
-- Dependencies: 169 169 2111
-- Name: scim_entitlements_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_entitlements
    ADD CONSTRAINT scim_entitlements_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1939 (class 2606 OID 34694)
-- Dependencies: 171 171 2111
-- Name: scim_extension_field_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
    ADD CONSTRAINT scim_extension_field_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1943 (class 2606 OID 34702)
-- Dependencies: 172 172 2111
-- Name: scim_extension_field_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT scim_extension_field_value_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1935 (class 2606 OID 34686)
-- Dependencies: 170 170 2111
-- Name: scim_extension_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension
    ADD CONSTRAINT scim_extension_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1946 (class 2606 OID 34707)
-- Dependencies: 173 173 2111
-- Name: scim_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT scim_group_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1950 (class 2606 OID 34712)
-- Dependencies: 174 174 174 2111
-- Name: scim_group_scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT scim_group_scim_id_pkey PRIMARY KEY (groups_internal_id, members_internal_id);


--
-- TOC entry 1952 (class 2606 OID 34720)
-- Dependencies: 175 175 2111
-- Name: scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT scim_id_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1958 (class 2606 OID 34728)
-- Dependencies: 176 176 2111
-- Name: scim_im_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
    ADD CONSTRAINT scim_im_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1963 (class 2606 OID 34736)
-- Dependencies: 177 177 2111
-- Name: scim_manager_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_manager
    ADD CONSTRAINT scim_manager_pkey PRIMARY KEY (id);


--
-- TOC entry 1965 (class 2606 OID 34744)
-- Dependencies: 178 178 2111
-- Name: scim_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_meta
    ADD CONSTRAINT scim_meta_pkey PRIMARY KEY (id);


--
-- TOC entry 1969 (class 2606 OID 34752)
-- Dependencies: 179 179 2111
-- Name: scim_name_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_name
    ADD CONSTRAINT scim_name_pkey PRIMARY KEY (id);


--
-- TOC entry 1971 (class 2606 OID 34760)
-- Dependencies: 180 180 2111
-- Name: scim_phonenumber_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
    ADD CONSTRAINT scim_phonenumber_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1976 (class 2606 OID 34768)
-- Dependencies: 181 181 2111
-- Name: scim_photo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
    ADD CONSTRAINT scim_photo_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1981 (class 2606 OID 34776)
-- Dependencies: 182 182 2111
-- Name: scim_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_roles
    ADD CONSTRAINT scim_roles_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 1986 (class 2606 OID 34784)
-- Dependencies: 183 183 2111
-- Name: scim_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT scim_user_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 1954 (class 2606 OID 34810)
-- Dependencies: 175 175 2111
-- Name: uk_164dcfif0r82xubvindi9vrnc; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT uk_164dcfif0r82xubvindi9vrnc UNIQUE (externalid);


--
-- TOC entry 1948 (class 2606 OID 34808)
-- Dependencies: 173 173 2111
-- Name: uk_1dt64mbf4gp83rwy18jofwwf; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT uk_1dt64mbf4gp83rwy18jofwwf UNIQUE (displayname);


--
-- TOC entry 1988 (class 2606 OID 34828)
-- Dependencies: 183 183 2111
-- Name: uk_1onynolltgwuk8a5ngjhkqcl1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT uk_1onynolltgwuk8a5ngjhkqcl1 UNIQUE (username);


--
-- TOC entry 1937 (class 2606 OID 34803)
-- Dependencies: 170 170 2111
-- Name: uk_60sysrrwavtwwnji8nw5tng2x; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension
    ADD CONSTRAINT uk_60sysrrwavtwwnji8nw5tng2x UNIQUE (urn);


--
-- TOC entry 1941 (class 2606 OID 34805)
-- Dependencies: 171 171 171 2111
-- Name: uk_9rvm7w04q503y4gx9q0c55cnv; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
    ADD CONSTRAINT uk_9rvm7w04q503y4gx9q0c55cnv UNIQUE (extension_internal_id, name);


--
-- TOC entry 1956 (class 2606 OID 34812)
-- Dependencies: 175 175 2111
-- Name: uk_q4ya5m8v6tafgtvw1inqtmm42; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT uk_q4ya5m8v6tafgtvw1inqtmm42 UNIQUE (id);


--
-- TOC entry 1966 (class 1259 OID 34817)
-- Dependencies: 178 2111
-- Name: uk_1b0o2foyw6nainc2vrssxkok0; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_1b0o2foyw6nainc2vrssxkok0 ON scim_meta USING btree (lastmodified);


--
-- TOC entry 1977 (class 1259 OID 34822)
-- Dependencies: 181 2111
-- Name: uk_1er38kw2ith4ewuf7b5rhh7br; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_1er38kw2ith4ewuf7b5rhh7br ON scim_photo USING btree (type);


--
-- TOC entry 1967 (class 1259 OID 34816)
-- Dependencies: 178 2111
-- Name: uk_1o8kevc2e2hfk24f19j3vcia4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_1o8kevc2e2hfk24f19j3vcia4 ON scim_meta USING btree (created);


--
-- TOC entry 1959 (class 1259 OID 34813)
-- Dependencies: 176 2111
-- Name: uk_31njuvoulynkorup0b5pjqni6; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_31njuvoulynkorup0b5pjqni6 ON scim_im USING btree (value);


--
-- TOC entry 1915 (class 1259 OID 34792)
-- Dependencies: 165 165 165 165 165 2111
-- Name: uk_3hqwl74jwjq0dksv2t4iqlptm; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_3hqwl74jwjq0dksv2t4iqlptm ON scim_address USING btree (country, region, locality, postalcode, streetaddress);


--
-- TOC entry 1978 (class 1259 OID 34821)
-- Dependencies: 181 2111
-- Name: uk_6y89p0fpcdcg2fq9k5u8h1173; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_6y89p0fpcdcg2fq9k5u8h1173 ON scim_photo USING btree (value);


--
-- TOC entry 1931 (class 1259 OID 34800)
-- Dependencies: 169 2111
-- Name: uk_75wo1phhovp2nbruh2dmfhcwk; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_75wo1phhovp2nbruh2dmfhcwk ON scim_entitlements USING btree (type);


--
-- TOC entry 1919 (class 1259 OID 34794)
-- Dependencies: 166 2111
-- Name: uk_7k7tc0du5jucy4ranqn8uid4b; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_7k7tc0du5jucy4ranqn8uid4b ON scim_certificate USING btree (type);


--
-- TOC entry 1960 (class 1259 OID 34814)
-- Dependencies: 176 2111
-- Name: uk_88yyj57g5nisgp2trhs2yqa91; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_88yyj57g5nisgp2trhs2yqa91 ON scim_im USING btree (type);


--
-- TOC entry 1982 (class 1259 OID 34825)
-- Dependencies: 182 2111
-- Name: uk_8qwt29ewjm8urpi7vk10q2fb3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_8qwt29ewjm8urpi7vk10q2fb3 ON scim_roles USING btree (type);


--
-- TOC entry 1924 (class 1259 OID 34796)
-- Dependencies: 167 2111
-- Name: uk_8snvn02x0for0fvcj8erir2k0; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_8snvn02x0for0fvcj8erir2k0 ON scim_email USING btree (value);


--
-- TOC entry 1972 (class 1259 OID 34818)
-- Dependencies: 180 2111
-- Name: uk_abrc9lbp52g1b16x0dwtd5nld; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_abrc9lbp52g1b16x0dwtd5nld ON scim_phonenumber USING btree (value);


--
-- TOC entry 1961 (class 1259 OID 34815)
-- Dependencies: 176 176 2111
-- Name: uk_da192a97ita9ygqdlmabnf4bw; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_da192a97ita9ygqdlmabnf4bw ON scim_im USING btree (value, type);


--
-- TOC entry 1973 (class 1259 OID 34819)
-- Dependencies: 180 2111
-- Name: uk_e7hqv692l3lm558s16p1l5acm; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_e7hqv692l3lm558s16p1l5acm ON scim_phonenumber USING btree (type);


--
-- TOC entry 1920 (class 1259 OID 34795)
-- Dependencies: 166 166 2111
-- Name: uk_eplkwvpox52tjppj9oogkf6f2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_eplkwvpox52tjppj9oogkf6f2 ON scim_certificate USING btree (value, type);


--
-- TOC entry 1925 (class 1259 OID 34797)
-- Dependencies: 167 2111
-- Name: uk_hvpieto01a5c7b5edr1v9pom4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_hvpieto01a5c7b5edr1v9pom4 ON scim_email USING btree (type);


--
-- TOC entry 1932 (class 1259 OID 34801)
-- Dependencies: 169 169 2111
-- Name: uk_i0njmun17yqq9eslmg7dqehrf; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_i0njmun17yqq9eslmg7dqehrf ON scim_entitlements USING btree (value, type);


--
-- TOC entry 1983 (class 1259 OID 34826)
-- Dependencies: 182 182 2111
-- Name: uk_i7n6iwn2x3stgn9q515xn46gi; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_i7n6iwn2x3stgn9q515xn46gi ON scim_roles USING btree (value, type);


--
-- TOC entry 1979 (class 1259 OID 34823)
-- Dependencies: 181 181 2111
-- Name: uk_iculqbamgtumwnjyjxseafy5h; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_iculqbamgtumwnjyjxseafy5h ON scim_photo USING btree (value, type);


--
-- TOC entry 1916 (class 1259 OID 34791)
-- Dependencies: 165 2111
-- Name: uk_ie5406dj1t9i0f9hytgvbxjl2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_ie5406dj1t9i0f9hytgvbxjl2 ON scim_address USING btree (type);


--
-- TOC entry 1974 (class 1259 OID 34820)
-- Dependencies: 180 180 2111
-- Name: uk_ipfxts8e4ofm3oo5djk40pv86; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_ipfxts8e4ofm3oo5djk40pv86 ON scim_phonenumber USING btree (value, type);


--
-- TOC entry 1926 (class 1259 OID 34798)
-- Dependencies: 167 167 2111
-- Name: uk_j86m6mxppkb3g2vx72a11xob1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_j86m6mxppkb3g2vx72a11xob1 ON scim_email USING btree (value, type);


--
-- TOC entry 1984 (class 1259 OID 34824)
-- Dependencies: 182 2111
-- Name: uk_mw914wc9rj4qsue2q60n4ktk4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_mw914wc9rj4qsue2q60n4ktk4 ON scim_roles USING btree (value);


--
-- TOC entry 1933 (class 1259 OID 34799)
-- Dependencies: 169 2111
-- Name: uk_nxxhl5vhce96gwm0se9spjjjv; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_nxxhl5vhce96gwm0se9spjjjv ON scim_entitlements USING btree (value);


--
-- TOC entry 1944 (class 1259 OID 34806)
-- Dependencies: 172 172 2111
-- Name: uk_p2y10qxtuqdvbl5spxu98akx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_p2y10qxtuqdvbl5spxu98akx2 ON scim_extension_field_value USING btree (user_internal_id, extension_field_internal_id);


--
-- TOC entry 1921 (class 1259 OID 34793)
-- Dependencies: 166 2111
-- Name: uk_tb6nu6msjqh1qb2ne5e4ghnp0; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_tb6nu6msjqh1qb2ne5e4ghnp0 ON scim_certificate USING btree (value);


--
-- TOC entry 1997 (class 2606 OID 34869)
-- Dependencies: 171 1938 172 2111
-- Name: fk_6y0v7g2y69nkvody9jv5q3tuo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT fk_6y0v7g2y69nkvody9jv5q3tuo FOREIGN KEY (extension_field_internal_id) REFERENCES scim_extension_field(internal_id);


--
-- TOC entry 1995 (class 2606 OID 34859)
-- Dependencies: 1985 169 183 2111
-- Name: fk_7jnl5vqcfg1j9plj4py1qvxcp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_entitlements
    ADD CONSTRAINT fk_7jnl5vqcfg1j9plj4py1qvxcp FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 2000 (class 2606 OID 34884)
-- Dependencies: 1951 175 174 2111
-- Name: fk_b29y2qc2j5uu49wa9grpbulb0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT fk_b29y2qc2j5uu49wa9grpbulb0 FOREIGN KEY (members_internal_id) REFERENCES scim_id(internal_id);


--
-- TOC entry 2002 (class 2606 OID 34894)
-- Dependencies: 178 175 1964 2111
-- Name: fk_byxttqfbmb2wcj4ud3hd53mw3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
    ADD CONSTRAINT fk_byxttqfbmb2wcj4ud3hd53mw3 FOREIGN KEY (meta_id) REFERENCES scim_meta(id);


--
-- TOC entry 2007 (class 2606 OID 34919)
-- Dependencies: 179 183 1968 2111
-- Name: fk_d2ji7ipe62fbg8uu2ir7b9ls4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT fk_d2ji7ipe62fbg8uu2ir7b9ls4 FOREIGN KEY (name_id) REFERENCES scim_name(id);


--
-- TOC entry 1993 (class 2606 OID 34849)
-- Dependencies: 1985 183 167 2111
-- Name: fk_dmfj3s46npn4p1pcrc3iur2mp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
    ADD CONSTRAINT fk_dmfj3s46npn4p1pcrc3iur2mp FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1996 (class 2606 OID 34864)
-- Dependencies: 1934 171 170 2111
-- Name: fk_eksek96tmtxkaqe5a7hfmoswo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
    ADD CONSTRAINT fk_eksek96tmtxkaqe5a7hfmoswo FOREIGN KEY (extension_internal_id) REFERENCES scim_extension(internal_id);


--
-- TOC entry 2001 (class 2606 OID 34889)
-- Dependencies: 1945 174 173 2111
-- Name: fk_gct22972jrrv22crorixfdlmi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
    ADD CONSTRAINT fk_gct22972jrrv22crorixfdlmi FOREIGN KEY (groups_internal_id) REFERENCES scim_group(internal_id);


--
-- TOC entry 1992 (class 2606 OID 34844)
-- Dependencies: 183 166 1985 2111
-- Name: fk_ghdpgmh1b8suimtfxdl8653bj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
    ADD CONSTRAINT fk_ghdpgmh1b8suimtfxdl8653bj FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 2003 (class 2606 OID 34899)
-- Dependencies: 183 176 1985 2111
-- Name: fk_hmsah9dinhk7f8k4lf50h658; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
    ADD CONSTRAINT fk_hmsah9dinhk7f8k4lf50h658 FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1998 (class 2606 OID 34874)
-- Dependencies: 1985 172 183 2111
-- Name: fk_in6gs4safpkntvac3v88ke54r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
    ADD CONSTRAINT fk_in6gs4safpkntvac3v88ke54r FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1994 (class 2606 OID 34854)
-- Dependencies: 177 168 1962 2111
-- Name: fk_jxkq2wka34t20eejcvycluyr6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_enterprise
    ADD CONSTRAINT fk_jxkq2wka34t20eejcvycluyr6 FOREIGN KEY (manager_id) REFERENCES scim_manager(id);


--
-- TOC entry 2006 (class 2606 OID 34914)
-- Dependencies: 1985 182 183 2111
-- Name: fk_n5und6lnrtblhgs2ococpglyi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_roles
    ADD CONSTRAINT fk_n5und6lnrtblhgs2ococpglyi FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 2008 (class 2606 OID 34924)
-- Dependencies: 1951 183 175 2111
-- Name: fk_nx0839hyqd5yrfelxkr2fpr7a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
    ADD CONSTRAINT fk_nx0839hyqd5yrfelxkr2fpr7a FOREIGN KEY (internal_id) REFERENCES scim_id(internal_id);


--
-- TOC entry 1999 (class 2606 OID 34879)
-- Dependencies: 173 1951 175 2111
-- Name: fk_oari88x9o5j9jmigtt5s20m4k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
    ADD CONSTRAINT fk_oari88x9o5j9jmigtt5s20m4k FOREIGN KEY (internal_id) REFERENCES scim_id(internal_id);


--
-- TOC entry 2005 (class 2606 OID 34909)
-- Dependencies: 181 183 1985 2111
-- Name: fk_q3rk61yla08pvod7gq8av7i0l; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
    ADD CONSTRAINT fk_q3rk61yla08pvod7gq8av7i0l FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 1991 (class 2606 OID 34839)
-- Dependencies: 183 165 1985 2111
-- Name: fk_qr6gtqi0h9r6yp034tarlry1k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
    ADD CONSTRAINT fk_qr6gtqi0h9r6yp034tarlry1k FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


--
-- TOC entry 2004 (class 2606 OID 34904)
-- Dependencies: 183 1985 180 2111
-- Name: fk_rpqvdf1p9twdigaq1wclu5wm8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
    ADD CONSTRAINT fk_rpqvdf1p9twdigaq1wclu5wm8 FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);


-- Completed on 2014-02-14 16:20:06 CET

--
-- PostgreSQL database dump complete
--

