CREATE KEYSPACE IF NOT EXISTS distcomp
    WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE distcomp;

-- ВНИМАНИЕ! Правильный ключ партиционирования - tweet_id для равномерного распределения
CREATE TABLE IF NOT EXISTS tbl_notice (
                                          id UUID,
                                          tweet_id UUID,
                                          content TEXT,
                                          created TIMESTAMP,
                                          modified TIMESTAMP,
                                          PRIMARY KEY ((tweet_id), id)
);

CREATE INDEX IF NOT EXISTS idx_notice_id ON tbl_notice (id);