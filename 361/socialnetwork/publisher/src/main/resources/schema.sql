-- Создание схемы, если не существует
CREATE SCHEMA IF NOT EXISTS distcomp;

-- Установка схемы по умолчанию
SET search_path TO distcomp;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS distcomp.tbl_user (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64) NOT NULL,
    lastname VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблица меток
CREATE TABLE IF NOT EXISTS distcomp.tbl_label (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблица твитов
CREATE TABLE IF NOT EXISTS distcomp.tbl_tweet (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES distcomp.tbl_user(id) ON DELETE CASCADE,
    title VARCHAR(64) NOT NULL,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблица сообщений
CREATE TABLE IF NOT EXISTS distcomp.tbl_message (
    id BIGSERIAL PRIMARY KEY,
    tweet_id BIGINT NOT NULL REFERENCES distcomp.tbl_tweet(id) ON DELETE CASCADE,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Таблица связи твитов и меток
CREATE TABLE IF NOT EXISTS distcomp.tbl_tweet_label (
    tweet_id BIGINT NOT NULL REFERENCES distcomp.tbl_tweet(id) ON DELETE CASCADE,
    label_id BIGINT NOT NULL REFERENCES distcomp.tbl_label(id) ON DELETE CASCADE,
    PRIMARY KEY (tweet_id, label_id)
);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_tweet_user_id ON distcomp.tbl_tweet(user_id);
CREATE INDEX IF NOT EXISTS idx_message_tweet_id ON distcomp.tbl_message(tweet_id);
CREATE INDEX IF NOT EXISTS idx_tweet_label_tweet_id ON distcomp.tbl_tweet_label(tweet_id);
CREATE INDEX IF NOT EXISTS idx_tweet_label_label_id ON distcomp.tbl_tweet_label(label_id);
CREATE INDEX IF NOT EXISTS idx_user_login ON distcomp.tbl_user(login);
CREATE INDEX IF NOT EXISTS idx_label_name ON distcomp.tbl_label(name);


