-- Keyspace required by the assignment: distcomp
CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE distcomp;

-- Main table required by tests (queried with ALLOW FILTERING by id)
-- Partition by story_id for fast access to "comments by story"
CREATE TABLE IF NOT EXISTS tbl_comment (
  story_id bigint,
  id bigint,
  content text,
  state text,
  PRIMARY KEY ((story_id), id)
);

-- Helper table for fast access by id (discussion REST GET /comments/{id})
CREATE TABLE IF NOT EXISTS tbl_comment_by_id (
  id bigint PRIMARY KEY,
  story_id bigint,
  content text,
  state text
);

