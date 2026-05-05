--liquibase formatted sql
--changeset your_name:1
CREATE TABLE IF NOT EXISTS distcomp.tbl_post (
    topic_id bigint,
    id bigint,
    content text,
    PRIMARY KEY ((topic_id), id)
) WITH CLUSTERING ORDER BY (id DESC);