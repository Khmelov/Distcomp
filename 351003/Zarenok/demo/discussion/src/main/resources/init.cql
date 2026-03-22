CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS distcomp.tbl_message (
    issue_id UUID,
    id UUID,
    content TEXT,
    PRIMARY KEY (issue_id, id)
);

CREATE INDEX IF NOT EXISTS idx_message_id ON distcomp.tbl_message (id);