-- schema.cql discussion
DROP KEYSPACE IF EXISTS distcomp;

CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

USE distcomp;

CREATE TABLE IF NOT EXISTS tbl_message (
    country TEXT,
    topic_id BIGINT,
    id BIGINT,
    content TEXT,
    editor_id BIGINT,
    state TEXT,
    created TIMESTAMP,
    modified TIMESTAMP,
    PRIMARY KEY ((country), topic_id, id)
) WITH CLUSTERING ORDER BY (topic_id ASC, id ASC);

CREATE INDEX IF NOT EXISTS idx_message_state ON tbl_message(state);
CREATE INDEX IF NOT EXISTS idx_message_editor ON tbl_message(editor_id);