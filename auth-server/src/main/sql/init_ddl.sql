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
-- Name: auth_server_sequence_osiam_client; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE auth_server_sequence_osiam_client
    START WITH 100
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 162 (class 1259 OID 34625)
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
-- TOC entry 163 (class 1259 OID 34633)
-- Dependencies: 5
-- Name: osiam_client_grants; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE osiam_client_grants (
    id bigint NOT NULL,
    grants character varying(255)
);


--
-- TOC entry 164 (class 1259 OID 34636)
-- Dependencies: 5
-- Name: osiam_client_scopes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE osiam_client_scopes (
    id bigint NOT NULL,
    scope character varying(255)
);

--
-- TOC entry 1906 (class 2606 OID 34632)
-- Dependencies: 162 162 2111
-- Name: osiam_client_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT osiam_client_pkey PRIMARY KEY (internal_id);
    
    --
-- TOC entry 1908 (class 2606 OID 34788)
-- Dependencies: 162 162 2111
-- Name: uk_c34iilt4h1ln91s9ro8m96hru; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client
    ADD CONSTRAINT uk_c34iilt4h1ln91s9ro8m96hru UNIQUE (id);


--
-- TOC entry 1989 (class 2606 OID 34829)
-- Dependencies: 163 162 1905 2111
-- Name: fk_ctvkl0udnj6jpn1p93vbwywte; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client_grants
    ADD CONSTRAINT fk_ctvkl0udnj6jpn1p93vbwywte FOREIGN KEY (id) REFERENCES osiam_client(internal_id);
    
    
--
-- TOC entry 1990 (class 2606 OID 34834)
-- Dependencies: 162 1905 164 2111
-- Name: fk_gl93uw092wua8dl5cpb5ysn3f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY osiam_client_scopes
    ADD CONSTRAINT fk_gl93uw092wua8dl5cpb5ysn3f FOREIGN KEY (id) REFERENCES osiam_client(internal_id);
