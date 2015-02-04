-- Migrationscript from release auth version 0.20 to 0.21

-- ATTENTION!
-- If you drop this sequence, your auto increment values will be deleted!
-- Make sure you save the value form the sequence beneath and set this value
-- to all the sequences that will be created in this script (START WITH $VALUE)
drop sequence if exists hibernate_sequence cascade;

CREATE SEQUENCE auth_server_sequence_osiam_client
    START WITH 100
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;