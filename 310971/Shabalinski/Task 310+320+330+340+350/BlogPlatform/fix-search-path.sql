-- Fix search_path for distcomp database
-- This allows queries without schema prefix (e.g., SELECT * FROM tbl_user)
-- Run: psql -U postgres -d distcomp -f fix-search-path.sql

-- Set search_path for the database (applies to all new connections)
ALTER DATABASE distcomp SET search_path TO distcomp, public;

-- Also set for the current session
SET search_path TO distcomp, public;

-- Verify
SELECT current_setting('search_path') AS current_search_path;

-- Test query without schema prefix
SELECT COUNT(*) AS user_count FROM tbl_user;
SELECT COUNT(*) AS article_count FROM tbl_article;

\echo 'Search path configured successfully!'
\echo 'Queries like "SELECT * FROM tbl_user" should now work.'

