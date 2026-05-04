-- Keyspace и таблицы для модуля discussion (схема distcomp, префикс tbl_).
-- Дублирует логику src/core/database.py для ручного применения или справки.

CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS distcomp.tbl_notes_by_news (
    news_id bigint,
    id bigint,
    content text,
    PRIMARY KEY ((news_id), id)
);

-- id_bucket = id % 64 (или NOTE_ID_BUCKETS) — равномерное распределение по партициям при точечном доступе по id
CREATE TABLE IF NOT EXISTS distcomp.tbl_note_by_id (
    id_bucket int,
    id bigint,
    news_id bigint,
    content text,
    PRIMARY KEY ((id_bucket), id)
);
