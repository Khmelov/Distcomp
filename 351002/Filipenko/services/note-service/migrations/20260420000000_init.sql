-- +goose Up
-- +goose StatementBegin
-- Схема public: совпадает с search_path по умолчанию в JDBC/клиентах без options,
-- иначе данные оказываются в distcomp.tbl_marker, а запросы вида FROM tbl_marker — в public.

CREATE TABLE tbl_author (
id BIGSERIAL PRIMARY KEY,
login VARCHAR(64) UNIQUE NOT NULL,
password VARCHAR(128) NOT NULL,
firstname VARCHAR(64) NOT NULL,
lastname VARCHAR(64) NOT NULL
);

CREATE TABLE tbl_marker (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE tbl_topic (
       id BIGSERIAL PRIMARY KEY,
       author_id BIGINT NOT NULL REFERENCES tbl_author(id) ON DELETE CASCADE,
       title VARCHAR(64) UNIQUE NOT NULL,
       content VARCHAR(2048) NOT NULL,
       created TIMESTAMP NOT NULL DEFAULT NOW(),
       modified TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tbl_topic_marker (
      topic_id BIGINT NOT NULL REFERENCES tbl_topic(id) ON DELETE CASCADE,
      marker_id BIGINT NOT NULL REFERENCES tbl_marker(id) ON DELETE CASCADE,
      PRIMARY KEY (topic_id, marker_id)
);

-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
DROP TABLE IF EXISTS tbl_topic_marker;
DROP TABLE IF EXISTS tbl_topic;
DROP TABLE IF EXISTS tbl_marker;
DROP TABLE IF EXISTS tbl_author;
-- +goose StatementEnd