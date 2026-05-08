CREATE KEYSPACE IF NOT EXISTS distcomp 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE distcomp;

CREATE TABLE IF NOT EXISTS tbl_comment (
    article_id bigint,
    id bigint,
    editor_id bigint,
    content text,
    state text,
    PRIMARY KEY ((article_id), id)
);