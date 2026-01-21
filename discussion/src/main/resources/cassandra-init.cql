-- Создание keyspace
CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

-- Используем keyspace
USE distcomp;

-- Создание таблицы для реакций (обязательно tbl_ префикс)
CREATE TABLE IF NOT EXISTS tbl_reaction (
    country text,
    tweet_id bigint,
    id bigint,
    content text,
    author_id bigint,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY ((country, tweet_id), id)
) WITH comment = 'Таблица для хранения реакций';