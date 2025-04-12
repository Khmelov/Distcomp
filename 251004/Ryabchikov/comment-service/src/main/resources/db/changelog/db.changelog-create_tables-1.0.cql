--liquibase formatted sql

--changeset ryabchikov:1
CREATE KEYSPACE IF NOT EXISTS tweet_keyspace
            WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1};

USE tweet_keyspace;

CREATE TABLE tbl_comment
(
    country  text,
    id       bigint,
    tweet_id bigint,
    content  text,
    PRIMARY KEY ((country), id, tweet_id)
);