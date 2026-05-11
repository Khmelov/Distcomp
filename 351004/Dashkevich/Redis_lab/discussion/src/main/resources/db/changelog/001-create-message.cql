CREATE TABLE IF NOT EXISTS tbl_message (
    country text,
    news_id bigint,
    id bigint,
    content text,
    state text,
    PRIMARY KEY ((country), news_id, id)
);
