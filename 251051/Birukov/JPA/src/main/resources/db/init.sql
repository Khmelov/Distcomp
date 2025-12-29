CREATE SCHEMA IF NOT EXISTS distcomp AUTHORIZATION postgres;

SET search_path TO distcomp;

GRANT ALL PRIVILEGES ON SCHEMA distcomp TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA distcomp TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA distcomp TO postgres;

ALTER ROLE postgres SET search_path TO distcomp;