-- Создаём keyspace
CREATE KEYSPACE IF NOT EXISTS distcomp
    WITH REPLICATION = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

USE distcomp;

-- Таблица для Notice
-- ВНИМАНИЕ! Правильный ключ партиционирования - tweet_id
-- Это обеспечивает равномерное распределение данных по нодам
CREATE TABLE IF NOT EXISTS tbl_notice (
                                          tweet_id BIGINT,
                                          id BIGINT,
                                          content TEXT,
                                          state TEXT,
                                          created TIMESTAMP,
                                          modified TIMESTAMP,
                                          PRIMARY KEY ((tweet_id), id)
);

-- Индекс для поиска по id (неэффективно, но для тестов)
CREATE INDEX IF NOT EXISTS idx_notice_id ON tbl_notice (id);