-- Complete database cleanup script for entities-app
-- Run this script in PostgreSQL to clean up the database before starting the application

-- Connect to postgres database first
-- Then run these commands:

-- 1. Drop the distcomp schema completely (this will remove all tables, data, and tracking info)
DROP SCHEMA IF EXISTS distcomp CASCADE;

-- 2. Recreate the distcomp schema
CREATE SCHEMA distcomp;

-- 3. Verify the cleanup (optional)
-- You can run this to see that the schema is empty:
-- SELECT schemaname, tablename FROM pg_tables WHERE schemaname = 'distcomp';