--
-- TOC entry 174 (class 1259 OID 108930)
-- Name: scim_address; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_address (
    multi_value_id bigint NOT NULL AUTO_INCREMENT,
    is_primary boolean,
    country character varying(255),
    formatted text,
    locality character varying(255),
    postalcode character varying(255),
    region character varying(255),
    streetaddress character varying(255),
    type character varying(255),
    user_internal_id bigint NOT NULL,
    primary key(multi_value_id)
) ENGINE=InnoDB;

--
-- TOC entry 175 (class 1259 OID 108938)
-- Name: scim_certificate; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_certificate (
    multi_value_id bigint NOT NULL AUTO_INCREMENT,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL,
    primary key(multi_value_id)
) ENGINE=InnoDB;

--
-- TOC entry 176 (class 1259 OID 108946)
-- Name: scim_email; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_email (
    multi_value_id bigint NOT NULL AUTO_INCREMENT,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL,
    primary key(multi_value_id)
) ENGINE=InnoDB;

--
-- TOC entry 177 (class 1259 OID 108954)
-- Name: scim_entitlements; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_entitlements (
    multi_value_id bigint NOT NULL AUTO_INCREMENT,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL,
    primary key(multi_value_id)
) ENGINE=InnoDB;

--
-- TOC entry 178 (class 1259 OID 108962)
-- Name: scim_extension; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_extension (
    internal_id bigint NOT NULL AUTO_INCREMENT,
    urn text NOT NULL,
    primary key(internal_id)
) ENGINE=InnoDB;

--
-- TOC entry 179 (class 1259 OID 108970)
-- Name: scim_extension_field; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_extension_field (
    internal_id bigint NOT NULL AUTO_INCREMENT,
    name character varying(255),
    is_required boolean,
    type character varying(255) NOT NULL,
    extension_internal_id bigint,
    primary key(internal_id)
) ENGINE=InnoDB;

--
-- TOC entry 180 (class 1259 OID 108978)
-- Name: scim_extension_field_value; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_extension_field_value (
    internal_id bigint NOT NULL AUTO_INCREMENT,
    value text NOT NULL,
    extension_field_internal_id bigint NOT NULL,
    user_internal_id bigint NOT NULL,
    primary key(internal_id)
) ENGINE=InnoDB;

--
-- TOC entry 181 (class 1259 OID 108986)
-- Name: scim_group; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_group (
    displayname character varying(255) NOT NULL,
    internal_id bigint NOT NULL
) ENGINE=InnoDB;

--
-- TOC entry 182 (class 1259 OID 108991)
-- Name: scim_group_scim_id; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_group_scim_id (
    groups_internal_id bigint NOT NULL,
    members_internal_id bigint NOT NULL
) ENGINE=InnoDB;

--
-- TOC entry 183 (class 1259 OID 108996)
-- Name: scim_id; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_id (
    internal_id bigint NOT NULL AUTO_INCREMENT,
    external_id character varying(255),
    id character varying(255) NOT NULL,
    meta_id bigint,
    PRIMARY KEY (internal_id)
) ENGINE=InnoDB;

--
-- TOC entry 184 (class 1259 OID 109004)
-- Name: scim_im; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_im (
    multi_value_id bigint NOT NULL AUTO_INCREMENT,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL,
    primary key(multi_value_id)
) ENGINE=InnoDB;

--
-- TOC entry 185 (class 1259 OID 109012)
-- Name: scim_meta; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_meta (
    id bigint NOT NULL AUTO_INCREMENT,
    created timestamp NULL,
    lastmodified timestamp NULL,
    location text,
    resourcetype character varying(255),
    version character varying(255),
    primary key(id)
) ENGINE=InnoDB;

--
-- TOC entry 186 (class 1259 OID 109020)
-- Name: scim_name; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_name (
    id bigint NOT NULL AUTO_INCREMENT,
    familyname character varying(255),
    formatted text,
    givenname character varying(255),
    honorificprefix character varying(255),
    honorificsuffix character varying(255),
    middlename character varying(255),
    primary key(id)
) ENGINE=InnoDB;

--
-- TOC entry 187 (class 1259 OID 109028)
-- Name: scim_phonenumber; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_phonenumber (
    multi_value_id bigint NOT NULL AUTO_INCREMENT,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL,
    primary key(multi_value_id)
) ENGINE=InnoDB;

--
-- TOC entry 188 (class 1259 OID 109036)
-- Name: scim_photo; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_photo (
    multi_value_id bigint NOT NULL AUTO_INCREMENT,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL,
    primary key(multi_value_id)
) ENGINE=InnoDB;

--
-- TOC entry 189 (class 1259 OID 109044)
-- Name: scim_roles; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_roles (
    multi_value_id bigint NOT NULL AUTO_INCREMENT,
    is_primary boolean,
    value text,
    type character varying(255),
    user_internal_id bigint NOT NULL,
    primary key(multi_value_id)
) ENGINE=InnoDB;

--
-- TOC entry 190 (class 1259 OID 109052)
-- Name: scim_user; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE scim_user
 (
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
) ENGINE=InnoDB;
--
-- TOC entry 2234 (class 2606 OID 108990)
-- Name: scim_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_group
    ADD CONSTRAINT scim_group_pkey PRIMARY KEY (internal_id);
--
-- TOC entry 2238 (class 2606 OID 108995)
-- Name: scim_group_scim_id_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_group_scim_id
    ADD CONSTRAINT scim_group_scim_id_pkey PRIMARY KEY (groups_internal_id, members_internal_id);
--
-- TOC entry 2272 (class 2606 OID 109059)
-- Name: scim_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_user
    ADD CONSTRAINT scim_user_pkey PRIMARY KEY (internal_id);
--
-- TOC entry 2242 (class 2606 OID 109079)
-- Name: uk_164dcfif0r82xubvindi9vrnc; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_id
    ADD CONSTRAINT uk_164dcfif0r82xubvindi9vrnc UNIQUE (external_id);
--
-- TOC entry 2236 (class 2606 OID 109077)
-- Name: uk_1dt64mbf4gp83rwy18jofwwf; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_group
    ADD CONSTRAINT uk_1dt64mbf4gp83rwy18jofwwf UNIQUE (displayname);
--
-- TOC entry 2274 (class 2606 OID 109097)
-- Name: uk_1onynolltgwuk8a5ngjhkqcl1; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_user
    ADD CONSTRAINT uk_1onynolltgwuk8a5ngjhkqcl1 UNIQUE (username);
--
-- TOC entry 2225 (class 2606 OID 109072)
-- Name: uk_60sysrrwavtwwnji8nw5tng2x; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_extension
    ADD CONSTRAINT uk_60sysrrwavtwwnji8nw5tng2x UNIQUE (urn(767));
--
-- TOC entry 2229 (class 2606 OID 109074)
-- Name: uk_9rvm7w04q503y4gx9q0c55cnv; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_extension_field
    ADD CONSTRAINT uk_9rvm7w04q503y4gx9q0c55cnv UNIQUE (extension_internal_id, name);
--
-- TOC entry 2244 (class 2606 OID 109081)
-- Name: uk_q4ya5m8v6tafgtvw1inqtmm42; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_id
    ADD CONSTRAINT uk_q4ya5m8v6tafgtvw1inqtmm42 UNIQUE (id);

--
-- TOC entry 2252 (class 1259 OID 109086)
-- Name: uk_1b0o2foyw6nainc2vrssxkok0; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_1b0o2foyw6nainc2vrssxkok0 USING btree ON scim_meta (lastmodified);
--
-- TOC entry 2263 (class 1259 OID 109091)
-- Name: uk_1er38kw2ith4ewuf7b5rhh7br; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_1er38kw2ith4ewuf7b5rhh7br USING btree ON scim_photo (type);

--
-- TOC entry 2253 (class 1259 OID 109085)
-- Name: uk_1o8kevc2e2hfk24f19j3vcia4; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_1o8kevc2e2hfk24f19j3vcia4 USING btree ON scim_meta (created);

--
-- TOC entry 2247 (class 1259 OID 109082)
-- Name: uk_31njuvoulynkorup0b5pjqni6; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_31njuvoulynkorup0b5pjqni6 USING btree ON scim_im (value(767));
--
-- TOC entry 2205 (class 1259 OID 109061)
-- Name: uk_3hqwl74jwjq0dksv2t4iqlptm; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_3hqwl74jwjq0dksv2t4iqlptm  USING btree ON scim_address (country, region, locality, postalcode, streetaddress);

--
-- TOC entry 2264 (class 1259 OID 109090)
-- Name: uk_6y89p0fpcdcg2fq9k5u8h1173; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_6y89p0fpcdcg2fq9k5u8h1173 USING btree ON scim_photo (value(767));

--
-- TOC entry 2219 (class 1259 OID 109069)
-- Name: uk_75wo1phhovp2nbruh2dmfhcwk; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_75wo1phhovp2nbruh2dmfhcwk USING btree ON scim_entitlements (type);

--
-- TOC entry 2209 (class 1259 OID 109063)
-- Name: uk_7k7tc0du5jucy4ranqn8uid4b; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_7k7tc0du5jucy4ranqn8uid4b USING btree ON scim_certificate (type);

--
-- TOC entry 2248 (class 1259 OID 109083)
-- Name: uk_88yyj57g5nisgp2trhs2yqa91; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_88yyj57g5nisgp2trhs2yqa91 USING btree ON scim_im (type);

--
-- TOC entry 2268 (class 1259 OID 109094)
-- Name: uk_8qwt29ewjm8urpi7vk10q2fb3; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_8qwt29ewjm8urpi7vk10q2fb3 USING btree ON scim_roles (type);

--
-- TOC entry 2214 (class 1259 OID 109065)
-- Name: uk_8snvn02x0for0fvcj8erir2k0; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_8snvn02x0for0fvcj8erir2k0 USING btree ON scim_email (value(767));

--
-- TOC entry 2258 (class 1259 OID 109087)
-- Name: uk_abrc9lbp52g1b16x0dwtd5nld; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_abrc9lbp52g1b16x0dwtd5nld USING btree ON scim_phonenumber (value(767));

--
-- TOC entry 2249 (class 1259 OID 109084)
-- Name: uk_da192a97ita9ygqdlmabnf4bw; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_da192a97ita9ygqdlmabnf4bw USING btree ON scim_im (value(767), type);

--
-- TOC entry 2259 (class 1259 OID 109088)
-- Name: uk_e7hqv692l3lm558s16p1l5acm; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_e7hqv692l3lm558s16p1l5acm USING btree ON scim_phonenumber (type);

--
-- TOC entry 2210 (class 1259 OID 109064)
-- Name: uk_eplkwvpox52tjppj9oogkf6f2; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_eplkwvpox52tjppj9oogkf6f2 USING btree ON scim_certificate (value(767), type);

--
-- TOC entry 2215 (class 1259 OID 109066)
-- Name: uk_hvpieto01a5c7b5edr1v9pom4; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_hvpieto01a5c7b5edr1v9pom4 USING btree ON scim_email (type);

--
-- TOC entry 2220 (class 1259 OID 109070)
-- Name: uk_i0njmun17yqq9eslmg7dqehrf; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_i0njmun17yqq9eslmg7dqehrf USING btree ON scim_entitlements (value(767), type);

--
-- TOC entry 2269 (class 1259 OID 109095)
-- Name: uk_i7n6iwn2x3stgn9q515xn46gi; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_i7n6iwn2x3stgn9q515xn46gi USING btree ON scim_roles (value(767), type);

--
-- TOC entry 2265 (class 1259 OID 109092)
-- Name: uk_iculqbamgtumwnjyjxseafy5h; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_iculqbamgtumwnjyjxseafy5h USING btree ON scim_photo (value(767), type);

--
-- TOC entry 2206 (class 1259 OID 109060)
-- Name: uk_ie5406dj1t9i0f9hytgvbxjl2; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_ie5406dj1t9i0f9hytgvbxjl2 USING btree ON scim_address (type);

--
-- TOC entry 2260 (class 1259 OID 109089)
-- Name: uk_ipfxts8e4ofm3oo5djk40pv86; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_ipfxts8e4ofm3oo5djk40pv86 USING btree ON scim_phonenumber (value(767), type);

--
-- TOC entry 2216 (class 1259 OID 109067)
-- Name: uk_j86m6mxppkb3g2vx72a11xob1; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_j86m6mxppkb3g2vx72a11xob1 USING btree ON scim_email (value(767), type);

--
-- TOC entry 2270 (class 1259 OID 109093)
-- Name: uk_mw914wc9rj4qsue2q60n4ktk4; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_mw914wc9rj4qsue2q60n4ktk4 USING btree ON scim_roles (value(767));

--
-- TOC entry 2221 (class 1259 OID 109068)
-- Name: uk_nxxhl5vhce96gwm0se9spjjjv; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_nxxhl5vhce96gwm0se9spjjjv USING btree ON scim_entitlements (value(767));

--
-- TOC entry 2232 (class 1259 OID 109075)
-- Name: uk_p2y10qxtuqdvbl5spxu98akx2; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_p2y10qxtuqdvbl5spxu98akx2 USING btree ON scim_extension_field_value (user_internal_id, extension_field_internal_id);

--
-- TOC entry 2211 (class 1259 OID 109062)
-- Name: uk_tb6nu6msjqh1qb2ne5e4ghnp0; Type: INDEX; Schema: public; Owner: -
--
CREATE INDEX uk_tb6nu6msjqh1qb2ne5e4ghnp0 USING btree ON scim_certificate (value(767));

--
-- TOC entry 2282 (class 2606 OID 109123)
-- Name: fk_6y0v7g2y69nkvody9jv5q3tuo; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_extension_field_value
    ADD CONSTRAINT fk_6y0v7g2y69nkvody9jv5q3tuo FOREIGN KEY (extension_field_internal_id) REFERENCES scim_extension_field(internal_id);

--
-- TOC entry 2280 (class 2606 OID 109113)
-- Name: fk_7jnl5vqcfg1j9plj4py1qvxcp; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_entitlements
    ADD CONSTRAINT fk_7jnl5vqcfg1j9plj4py1qvxcp FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);

--
-- TOC entry 2285 (class 2606 OID 109138)
-- Name: fk_b29y2qc2j5uu49wa9grpbulb0; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_group_scim_id
    ADD CONSTRAINT fk_b29y2qc2j5uu49wa9grpbulb0 FOREIGN KEY (members_internal_id) REFERENCES scim_id(internal_id);

--
-- TOC entry 2287 (class 2606 OID 109148)
-- Name: fk_byxttqfbmb2wcj4ud3hd53mw3; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_id
    ADD CONSTRAINT fk_byxttqfbmb2wcj4ud3hd53mw3 FOREIGN KEY (meta_id) REFERENCES scim_meta(id);

--
-- TOC entry 2292 (class 2606 OID 109173)
-- Name: fk_d2ji7ipe62fbg8uu2ir7b9ls4; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_user
    ADD CONSTRAINT fk_d2ji7ipe62fbg8uu2ir7b9ls4 FOREIGN KEY (name_id) REFERENCES scim_name(id);

--
-- TOC entry 2279 (class 2606 OID 109108)
-- Name: fk_dmfj3s46npn4p1pcrc3iur2mp; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_email
    ADD CONSTRAINT fk_dmfj3s46npn4p1pcrc3iur2mp FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);

--
-- TOC entry 2281 (class 2606 OID 109118)
-- Name: fk_eksek96tmtxkaqe5a7hfmoswo; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_extension_field
    ADD CONSTRAINT fk_eksek96tmtxkaqe5a7hfmoswo FOREIGN KEY (extension_internal_id) REFERENCES scim_extension(internal_id);

--
-- TOC entry 2286 (class 2606 OID 109143)
-- Name: fk_gct22972jrrv22crorixfdlmi; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_group_scim_id
    ADD CONSTRAINT fk_gct22972jrrv22crorixfdlmi FOREIGN KEY (groups_internal_id) REFERENCES scim_group(internal_id);

--
-- TOC entry 2278 (class 2606 OID 109103)
-- Name: fk_ghdpgmh1b8suimtfxdl8653bj; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_certificate
    ADD CONSTRAINT fk_ghdpgmh1b8suimtfxdl8653bj FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);

--
-- TOC entry 2288 (class 2606 OID 109153)
-- Name: fk_hmsah9dinhk7f8k4lf50h658; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_im
    ADD CONSTRAINT fk_hmsah9dinhk7f8k4lf50h658 FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);

--
-- TOC entry 2283 (class 2606 OID 109128)
-- Name: fk_in6gs4safpkntvac3v88ke54r; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_extension_field_value
    ADD CONSTRAINT fk_in6gs4safpkntvac3v88ke54r FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);

--
-- TOC entry 2291 (class 2606 OID 109168)
-- Name: fk_n5und6lnrtblhgs2ococpglyi; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_roles
    ADD CONSTRAINT fk_n5und6lnrtblhgs2ococpglyi FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);

--
-- TOC entry 2293 (class 2606 OID 109178)
-- Name: fk_nx0839hyqd5yrfelxkr2fpr7a; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_user
    ADD CONSTRAINT fk_nx0839hyqd5yrfelxkr2fpr7a FOREIGN KEY (internal_id) REFERENCES scim_id(internal_id);

--
-- TOC entry 2284 (class 2606 OID 109133)
-- Name: fk_oari88x9o5j9jmigtt5s20m4k; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_group
    ADD CONSTRAINT fk_oari88x9o5j9jmigtt5s20m4k FOREIGN KEY (internal_id) REFERENCES scim_id(internal_id);

--
-- TOC entry 2290 (class 2606 OID 109163)
-- Name: fk_q3rk61yla08pvod7gq8av7i0l; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_photo
    ADD CONSTRAINT fk_q3rk61yla08pvod7gq8av7i0l FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);

--
-- TOC entry 2277 (class 2606 OID 109098)
-- Name: fk_qr6gtqi0h9r6yp034tarlry1k; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_address
    ADD CONSTRAINT fk_qr6gtqi0h9r6yp034tarlry1k FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);

--
-- TOC entry 2289 (class 2606 OID 109158)
-- Name: fk_rpqvdf1p9twdigaq1wclu5wm8; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE scim_phonenumber
    ADD CONSTRAINT fk_rpqvdf1p9twdigaq1wclu5wm8 FOREIGN KEY (user_internal_id) REFERENCES scim_user(internal_id);