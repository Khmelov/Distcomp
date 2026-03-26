--liquibase formatted sql
--changeset notebook:000-1
CREATE SCHEMA IF NOT EXISTS distcomp;

--changeset notebook:000-2
CREATE TABLE IF NOT EXISTS distcomp.tbl_writer (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64) NOT NULL,
    lastname VARCHAR(64) NOT NULL
);

--changeset notebook:000-3
CREATE TABLE IF NOT EXISTS distcomp.tbl_marker (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

--changeset notebook:000-4
CREATE TABLE IF NOT EXISTS distcomp.tbl_story (
    id BIGSERIAL PRIMARY KEY,
    writer_id BIGINT NOT NULL REFERENCES distcomp.tbl_writer(id),
    title VARCHAR(64) NOT NULL,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);

--changeset notebook:000-5
CREATE TABLE IF NOT EXISTS distcomp.tbl_notice (
    id BIGSERIAL PRIMARY KEY,
    story_id BIGINT NOT NULL REFERENCES distcomp.tbl_story(id),
    content VARCHAR(2048) NOT NULL
);

--changeset notebook:000-6
CREATE TABLE IF NOT EXISTS distcomp.tbl_story_marker (
    story_id BIGINT NOT NULL REFERENCES distcomp.tbl_story(id),
    marker_id BIGINT NOT NULL REFERENCES distcomp.tbl_marker(id),
    PRIMARY KEY (story_id, marker_id)
);

--changeset notebook:000-7 runInTransaction:false
ALTER DATABASE distcomp SET search_path TO distcomp, public;

--changeset notebook:000-8
ALTER TABLE distcomp.tbl_writer ADD CONSTRAINT uk_tbl_writer_login UNIQUE (login);

--changeset notebook:000-9
ALTER TABLE distcomp.tbl_story ADD CONSTRAINT uk_tbl_story_writer_title UNIQUE (writer_id, title);
