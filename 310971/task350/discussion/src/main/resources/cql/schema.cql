-- Create keyspace if not exists
CREATE KEYSPACE IF NOT EXISTS distcomp
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

USE distcomp;

-- Create table for messages
CREATE TABLE IF NOT EXISTS tbl_message (
    country TEXT,
    tweet_id BIGINT,
    id BIGINT,
    content TEXT,
    state TEXT,
    PRIMARY KEY (country, tweet_id, id)
) WITH CLUSTERING ORDER BY (tweet_id ASC, id ASC);

