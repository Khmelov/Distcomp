CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS distcomp.tbl_post (
  country text,
  article_id bigint,
  id bigint,
  content text,
  PRIMARY KEY ((country), article_id, id)
);
