-- Таблицы для Notice: партиция по issue_id (равномерно по задачам), без ключа country (избегаем hot partition).
-- tbl_notice_by_id — одна строка на id: партиция = id, равномерное распределение по кластеру для точечных чтений.
-- tbl_notice_bucket — шардированный полный список: bucket = id % 256.

CREATE TABLE IF NOT EXISTS tbl_notice_by_issue (
    issue_id bigint,
    id bigint,
    content text,
    state text,
    PRIMARY KEY ((issue_id), id)
) WITH CLUSTERING ORDER BY (id ASC);

CREATE TABLE IF NOT EXISTS tbl_notice_by_id (
    id bigint PRIMARY KEY,
    issue_id bigint,
    content text,
    state text
);

CREATE TABLE IF NOT EXISTS tbl_notice_bucket (
    bucket int,
    id bigint,
    issue_id bigint,
    content text,
    state text,
    PRIMARY KEY ((bucket), id)
) WITH CLUSTERING ORDER BY (id ASC);
