-- Создание keyspace, если не существует
CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
}
AND durable_writes = true;

-- Использование keyspace
USE distcomp;

-- Создание таблицы сообщений
CREATE TABLE IF NOT EXISTS tbl_message (
    country text,
    tweet_id bigint,
    id bigint,
    content text,
    PRIMARY KEY ((country), tweet_id, id)
) WITH CLUSTERING ORDER BY (tweet_id DESC, id DESC);

-- Создание индекса для поиска по tweet_id
CREATE INDEX IF NOT EXISTS idx_tweet_id ON tbl_message (tweet_id);