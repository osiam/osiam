-- Migrationscript from release server version 0.20 to 0.21


-- Drop not used tables
drop table if exists database_scheme_version cascade;
drop table if exists scim_manager cascade;
drop table if exists scim_enterprise cascade;

-- Renamed externalid to external_id
ALTER TABLE scim_id RENAME COLUMN externalid TO external_id;

-- ATTENTION!
-- If you drop this sequence, your auto increment values will be deleted!
-- Make sure you save the value form the sequence beneath and set this value
-- to all the sequences that will be created in this script (START WITH $VALUE)
drop sequence if exists hibernate_sequence cascade;

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