-- создаём keyspace testkeyspace (если не существует)
CREATE KEYSPACE IF NOT EXISTS testkeyspace
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
  AND durable_writes = true;

-- переключаемся на keyspace
USE testkeyspace;

-- создаём таблицу соответствующую модели:
-- PRIMARY KEY ((country), tweet_id, id)
CREATE TABLE IF NOT EXISTS tbl_comment (
  country text,
  tweet_id bigint,
  id bigint,
  content text,
  PRIMARY KEY ((country), tweet_id, id)
);