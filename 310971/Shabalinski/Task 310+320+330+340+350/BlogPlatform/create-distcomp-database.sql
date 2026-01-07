-- Script to create distcomp database for test system compatibility
-- Execute this script in PostgreSQL before running tests that require direct database access
-- 
-- Method 1: Using psql command line (Windows):
--   psql -U postgres -f create-distcomp-database.sql
--
-- Method 2: Using psql interactively:
--   psql -U postgres
--   Then copy and paste the command below:
--
-- Method 3: Using pgAdmin or other GUI tool:
--   Connect to PostgreSQL server, then execute the command below

CREATE DATABASE distcomp;

-- Grant privileges (optional, if needed)
-- GRANT ALL PRIVILEGES ON DATABASE distcomp TO postgres;

-- After creating the database, you may need to run Liquibase migrations
-- to create the schema and tables in the distcomp database

