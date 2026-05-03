CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS distcomp.tbl_post_by_id (
  id bigint PRIMARY KEY,
  story_id bigint,
  content text,
  created_at timestamp,
  modified_at timestamp
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_post_by_story (
  story_id bigint,
  id bigint,
  content text,
  created_at timestamp,
  modified_at timestamp,
  PRIMARY KEY ((story_id), id)
);
