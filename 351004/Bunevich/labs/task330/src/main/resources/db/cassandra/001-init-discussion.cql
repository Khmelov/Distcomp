-- Task330 Cassandra schema for discussion module
-- Keyspace and tables for Post storage moved from PostgreSQL.
-- API compatibility note:
-- - keep direct CRUD by post id
-- - keep efficient reads by story id (one-to-many Story -> Post)
-- - avoid data skew by NOT using constant/small-cardinality partition keys

CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

-- Table for direct access by post id (GET/PUT/DELETE /posts/{id})
CREATE TABLE IF NOT EXISTS distcomp.tbl_post_by_id (
  id uuid PRIMARY KEY,
  story_id bigint,
  content text,
  created_at timestamp,
  modified_at timestamp
);

-- Table for efficient post listing within a story
-- Partition key: story_id (high cardinality in production)
-- Clustering: created_at DESC, id ASC for stable ordering
CREATE TABLE IF NOT EXISTS distcomp.tbl_post_by_story (
  story_id bigint,
  created_at timestamp,
  id uuid,
  content text,
  modified_at timestamp,
  PRIMARY KEY ((story_id), created_at, id)
) WITH CLUSTERING ORDER BY (created_at DESC, id ASC);

-- Optional safety table for reverse lookup if needed by service internals.
CREATE TABLE IF NOT EXISTS distcomp.tbl_post_story_ref (
  id uuid PRIMARY KEY,
  story_id bigint
);
