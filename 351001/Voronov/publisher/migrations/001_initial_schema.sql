-- +goose Up
-- +goose StatementBegin

CREATE SCHEMA IF NOT EXISTS distcomp;
SET search_path TO distcomp, public;

CREATE TABLE IF NOT EXISTS tbl_user (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    login VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64) NOT NULL,
    lastname VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_issue (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES tbl_user(id) ON DELETE CASCADE,
    title VARCHAR(64) NOT NULL,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tbl_label (
    d BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tbl_reaction (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES tbl_issue(id) ON DELETE CASCADE,
    content VARCHAR(2048) NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_issue_label (
    issue_id BIGINT NOT NULL REFERENCES tbl_issue(id) ON DELETE CASCADE,
    label_id BIGINT NOT NULL REFERENCES tbl_label(id) ON DELETE CASCADE,
    PRIMARY KEY (issue_id, label_id)
);

ALTER ROLE postgres SET search_path TO distcomp, public;

-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
DROP TABLE IF EXISTS tbl_issue_label;
DROP TABLE IF EXISTS tbl_reaction;
DROP TABLE IF EXISTS tbl_label;
DROP TABLE IF EXISTS tbl_issue;
DROP TABLE IF EXISTS tbl_user;
-- +goose StatementEnd
