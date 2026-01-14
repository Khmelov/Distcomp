-- schema.sql publisher
-- Создание схемы, если не существует
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'distcomp') THEN
        EXECUTE 'CREATE SCHEMA distcomp';
    END IF;
END
$$;

-- Таблица редакторов
CREATE TABLE IF NOT EXISTS distcomp.tbl_editor (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64) NOT NULL,
    lastname VARCHAR(64) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблица тегов
CREATE TABLE IF NOT EXISTS distcomp.tbl_tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблица тем
CREATE TABLE IF NOT EXISTS distcomp.tbl_topic (
    id BIGSERIAL PRIMARY KEY,
    editor_id BIGINT NOT NULL,
    title VARCHAR(64) NOT NULL,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (editor_id) REFERENCES distcomp.tbl_editor(id) ON DELETE CASCADE
);

-- Таблица связи тем и тегов
CREATE TABLE IF NOT EXISTS distcomp.tbl_topic_tag (
    topic_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (topic_id, tag_id),
    FOREIGN KEY (topic_id) REFERENCES distcomp.tbl_topic(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES distcomp.tbl_tag(id) ON DELETE CASCADE
);

-- Таблица сообщений
CREATE TABLE IF NOT EXISTS distcomp.tbl_message (
    id BIGINT PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    content VARCHAR(2048) NOT NULL,
    editor_id BIGINT,
    country VARCHAR(50),
    state VARCHAR(20) DEFAULT 'PENDING',
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для улучшения производительности
CREATE INDEX IF NOT EXISTS idx_editor_login ON distcomp.tbl_editor(login);
CREATE INDEX IF NOT EXISTS idx_tag_name ON distcomp.tbl_tag(name);
CREATE INDEX IF NOT EXISTS idx_topic_editor ON distcomp.tbl_topic(editor_id);
CREATE INDEX IF NOT EXISTS idx_topic_created ON distcomp.tbl_topic(created);
CREATE INDEX IF NOT EXISTS idx_topic_tag_topic ON distcomp.tbl_topic_tag(topic_id);
CREATE INDEX IF NOT EXISTS idx_topic_tag_tag ON distcomp.tbl_topic_tag(tag_id);
CREATE INDEX IF NOT EXISTS idx_message_topic ON distcomp.tbl_message(topic_id);
CREATE INDEX IF NOT EXISTS idx_message_state ON distcomp.tbl_message(state);
CREATE INDEX IF NOT EXISTS idx_message_created ON distcomp.tbl_message(created);