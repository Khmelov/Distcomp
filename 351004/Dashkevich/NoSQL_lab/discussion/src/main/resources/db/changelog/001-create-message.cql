CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS distcomp.tbl_message (
    country text,
    news_id bigint,
    id bigint,
    content text,
    PRIMARY KEY ((country), news_id, id)
) WITH CLUSTERING ORDER BY (news_id ASC, id ASC);

CREATE INDEX IF NOT EXISTS idx_tbl_message_id ON distcomp.tbl_message (id);
CREATE INDEX IF NOT EXISTS idx_tbl_message_news_id ON distcomp.tbl_message (news_id);
