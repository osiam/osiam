-- Migrationscript from release server version 0.16 to 0.17

DELETE FROM scim_extension_field_value WHERE value IS NULL OR value = '';

ALTER TABLE ONLY scim_extension_field
    ADD CONSTRAINT uk_9rvm7w04q503y4gx9q0c55cnv UNIQUE (extension_internal_id, name);

CREATE INDEX uk_1b0o2foyw6nainc2vrssxkok0 ON scim_meta USING btree (lastmodified);

CREATE INDEX uk_1er38kw2ith4ewuf7b5rhh7br ON scim_photo USING btree (type);

CREATE INDEX uk_1o8kevc2e2hfk24f19j3vcia4 ON scim_meta USING btree (created);

CREATE INDEX uk_31njuvoulynkorup0b5pjqni6 ON scim_im USING btree (value);

CREATE INDEX uk_3hqwl74jwjq0dksv2t4iqlptm ON scim_address USING btree (country, region, locality, postalcode, streetaddress);

CREATE INDEX uk_6y89p0fpcdcg2fq9k5u8h1173 ON scim_photo USING btree (value);

CREATE INDEX uk_75wo1phhovp2nbruh2dmfhcwk ON scim_entitlements USING btree (type);

CREATE INDEX uk_7k7tc0du5jucy4ranqn8uid4b ON scim_certificate USING btree (type);

CREATE INDEX uk_88yyj57g5nisgp2trhs2yqa91 ON scim_im USING btree (type);

CREATE INDEX uk_8qwt29ewjm8urpi7vk10q2fb3 ON scim_roles USING btree (type);

CREATE INDEX uk_8snvn02x0for0fvcj8erir2k0 ON scim_email USING btree (value);

CREATE INDEX uk_abrc9lbp52g1b16x0dwtd5nld ON scim_phonenumber USING btree (value);

CREATE INDEX uk_da192a97ita9ygqdlmabnf4bw ON scim_im USING btree (value, type);

CREATE INDEX uk_e7hqv692l3lm558s16p1l5acm ON scim_phonenumber USING btree (type);

CREATE INDEX uk_eplkwvpox52tjppj9oogkf6f2 ON scim_certificate USING btree (value, type);

CREATE INDEX uk_hvpieto01a5c7b5edr1v9pom4 ON scim_email USING btree (type);

CREATE INDEX uk_i0njmun17yqq9eslmg7dqehrf ON scim_entitlements USING btree (value, type);

CREATE INDEX uk_i7n6iwn2x3stgn9q515xn46gi ON scim_roles USING btree (value, type);

CREATE INDEX uk_iculqbamgtumwnjyjxseafy5h ON scim_photo USING btree (value, type);

CREATE INDEX uk_ie5406dj1t9i0f9hytgvbxjl2 ON scim_address USING btree (type);

CREATE INDEX uk_ipfxts8e4ofm3oo5djk40pv86 ON scim_phonenumber USING btree (value, type);

CREATE INDEX uk_j86m6mxppkb3g2vx72a11xob1 ON scim_email USING btree (value, type);

CREATE INDEX uk_mw914wc9rj4qsue2q60n4ktk4 ON scim_roles USING btree (value);

CREATE INDEX uk_nxxhl5vhce96gwm0se9spjjjv ON scim_entitlements USING btree (value);

CREATE INDEX uk_p2y10qxtuqdvbl5spxu98akx2 ON scim_extension_field_value USING btree (user_internal_id, extension_field_internal_id);

CREATE INDEX uk_tb6nu6msjqh1qb2ne5e4ghnp0 ON scim_certificate USING btree (value);
