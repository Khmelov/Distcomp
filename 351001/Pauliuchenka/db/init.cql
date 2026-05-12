CREATE KEYSPACE IF NOT EXISTS distcomp
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE distcomp;

CREATE TABLE IF NOT EXISTS messages (
    id bigint PRIMARY KEY,
    story_id bigint,
    content text
);
