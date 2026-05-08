CREATE SCHEMA IF NOT EXISTS distcomp;
ALTER DATABASE distcomp SET search_path TO distcomp, public;

CREATE TABLE IF NOT EXISTS distcomp.tbl_editor (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64) NOT NULL,
    lastname VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_article (
    id BIGSERIAL PRIMARY KEY,
    editor_id BIGINT NOT NULL REFERENCES distcomp.tbl_editor(id) ON DELETE CASCADE,
    title VARCHAR(64) NOT NULL UNIQUE,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_article_tag (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES distcomp.tbl_article(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES distcomp.tbl_tag(id) ON DELETE CASCADE
);