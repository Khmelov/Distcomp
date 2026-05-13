-- init.sql
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'distcomp') THEN
        CREATE DATABASE distcomp;
    END IF;
END
$$;

ALTER DATABASE distcomp SET search_path = distcomp, public;

\c distcomp

CREATE SCHEMA IF NOT EXISTS distcomp;
SET search_path TO distcomp;

-- Таблица редакторов
CREATE TABLE tbl_editor (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    login TEXT NOT NULL CHECK (char_length(login) BETWEEN 2 AND 64) UNIQUE,
    password TEXT NOT NULL CHECK (char_length(password) BETWEEN 8 AND 128),
    firstname TEXT NOT NULL CHECK (char_length(firstname) BETWEEN 2 AND 64),
    lastname TEXT NOT NULL CHECK (char_length(lastname) BETWEEN 2 AND 64)
);

-- Таблица историй
CREATE TABLE tbl_story (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    editor_id BIGINT NOT NULL REFERENCES tbl_editor(id),
    title TEXT NOT NULL CHECK (char_length(title) BETWEEN 2 AND 64) UNIQUE,
    content TEXT NOT NULL CHECK (char_length(content) BETWEEN 4 AND 2048),
    created TIMESTAMPTZ NOT NULL,
    modified TIMESTAMPTZ NOT NULL
);

-- Таблица сообщений
CREATE TABLE tbl_message (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    story_id BIGINT NOT NULL REFERENCES tbl_story(id),
    content TEXT NOT NULL CHECK (char_length(content) BETWEEN 2 AND 2048)
);

-- Таблица меток (добавлено уникальное ограничение на имя)
CREATE TABLE tbl_label (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL CHECK (char_length(name) BETWEEN 2 AND 32) UNIQUE
);

-- Связующая таблица Story <-> Label
CREATE TABLE tbl_story_label (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    story_id BIGINT NOT NULL REFERENCES tbl_story(id),
    label_id BIGINT NOT NULL REFERENCES tbl_label(id),
    UNIQUE (story_id, label_id)
);

-- Функция, гарантирующая существование меток по их именам
-- Возвращает таблицу с id и name для всех переданных имён
CREATE OR REPLACE FUNCTION ensure_labels(label_names TEXT[])
RETURNS TABLE(id BIGINT, name TEXT) LANGUAGE plpgsql AS $$
DECLARE
    lbl_name TEXT;
BEGIN
    -- Вставляем отсутствующие метки
    FOREACH lbl_name IN ARRAY label_names
    LOOP
        INSERT INTO tbl_label (name)
        VALUES (lbl_name)
        ON CONFLICT (name) DO NOTHING;
    END LOOP;

    -- Возвращаем id и name для всех запрошенных меток
    RETURN QUERY
    SELECT lbl.id, lbl.name
    FROM tbl_label lbl
    WHERE lbl.name = ANY(label_names);
END;
$$;

-- Функция создания истории вместе с метками
-- Параметры: editor_id, title, content, массив имён меток
-- Возвращает id созданной истории
CREATE OR REPLACE FUNCTION create_story_with_labels(
    p_editor_id BIGINT,
    p_title TEXT,
    p_content TEXT,
    p_label_names TEXT[]
)
RETURNS BIGINT LANGUAGE plpgsql AS $$
DECLARE
    v_story_id BIGINT;
    v_label_record RECORD;
BEGIN
    -- Вставка истории
    INSERT INTO tbl_story (editor_id, title, content, created, modified)
    VALUES (p_editor_id, p_title, p_content, NOW(), NOW())
    RETURNING id INTO v_story_id;

    -- Если переданы метки, связываем их с историей
    IF p_label_names IS NOT NULL AND array_length(p_label_names, 1) > 0 THEN
        -- Создаём недостающие метки и получаем их id
        FOR v_label_record IN
            SELECT id FROM ensure_labels(p_label_names)
        LOOP
            INSERT INTO tbl_story_label (story_id, label_id)
            VALUES (v_story_id, v_label_record.id)
            ON CONFLICT (story_id, label_id) DO NOTHING;
        END LOOP;
    END IF;

    RETURN v_story_id;
END;
$$;