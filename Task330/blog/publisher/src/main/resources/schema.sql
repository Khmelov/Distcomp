-- Создаем схему, если она не существует
CREATE SCHEMA IF NOT EXISTS distcomp;

-- Устанавливаем схему по умолчанию
SET search_path TO distcomp;

-- Таблица редакторов
CREATE TABLE IF NOT EXISTS tbl_editor (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64) NOT NULL,
    lastname VARCHAR(64) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);

-- Таблица тегов
CREATE TABLE IF NOT EXISTS tbl_tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);

-- Таблица тем (БЕЗ сообщений - они в отдельной базе Cassandra)
CREATE TABLE IF NOT EXISTS tbl_topic (
    id BIGSERIAL PRIMARY KEY,
    editor_id BIGINT NOT NULL REFERENCES tbl_editor(id) ON DELETE CASCADE,
    title VARCHAR(64) NOT NULL,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);

-- Таблица связи тем и тегов (многие ко многим)
CREATE TABLE IF NOT EXISTS tbl_topic_tag (
    topic_id BIGINT NOT NULL REFERENCES tbl_topic(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tbl_tag(id) ON DELETE CASCADE,
    PRIMARY KEY (topic_id, tag_id)
);

-- УДАЛЕНА таблица сообщений - теперь в Cassandra

-- Индексы для улучшения производительности
CREATE INDEX IF NOT EXISTS idx_topic_editor_id ON tbl_topic(editor_id);
CREATE INDEX IF NOT EXISTS idx_topic_tag_topic_id ON tbl_topic_tag(topic_id);
CREATE INDEX IF NOT EXISTS idx_topic_tag_tag_id ON tbl_topic_tag(tag_id);
CREATE INDEX IF NOT EXISTS idx_editor_login ON tbl_editor(login);
CREATE INDEX IF NOT EXISTS idx_tag_name ON tbl_tag(name);