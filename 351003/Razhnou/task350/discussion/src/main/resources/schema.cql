CREATE KEYSPACE IF NOT EXISTS task350
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE task350;

CREATE TABLE IF NOT EXISTS tbl_reaction (
    id bigint PRIMARY KEY,
    tweet_id bigint,
    content text,
    state text
);
