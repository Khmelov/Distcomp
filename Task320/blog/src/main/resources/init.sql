-- Create database if not exists
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'distcomp') THEN
      PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE distcomp');
   END IF;
END
$$;

-- Switch to distcomp database
\c distcomp;

-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS distcomp;