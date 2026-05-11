CREATE KEYSPACE IF NOT EXISTS discussion
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

USE discussion;

CREATE TABLE tbl_notice_by_news (
    news_id INT,
    id UUID,
    content TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY (news_id, id)
) WITH CLUSTERING ORDER BY (id DESC);

CREATE TABLE tbl_notice_by_id (
    id UUID PRIMARY KEY,
    news_id INT,
    content TEXT,
    created_at TIMESTAMP
);