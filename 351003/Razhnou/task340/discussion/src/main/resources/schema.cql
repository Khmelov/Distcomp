CREATE TABLE IF NOT EXISTS distcomp.tbl_reaction (
    id bigint PRIMARY KEY,
    tweet_id bigint,
    content text,
    state text
);
