--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.0
-- Dumped by pg_dump version 9.3.1
-- Started on 2014-04-30 15:18:46 CEST

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = ON;
SET check_function_bodies = FALSE;
SET client_min_messages = WARNING;
SET default_with_oids = FALSE;


--
-- TOC entry 191 (class 1259 OID 109183)
-- Name: resource_server_sequence_scim_extension; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_server_sequence_scim_extension
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


--
-- TOC entry 192 (class 1259 OID 109185)
-- Name: resource_server_sequence_scim_extension_field; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_server_sequence_scim_extension_field
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


--
-- TOC entry 193 (class 1259 OID 109187)
-- Name: resource_server_sequence_scim_extension_field_value; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_server_sequence_scim_extension_field_value
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


--
-- TOC entry 194 (class 1259 OID 109189)
-- Name: resource_server_sequence_scim_id; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_server_sequence_scim_id
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


--
-- TOC entry 195 (class 1259 OID 109191)
-- Name: resource_server_sequence_scim_meta; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_server_sequence_scim_meta
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


--
-- TOC entry 196 (class 1259 OID 109193)
-- Name: resource_server_sequence_scim_multi_valued_attribute; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_server_sequence_scim_multi_valued_attribute
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


--
-- TOC entry 197 (class 1259 OID 109195)
-- Name: resource_server_sequence_scim_name; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE resource_server_sequence_scim_name
START WITH 100
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


--
-- TOC entry 174 (class 1259 OID 108930)
-- Name: scim_address; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_address (
  multi_value_id   BIGINT NOT NULL,
  is_primary       BOOLEAN,
  country          CHARACTER VARYING(255),
  formatted        TEXT,
  locality         CHARACTER VARYING(255),
  postalcode       CHARACTER VARYING(255),
  region           CHARACTER VARYING(255),
  streetaddress    CHARACTER VARYING(255),
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT NOT NULL
);


--
-- TOC entry 175 (class 1259 OID 108938)
-- Name: scim_certificate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_certificate (
  multi_value_id   BIGINT NOT NULL,
  is_primary       BOOLEAN,
  value            TEXT,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT NOT NULL
);


--
-- TOC entry 176 (class 1259 OID 108946)
-- Name: scim_email; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_email (
  multi_value_id   BIGINT NOT NULL,
  is_primary       BOOLEAN,
  value            TEXT,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT NOT NULL
);


--
-- TOC entry 177 (class 1259 OID 108954)
-- Name: scim_entitlements; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_entitlements (
  multi_value_id   BIGINT NOT NULL,
  is_primary       BOOLEAN,
  value            TEXT,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT NOT NULL
);


--
-- TOC entry 178 (class 1259 OID 108962)
-- Name: scim_extension; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension (
  internal_id BIGINT NOT NULL,
  urn         TEXT   NOT NULL
);


--
-- TOC entry 179 (class 1259 OID 108970)
-- Name: scim_extension_field; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension_field (
  internal_id           BIGINT                 NOT NULL,
  name                  CHARACTER VARYING(255),
  is_required           BOOLEAN,
  type                  CHARACTER VARYING(255) NOT NULL,
  extension_internal_id BIGINT
);


--
-- TOC entry 180 (class 1259 OID 108978)
-- Name: scim_extension_field_value; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_extension_field_value (
  internal_id                 BIGINT NOT NULL,
  value                       TEXT   NOT NULL,
  extension_field_internal_id BIGINT NOT NULL,
  user_internal_id            BIGINT NOT NULL
);


--
-- TOC entry 181 (class 1259 OID 108986)
-- Name: scim_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_group (
  displayname CHARACTER VARYING(255) NOT NULL,
  internal_id BIGINT                 NOT NULL
);


--
-- TOC entry 182 (class 1259 OID 108991)
-- Name: scim_group_scim_id; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_group_scim_id (
  groups_internal_id  BIGINT NOT NULL,
  members_internal_id BIGINT NOT NULL
);


--
-- TOC entry 183 (class 1259 OID 108996)
-- Name: scim_id; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_id (
  internal_id BIGINT                 NOT NULL,
  external_id CHARACTER VARYING(255),
  id          CHARACTER VARYING(255) NOT NULL,
  meta_id     BIGINT
);


--
-- TOC entry 184 (class 1259 OID 109004)
-- Name: scim_im; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_im (
  multi_value_id   BIGINT NOT NULL,
  is_primary       BOOLEAN,
  value            TEXT,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT NOT NULL
);


--
-- TOC entry 185 (class 1259 OID 109012)
-- Name: scim_meta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_meta (
  id           BIGINT NOT NULL,
  created      TIMESTAMP WITHOUT TIME ZONE,
  lastmodified TIMESTAMP WITHOUT TIME ZONE,
  location     TEXT,
  resourcetype CHARACTER VARYING(255),
  version      CHARACTER VARYING(255)
);


--
-- TOC entry 186 (class 1259 OID 109020)
-- Name: scim_name; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_name (
  id              BIGINT NOT NULL,
  familyname      CHARACTER VARYING(255),
  formatted       TEXT,
  givenname       CHARACTER VARYING(255),
  honorificprefix CHARACTER VARYING(255),
  honorificsuffix CHARACTER VARYING(255),
  middlename      CHARACTER VARYING(255)
);


--
-- TOC entry 187 (class 1259 OID 109028)
-- Name: scim_phonenumber; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_phonenumber (
  multi_value_id   BIGINT NOT NULL,
  is_primary       BOOLEAN,
  value            TEXT,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT NOT NULL
);


--
-- TOC entry 188 (class 1259 OID 109036)
-- Name: scim_photo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_photo (
  multi_value_id   BIGINT NOT NULL,
  is_primary       BOOLEAN,
  value            TEXT,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT NOT NULL
);


--
-- TOC entry 189 (class 1259 OID 109044)
-- Name: scim_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_roles (
  multi_value_id   BIGINT NOT NULL,
  is_primary       BOOLEAN,
  value            TEXT,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT NOT NULL
);


--
-- TOC entry 190 (class 1259 OID 109052)
-- Name: scim_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE scim_user (
  active            BOOLEAN,
  displayname       CHARACTER VARYING(255),
  locale            CHARACTER VARYING(255),
  nickname          CHARACTER VARYING(255),
  password          CHARACTER VARYING(255) NOT NULL,
  preferredlanguage CHARACTER VARYING(255),
  profileurl        TEXT,
  timezone          CHARACTER VARYING(255),
  title             CHARACTER VARYING(255),
  username          CHARACTER VARYING(255) NOT NULL,
  usertype          CHARACTER VARYING(255),
  internal_id       BIGINT                 NOT NULL,
  name_id           BIGINT
);


--
-- TOC entry 2204 (class 2606 OID 108937)
-- Name: scim_address_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
ADD CONSTRAINT scim_address_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 2208 (class 2606 OID 108945)
-- Name: scim_certificate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
ADD CONSTRAINT scim_certificate_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 2213 (class 2606 OID 108953)
-- Name: scim_email_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
ADD CONSTRAINT scim_email_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 2218 (class 2606 OID 108961)
-- Name: scim_entitlements_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_entitlements
ADD CONSTRAINT scim_entitlements_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 2227 (class 2606 OID 108977)
-- Name: scim_extension_field_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
ADD CONSTRAINT scim_extension_field_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 2231 (class 2606 OID 108985)
-- Name: scim_extension_field_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
ADD CONSTRAINT scim_extension_field_value_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 2223 (class 2606 OID 108969)
-- Name: scim_extension_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension
ADD CONSTRAINT scim_extension_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 2234 (class 2606 OID 108990)
-- Name: scim_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
ADD CONSTRAINT scim_group_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 2238 (class 2606 OID 108995)
-- Name: scim_group_scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
ADD CONSTRAINT scim_group_scim_id_pkey PRIMARY KEY (groups_internal_id, members_internal_id);


--
-- TOC entry 2240 (class 2606 OID 109003)
-- Name: scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
ADD CONSTRAINT scim_id_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 2246 (class 2606 OID 109011)
-- Name: scim_im_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
ADD CONSTRAINT scim_im_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 2251 (class 2606 OID 109019)
-- Name: scim_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_meta
ADD CONSTRAINT scim_meta_pkey PRIMARY KEY (id);


--
-- TOC entry 2255 (class 2606 OID 109027)
-- Name: scim_name_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_name
ADD CONSTRAINT scim_name_pkey PRIMARY KEY (id);


--
-- TOC entry 2257 (class 2606 OID 109035)
-- Name: scim_phonenumber_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
ADD CONSTRAINT scim_phonenumber_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 2262 (class 2606 OID 109043)
-- Name: scim_photo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
ADD CONSTRAINT scim_photo_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 2267 (class 2606 OID 109051)
-- Name: scim_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_roles
ADD CONSTRAINT scim_roles_pkey PRIMARY KEY (multi_value_id);


--
-- TOC entry 2272 (class 2606 OID 109059)
-- Name: scim_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
ADD CONSTRAINT scim_user_pkey PRIMARY KEY (internal_id);


--
-- TOC entry 2242 (class 2606 OID 109079)
-- Name: uk_164dcfif0r82xubvindi9vrnc; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
ADD CONSTRAINT uk_164dcfif0r82xubvindi9vrnc UNIQUE (external_id);


--
-- TOC entry 2236 (class 2606 OID 109077)
-- Name: uk_1dt64mbf4gp83rwy18jofwwf; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
ADD CONSTRAINT uk_1dt64mbf4gp83rwy18jofwwf UNIQUE (displayname);


--
-- TOC entry 2274 (class 2606 OID 109097)
-- Name: uk_1onynolltgwuk8a5ngjhkqcl1; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
ADD CONSTRAINT uk_1onynolltgwuk8a5ngjhkqcl1 UNIQUE (username);


--
-- TOC entry 2225 (class 2606 OID 109072)
-- Name: uk_60sysrrwavtwwnji8nw5tng2x; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension
ADD CONSTRAINT uk_60sysrrwavtwwnji8nw5tng2x UNIQUE (urn);


--
-- TOC entry 2229 (class 2606 OID 109074)
-- Name: uk_9rvm7w04q503y4gx9q0c55cnv; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
ADD CONSTRAINT uk_9rvm7w04q503y4gx9q0c55cnv UNIQUE (extension_internal_id, name);


--
-- TOC entry 2244 (class 2606 OID 109081)
-- Name: uk_q4ya5m8v6tafgtvw1inqtmm42; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
ADD CONSTRAINT uk_q4ya5m8v6tafgtvw1inqtmm42 UNIQUE (id);


--
-- TOC entry 2252 (class 1259 OID 109086)
-- Name: uk_1b0o2foyw6nainc2vrssxkok0; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_1b0o2foyw6nainc2vrssxkok0 ON scim_meta USING BTREE (lastmodified);


--
-- TOC entry 2263 (class 1259 OID 109091)
-- Name: uk_1er38kw2ith4ewuf7b5rhh7br; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_1er38kw2ith4ewuf7b5rhh7br ON scim_photo USING BTREE (type);


--
-- TOC entry 2253 (class 1259 OID 109085)
-- Name: uk_1o8kevc2e2hfk24f19j3vcia4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_1o8kevc2e2hfk24f19j3vcia4 ON scim_meta USING BTREE (created);


--
-- TOC entry 2247 (class 1259 OID 109082)
-- Name: uk_31njuvoulynkorup0b5pjqni6; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_31njuvoulynkorup0b5pjqni6 ON scim_im USING BTREE (value);


--
-- TOC entry 2205 (class 1259 OID 109061)
-- Name: uk_3hqwl74jwjq0dksv2t4iqlptm; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_3hqwl74jwjq0dksv2t4iqlptm ON scim_address USING BTREE (country, region, locality, postalcode, streetaddress);


--
-- TOC entry 2264 (class 1259 OID 109090)
-- Name: uk_6y89p0fpcdcg2fq9k5u8h1173; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_6y89p0fpcdcg2fq9k5u8h1173 ON scim_photo USING BTREE (value);


--
-- TOC entry 2219 (class 1259 OID 109069)
-- Name: uk_75wo1phhovp2nbruh2dmfhcwk; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_75wo1phhovp2nbruh2dmfhcwk ON scim_entitlements USING BTREE (type);


--
-- TOC entry 2209 (class 1259 OID 109063)
-- Name: uk_7k7tc0du5jucy4ranqn8uid4b; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_7k7tc0du5jucy4ranqn8uid4b ON scim_certificate USING BTREE (type);


--
-- TOC entry 2248 (class 1259 OID 109083)
-- Name: uk_88yyj57g5nisgp2trhs2yqa91; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_88yyj57g5nisgp2trhs2yqa91 ON scim_im USING BTREE (type);


--
-- TOC entry 2268 (class 1259 OID 109094)
-- Name: uk_8qwt29ewjm8urpi7vk10q2fb3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_8qwt29ewjm8urpi7vk10q2fb3 ON scim_roles USING BTREE (type);


--
-- TOC entry 2214 (class 1259 OID 109065)
-- Name: uk_8snvn02x0for0fvcj8erir2k0; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_8snvn02x0for0fvcj8erir2k0 ON scim_email USING BTREE (value);


--
-- TOC entry 2258 (class 1259 OID 109087)
-- Name: uk_abrc9lbp52g1b16x0dwtd5nld; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_abrc9lbp52g1b16x0dwtd5nld ON scim_phonenumber USING BTREE (value);


--
-- TOC entry 2249 (class 1259 OID 109084)
-- Name: uk_da192a97ita9ygqdlmabnf4bw; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_da192a97ita9ygqdlmabnf4bw ON scim_im USING BTREE (value, type);


--
-- TOC entry 2259 (class 1259 OID 109088)
-- Name: uk_e7hqv692l3lm558s16p1l5acm; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_e7hqv692l3lm558s16p1l5acm ON scim_phonenumber USING BTREE (type);


--
-- TOC entry 2210 (class 1259 OID 109064)
-- Name: uk_eplkwvpox52tjppj9oogkf6f2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_eplkwvpox52tjppj9oogkf6f2 ON scim_certificate USING BTREE (value, type);


--
-- TOC entry 2215 (class 1259 OID 109066)
-- Name: uk_hvpieto01a5c7b5edr1v9pom4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_hvpieto01a5c7b5edr1v9pom4 ON scim_email USING BTREE (type);


--
-- TOC entry 2220 (class 1259 OID 109070)
-- Name: uk_i0njmun17yqq9eslmg7dqehrf; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_i0njmun17yqq9eslmg7dqehrf ON scim_entitlements USING BTREE (value, type);


--
-- TOC entry 2269 (class 1259 OID 109095)
-- Name: uk_i7n6iwn2x3stgn9q515xn46gi; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_i7n6iwn2x3stgn9q515xn46gi ON scim_roles USING BTREE (value, type);


--
-- TOC entry 2265 (class 1259 OID 109092)
-- Name: uk_iculqbamgtumwnjyjxseafy5h; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_iculqbamgtumwnjyjxseafy5h ON scim_photo USING BTREE (value, type);


--
-- TOC entry 2206 (class 1259 OID 109060)
-- Name: uk_ie5406dj1t9i0f9hytgvbxjl2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_ie5406dj1t9i0f9hytgvbxjl2 ON scim_address USING BTREE (type);


--
-- TOC entry 2260 (class 1259 OID 109089)
-- Name: uk_ipfxts8e4ofm3oo5djk40pv86; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_ipfxts8e4ofm3oo5djk40pv86 ON scim_phonenumber USING BTREE (value, type);


--
-- TOC entry 2216 (class 1259 OID 109067)
-- Name: uk_j86m6mxppkb3g2vx72a11xob1; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_j86m6mxppkb3g2vx72a11xob1 ON scim_email USING BTREE (value, type);


--
-- TOC entry 2270 (class 1259 OID 109093)
-- Name: uk_mw914wc9rj4qsue2q60n4ktk4; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_mw914wc9rj4qsue2q60n4ktk4 ON scim_roles USING BTREE (value);


--
-- TOC entry 2221 (class 1259 OID 109068)
-- Name: uk_nxxhl5vhce96gwm0se9spjjjv; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_nxxhl5vhce96gwm0se9spjjjv ON scim_entitlements USING BTREE (value);


--
-- TOC entry 2232 (class 1259 OID 109075)
-- Name: uk_p2y10qxtuqdvbl5spxu98akx2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_p2y10qxtuqdvbl5spxu98akx2 ON scim_extension_field_value USING BTREE (user_internal_id, extension_field_internal_id);


--
-- TOC entry 2211 (class 1259 OID 109062)
-- Name: uk_tb6nu6msjqh1qb2ne5e4ghnp0; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX uk_tb6nu6msjqh1qb2ne5e4ghnp0 ON scim_certificate USING BTREE (value);


--
-- TOC entry 2282 (class 2606 OID 109123)
-- Name: fk_6y0v7g2y69nkvody9jv5q3tuo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
ADD CONSTRAINT fk_6y0v7g2y69nkvody9jv5q3tuo FOREIGN KEY (extension_field_internal_id) REFERENCES scim_extension_field (internal_id);


--
-- TOC entry 2280 (class 2606 OID 109113)
-- Name: fk_7jnl5vqcfg1j9plj4py1qvxcp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_entitlements
ADD CONSTRAINT fk_7jnl5vqcfg1j9plj4py1qvxcp FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- TOC entry 2285 (class 2606 OID 109138)
-- Name: fk_b29y2qc2j5uu49wa9grpbulb0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
ADD CONSTRAINT fk_b29y2qc2j5uu49wa9grpbulb0 FOREIGN KEY (members_internal_id) REFERENCES scim_id (internal_id);


--
-- TOC entry 2287 (class 2606 OID 109148)
-- Name: fk_byxttqfbmb2wcj4ud3hd53mw3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_id
ADD CONSTRAINT fk_byxttqfbmb2wcj4ud3hd53mw3 FOREIGN KEY (meta_id) REFERENCES scim_meta (id);


--
-- TOC entry 2292 (class 2606 OID 109173)
-- Name: fk_d2ji7ipe62fbg8uu2ir7b9ls4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
ADD CONSTRAINT fk_d2ji7ipe62fbg8uu2ir7b9ls4 FOREIGN KEY (name_id) REFERENCES scim_name (id);


--
-- TOC entry 2279 (class 2606 OID 109108)
-- Name: fk_dmfj3s46npn4p1pcrc3iur2mp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_email
ADD CONSTRAINT fk_dmfj3s46npn4p1pcrc3iur2mp FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- TOC entry 2281 (class 2606 OID 109118)
-- Name: fk_eksek96tmtxkaqe5a7hfmoswo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field
ADD CONSTRAINT fk_eksek96tmtxkaqe5a7hfmoswo FOREIGN KEY (extension_internal_id) REFERENCES scim_extension (internal_id);


--
-- TOC entry 2286 (class 2606 OID 109143)
-- Name: fk_gct22972jrrv22crorixfdlmi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group_scim_id
ADD CONSTRAINT fk_gct22972jrrv22crorixfdlmi FOREIGN KEY (groups_internal_id) REFERENCES scim_group (internal_id);


--
-- TOC entry 2278 (class 2606 OID 109103)
-- Name: fk_ghdpgmh1b8suimtfxdl8653bj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_certificate
ADD CONSTRAINT fk_ghdpgmh1b8suimtfxdl8653bj FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- TOC entry 2288 (class 2606 OID 109153)
-- Name: fk_hmsah9dinhk7f8k4lf50h658; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_im
ADD CONSTRAINT fk_hmsah9dinhk7f8k4lf50h658 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- TOC entry 2283 (class 2606 OID 109128)
-- Name: fk_in6gs4safpkntvac3v88ke54r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_extension_field_value
ADD CONSTRAINT fk_in6gs4safpkntvac3v88ke54r FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- TOC entry 2291 (class 2606 OID 109168)
-- Name: fk_n5und6lnrtblhgs2ococpglyi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_roles
ADD CONSTRAINT fk_n5und6lnrtblhgs2ococpglyi FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- TOC entry 2293 (class 2606 OID 109178)
-- Name: fk_nx0839hyqd5yrfelxkr2fpr7a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_user
ADD CONSTRAINT fk_nx0839hyqd5yrfelxkr2fpr7a FOREIGN KEY (internal_id) REFERENCES scim_id (internal_id);


--
-- TOC entry 2284 (class 2606 OID 109133)
-- Name: fk_oari88x9o5j9jmigtt5s20m4k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_group
ADD CONSTRAINT fk_oari88x9o5j9jmigtt5s20m4k FOREIGN KEY (internal_id) REFERENCES scim_id (internal_id);


--
-- TOC entry 2290 (class 2606 OID 109163)
-- Name: fk_q3rk61yla08pvod7gq8av7i0l; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_photo
ADD CONSTRAINT fk_q3rk61yla08pvod7gq8av7i0l FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- TOC entry 2277 (class 2606 OID 109098)
-- Name: fk_qr6gtqi0h9r6yp034tarlry1k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_address
ADD CONSTRAINT fk_qr6gtqi0h9r6yp034tarlry1k FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


--
-- TOC entry 2289 (class 2606 OID 109158)
-- Name: fk_rpqvdf1p9twdigaq1wclu5wm8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scim_phonenumber
ADD CONSTRAINT fk_rpqvdf1p9twdigaq1wclu5wm8 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


-- Completed on 2014-04-30 15:18:47 CEST

--
-- PostgreSQL database dump complete
--

-- Must be imported into database before starting resource server

-- This extension needed by the auth server

INSERT INTO scim_extension VALUES (1, 'urn:org.osiam:scim:extensions:auth-server');

INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
VALUES (1, FALSE, 'origin', 'STRING', 1);

--
-- Example User: admin, pw: koala
--
INSERT INTO scim_meta (id, created, lastmodified, location, resourcetype, version)
VALUES (1, '2011-10-10', '2011-10-10', NULL, 'User', NULL);

INSERT INTO scim_id (internal_id, external_id, id, meta_id)
VALUES (1, NULL, 'cef9452e-00a9-4cec-a086-d171374ffbef', 1);

INSERT INTO scim_user (active, displayname, locale, nickname, password, preferredlanguage,
                       profileurl, timezone, title, username, usertype, internal_id,
                       name_id)
VALUES (TRUE, NULL, NULL, NULL,
        'cbae73fac0893291c4792ef19d158a589402288b35cb18fb8406e951b9d95f6b8b06a3526ffebe96ae0d91c04ae615a7fe2af362763db386ccbf3b55c29ae800',
        NULL,
        NULL, NULL, NULL, 'admin', NULL, 1,
        NULL);
