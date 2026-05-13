CREATE TABLE IF NOT EXISTS distcomp.tbl_note_by_id (
    id bigint PRIMARY KEY,
    article_id bigint,
    content text,
    state text,
    created_at timestamp,
    updated_at timestamp
);

CREATE TABLE IF NOT EXISTS distcomp.tbl_note_by_article (
    article_id bigint,
    created_at timestamp,
    id bigint,
    content text,
    state text,
    updated_at timestamp,
    PRIMARY KEY ((article_id), created_at, id)
) WITH CLUSTERING ORDER BY (created_at DESC, id DESC);

CREATE TABLE IF NOT EXISTS distcomp.tbl_note_by_bucket (
    bucket tinyint,
    created_at timestamp,
    id bigint,
    article_id bigint,
    content text,
    state text,
    updated_at timestamp,
    PRIMARY KEY ((bucket), created_at, id)
) WITH CLUSTERING ORDER BY (created_at DESC, id DESC);