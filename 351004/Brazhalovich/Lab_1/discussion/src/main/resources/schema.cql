CREATE KEYSPACE IF NOT EXISTS distcomp
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS distcomp.tbl_comment (
                                                    id bigint PRIMARY KEY,
                                                    news_id bigint,
                                                    content text,
                                                    state text
);

ALTER TABLE distcomp.tbl_comment ADD user_id bigint;

CREATE INDEX IF NOT EXISTS idx_comment_news_id ON distcomp.tbl_comment (news_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON distcomp.tbl_comment (user_id);