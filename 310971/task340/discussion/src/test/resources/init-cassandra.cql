CREATE KEYSPACE IF NOT EXISTS distcomp
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

USE distcomp;

CREATE TABLE IF NOT EXISTS tbl_message (
    country TEXT,
    tweet_id BIGINT,
    id BIGINT,
    content TEXT,
    PRIMARY KEY (country, tweet_id, id)
) WITH CLUSTERING ORDER BY (tweet_id ASC, id ASC);

