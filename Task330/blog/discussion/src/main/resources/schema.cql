-- Создание ключевого пространства
CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

-- Использование ключевого пространства
USE distcomp;

-- Таблица сообщений с составным первичным ключом
-- country - ключ раздела (partition key)
-- topic_id, id - ключи кластеризации
CREATE TABLE IF NOT EXISTS tbl_message (
    country TEXT,
    topic_id BIGINT,
    id BIGINT,
    content TEXT,
    created TIMESTAMP,
    modified TIMESTAMP,
    PRIMARY KEY ((country), topic_id, id)
) WITH CLUSTERING ORDER BY (topic_id ASC, id ASC);