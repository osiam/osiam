ALTER TABLE osiam_client CHANGE accesstokenvalidityseconds access_token_validity_seconds int(11) NOT NULL;

ALTER TABLE osiam_client CHANGE refreshtokenvalidityseconds refresh_token_validity_seconds int(11) NOT NULL;

ALTER TABLE osiam_client CHANGE validityinseconds validity_in_seconds bigint(20) NOT NULL;
