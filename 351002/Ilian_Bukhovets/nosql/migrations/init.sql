CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

USE distcomp;

CREATE TABLE IF NOT EXISTS tbl_comment (
    id INT PRIMARY KEY,
    issue_id BIGINT,
    country TEXT
);