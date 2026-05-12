-- Схема distcomp, таблицы с префиксом tbl_
-- Notice перенесены в модуль discussion (Cassandra)

CREATE SCHEMA IF NOT EXISTS distcomp;

CREATE TABLE distcomp.tbl_editor (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL,
    password VARCHAR(256) NOT NULL,
    firstname VARCHAR(128) NOT NULL,
    lastname VARCHAR(128) NOT NULL,
    role VARCHAR(16) NOT NULL DEFAULT 'CUSTOMER',
    CONSTRAINT uq_tbl_editor_login UNIQUE (login)
);

CREATE TABLE distcomp.tbl_label (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    CONSTRAINT uq_tbl_label_name UNIQUE (name)
);

CREATE TABLE distcomp.tbl_issue (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(512) NOT NULL,
    content TEXT NOT NULL,
    editor_id BIGINT NOT NULL,
    created TIMESTAMPTZ NOT NULL,
    modified TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_tbl_issue_editor FOREIGN KEY (editor_id)
        REFERENCES distcomp.tbl_editor (id) ON DELETE RESTRICT
);

CREATE TABLE distcomp.tbl_issue_label (
    issue_id BIGINT NOT NULL,
    label_id BIGINT NOT NULL,
    PRIMARY KEY (issue_id, label_id),
    CONSTRAINT fk_tbl_issue_label_issue FOREIGN KEY (issue_id)
        REFERENCES distcomp.tbl_issue (id) ON DELETE CASCADE,
    CONSTRAINT fk_tbl_issue_label_label FOREIGN KEY (label_id)
        REFERENCES distcomp.tbl_label (id) ON DELETE CASCADE
);

CREATE INDEX ix_tbl_issue_editor_id ON distcomp.tbl_issue (editor_id);

DO $$
BEGIN
  EXECUTE format(
    'ALTER DATABASE %I SET search_path TO distcomp, public',
    current_database());
END $$;
