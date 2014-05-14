-- Migrationscript from release auth version 0.20 to 0.21

-- ATTENTION!
-- Please check the name of the unique constraint of the column 'client_secret'
-- before you delete the following constraint
--
    
ALTER TABLE osiam_client DROP CONSTRAINT uk_ktjxo7vyfs0veopytnh1x68sm;


-- ATTENTION!
-- Please check the name of the unique constraint of the column 'redirect_uri'
-- before you delete the following constraint
--
    
ALTER TABLE osiam_client DROP CONSTRAINT uk_jj3o15pxbkaf4p88paf4l6ax0;