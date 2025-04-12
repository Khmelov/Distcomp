CREATE KEYSPACE IF NOT EXISTS distcomp 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE distcomp;

CREATE TABLE IF NOT EXISTS tbl_note (
  "id" INT,
  "articleId" INT,
  "content" TEXT,
  "country" TEXT,
  PRIMARY KEY (("country"), "articleId", "id")
);