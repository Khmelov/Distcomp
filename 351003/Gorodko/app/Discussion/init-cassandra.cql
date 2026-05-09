CREATE KEYSPACE IF NOT EXISTS distcomp 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE distcomp;

CREATE TABLE IF NOT EXISTS tbl_reaction (
    country text,
    tweetId bigint,
    id bigint,
    content text,
    created timestamp,
    PRIMARY KEY ((country), tweetId, id)
) WITH CLUSTERING ORDER BY (tweetId ASC, id ASC);

CREATE INDEX IF NOT EXISTS ON tbl_reaction (tweetId);
CREATE INDEX IF NOT EXISTS ON tbl_reaction (id);

INSERT INTO tbl_reaction (country, tweetId, id, content, created) 
VALUES ('by', 1, 1, 'Test reaction 1', toTimestamp(now()));

INSERT INTO tbl_reaction (country, tweetId, id, content, created) 
VALUES ('by', 1, 2, 'Test reaction 2', toTimestamp(now()));

INSERT INTO tbl_reaction (country, tweetId, id, content, created) 
VALUES ('ru', 1, 3, 'Тестовая реакция', toTimestamp(now()));