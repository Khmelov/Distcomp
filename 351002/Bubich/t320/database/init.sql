-- Создание схемы
CREATE SCHEMA IF NOT EXISTS distcomp;

-- Установка схемы по умолчанию
SET search_path TO distcomp;

-- Таблица писателей
CREATE TABLE IF NOT EXISTS distcomp.tbl_writer (
    id BIGSERIAL PRIMARY KEY,
    login TEXT NOT NULL UNIQUE CHECK (length(login) BETWEEN 2 AND 64),
    password TEXT NOT NULL CHECK (length(password) BETWEEN 8 AND 128),
    firstname TEXT NOT NULL CHECK (length(firstname) BETWEEN 2 AND 64),
    lastname TEXT NOT NULL CHECK (length(lastname) BETWEEN 2 AND 64)
);

-- Таблица историй
CREATE TABLE IF NOT EXISTS distcomp.tbl_story (
    id BIGSERIAL PRIMARY KEY,
    writer_id BIGINT NOT NULL REFERENCES distcomp.tbl_writer(id) ON DELETE CASCADE,
    title TEXT NOT NULL CHECK (length(title) BETWEEN 2 AND 64),
    content TEXT NOT NULL CHECK (length(content) BETWEEN 4 AND 2048),
    created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Таблица меток
CREATE TABLE IF NOT EXISTS distcomp.tbl_mark (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL CHECK (length(name) BETWEEN 2 AND 32)
);

-- Таблица комментариев
CREATE TABLE IF NOT EXISTS distcomp.tbl_comment (
    id BIGSERIAL PRIMARY KEY,
    story_id BIGINT NOT NULL REFERENCES distcomp.tbl_story(id) ON DELETE CASCADE,
    content TEXT NOT NULL CHECK (length(content) BETWEEN 2 AND 2048)
);

-- Связующая таблица Story-Mark (многие-ко-многим)
CREATE TABLE IF NOT EXISTS distcomp.tbl_story_mark (
    id BIGSERIAL PRIMARY KEY,
    story_id BIGINT NOT NULL REFERENCES distcomp.tbl_story(id) ON DELETE CASCADE,
    mark_id BIGINT NOT NULL REFERENCES distcomp.tbl_mark(id) ON DELETE CASCADE,
    UNIQUE(story_id, mark_id)
);

-- Индексы для ускорения поиска
CREATE INDEX IF NOT EXISTS idx_story_writer ON distcomp.tbl_story(writer_id);
CREATE INDEX IF NOT EXISTS idx_story_title ON distcomp.tbl_story(title);
CREATE INDEX IF NOT EXISTS idx_comment_story ON distcomp.tbl_comment(story_id);
CREATE INDEX IF NOT EXISTS idx_story_mark_story ON distcomp.tbl_story_mark(story_id);
CREATE INDEX IF NOT EXISTS idx_story_mark_mark ON distcomp.tbl_story_mark(mark_id);
CREATE INDEX IF NOT EXISTS idx_writer_login ON distcomp.tbl_writer(login);

-- Вставка начальных данных
-- Первая запись Writer должна содержать login: bubichviktor@gmail.com
INSERT INTO distcomp.tbl_writer (login, password, firstname, lastname)
VALUES ('bubichviktor@gmail.com', 'securepassword123', 'Виктор', 'Бубич');