--
-- TOC entry 162 (class 1259 OID 34625)
-- Dependencies: 5
-- Name: osiam_client; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE osiam_client (
    internal_id bigint NOT NULL AUTO_INCREMENT,
    accesstokenvalidityseconds integer NOT NULL,
    client_secret character varying(255) NOT NULL,
    expiry datetime NULL,
    id character varying(32) NOT NULL,
    implicit_approval boolean NOT NULL,
    redirect_uri longtext NOT NULL,
    refreshtokenvalidityseconds integer NOT NULL,
    validityinseconds bigint NOT NULL,
    primary key (internal_id)
) ENGINE=InnoDB;

ALTER TABLE osiam_client AUTO_INCREMENT = 100;

-- TOC entry 163 (class 1259 OID 34633)
-- Dependencies: 5
-- Name: osiam_client_grants; Type: TABLE; Schema: public; Owner: -
CREATE TABLE osiam_client_grants (
    id bigint NOT NULL,
    grants character varying(255)
) ENGINE=InnoDB;

--
-- TOC entry 164 (class 1259 OID 34636)
-- Dependencies: 5
-- Name: osiam_client_scopes; Type: TABLE; Schema: public; Owner: -
--
CREATE TABLE osiam_client_scopes (
    id bigint NOT NULL,
    scope character varying(255)
) ENGINE=InnoDB;

-- TOC entry 1908 (class 2606 OID 34788)
-- Dependencies: 162 162 2111
-- Name: uk_c34iilt4h1ln91s9ro8m96hru; Type: CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE osiam_client
    ADD CONSTRAINT uk_c34iilt4h1ln91s9ro8m96hru UNIQUE (id);

--
-- TOC entry 1989 (class 2606 OID 34829)
-- Dependencies: 163 162 1905 2111
-- Name: fk_ctvkl0udnj6jpn1p93vbwywte; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE osiam_client_grants
    ADD CONSTRAINT fk_ctvkl0udnj6jpn1p93vbwywte FOREIGN KEY (id) REFERENCES osiam_client(internal_id);

--
-- TOC entry 1990 (class 2606 OID 34834)
-- Dependencies: 162 1905 164 2111
-- Name: fk_gl93uw092wua8dl5cpb5ysn3f; Type: FK CONSTRAINT; Schema: public; Owner: -
--
ALTER TABLE osiam_client_scopes
    ADD CONSTRAINT fk_gl93uw092wua8dl5cpb5ysn3f FOREIGN KEY (id) REFERENCES osiam_client(internal_id);

--
-- Example Client
--
INSERT INTO osiam_client (internal_id, accesstokenvalidityseconds, client_secret, expiry,
            id, implicit_approval, redirect_uri, refreshtokenvalidityseconds,
            validityinseconds)
    VALUES (1, 28800, 'secret', null,
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
