-- Test connection script
-- Run this to verify database and connection settings

-- Check if database exists
SELECT datname FROM pg_database WHERE datname = 'distcomp';

-- Connect to distcomp database and check schema
\c distcomp

-- Check if schema exists
SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'distcomp';

-- Check tables
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'distcomp' AND table_name LIKE 'tbl_%';

-- Check user permissions
SELECT has_database_privilege('postgres', 'distcomp', 'CONNECT');
SELECT has_schema_privilege('postgres', 'distcomp', 'USAGE');

