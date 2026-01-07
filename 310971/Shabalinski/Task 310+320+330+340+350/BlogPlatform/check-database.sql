-- SQL script to check database setup
-- Run this in psql: psql -U postgres -f check-database.sql

-- 1. Check if distcomp database exists
\echo '=== Checking database ==='
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM pg_database WHERE datname = 'distcomp') 
        THEN 'Database distcomp EXISTS'
        ELSE 'Database distcomp DOES NOT EXIST - Run: CREATE DATABASE distcomp;'
    END AS database_status;

-- 2. Connect to distcomp database
\c distcomp

-- 3. Check if schema exists
\echo ''
\echo '=== Checking schema ==='
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'distcomp') 
        THEN 'Schema distcomp EXISTS'
        ELSE 'Schema distcomp DOES NOT EXIST - Will be created by Liquibase'
    END AS schema_status;

-- 4. Check tables
\echo ''
\echo '=== Checking tables ==='
SELECT 
    CASE 
        WHEN COUNT(*) > 0 
        THEN 'Found ' || COUNT(*) || ' table(s)'
        ELSE 'No tables found - Will be created by Liquibase'
    END AS tables_status
FROM information_schema.tables 
WHERE table_schema = 'distcomp' AND table_name LIKE 'tbl_%';

-- 5. List all tables
\echo ''
\echo '=== List of tables ==='
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'distcomp' AND table_name LIKE 'tbl_%'
ORDER BY table_name;

-- 6. Test sample query
\echo ''
\echo '=== Testing sample query ==='
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'distcomp' AND table_name = 'tbl_user')
        THEN 'Table tbl_user exists - Query test: ' || (SELECT COUNT(*)::text FROM distcomp.tbl_user) || ' user(s)'
        ELSE 'Table tbl_user does not exist yet'
    END AS query_test;

-- 7. Check user permissions
\echo ''
\echo '=== Checking permissions ==='
SELECT 
    has_database_privilege('postgres', 'distcomp', 'CONNECT') AS can_connect,
    has_schema_privilege('postgres', 'distcomp', 'USAGE') AS can_use_schema;

