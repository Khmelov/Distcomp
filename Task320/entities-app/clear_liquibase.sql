-- Clear Liquibase checksums to allow changeset modifications
-- Run this in the distcomp database

-- Connect to distcomp database and execute:
DELETE FROM databasechangelog;