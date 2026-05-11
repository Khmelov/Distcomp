-- liquibase formatted sql

-- changeset author:1
CREATE KEYSPACE IF NOT EXISTS distcomp 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

-- changeset author:2
CREATE TABLE IF NOT EXISTS distcomp.tbl_comment (
    story_id bigint,
    id bigint,
    content text,
    country text,
    created timestamp,
    PRIMARY KEY (story_id, id)
) WITH CLUSTERING ORDER BY (id DESC);

-- changeset author:3
CREATE INDEX IF NOT EXISTS idx_comment_country 
ON distcomp.tbl_comment (country);