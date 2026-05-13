CREATE SCHEMA IF NOT EXISTS distcomp;

CREATE TABLE IF NOT EXISTS distcomp.tbl_creator (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64) NOT NULL,
    lastname VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_topic (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    creator_id BIGINT NOT NULL REFERENCES distcomp.tbl_creator(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_mark (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_post (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    topic_id BIGINT NOT NULL REFERENCES distcomp.tbl_topic(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_topic_mark (
    topic_id BIGINT NOT NULL REFERENCES distcomp.tbl_topic(id) ON DELETE CASCADE,
    mark_id BIGINT NOT NULL REFERENCES distcomp.tbl_mark(id) ON DELETE CASCADE,
    PRIMARY KEY (topic_id, mark_id)
);
