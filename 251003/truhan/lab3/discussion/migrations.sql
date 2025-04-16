CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1};

USE distcomp;

CREATE TABLE IF NOT EXISTS tbl_message (
    id bigint,
    issue_id bigint,
    content text,
    PRIMARY KEY (id)
);