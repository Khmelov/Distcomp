CREATE TABLE tbl_creator (
    id SERIAL PRIMARY KEY,
    login TEXT NOT NULL UNIQUE CHECK (char_length(login) BETWEEN 2 AND 64),
    password TEXT NOT NULL CHECK (char_length(password) BETWEEN 8 AND 124),
    firstname TEXT NOT NULL CHECK (char_length(firstname) BETWEEN 2 AND 64),
    lastname TEXT NOT NULL CHECK (char_length(lastname) BETWEEN 2 AND 64)
);

CREATE TABLE tbl_issue (
    id SERIAL PRIMARY KEY,
    creator_id BIGINT NOT NULL REFERENCES tbl_creator(id),
    title TEXT NOT NULL UNIQUE CHECK (char_length(title) BETWEEN 2 AND 64),
    content TEXT NOT NULL CHECK (char_length(content) BETWEEN 4 AND 2048),
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified TIMESTAMPTZ DEFAULT NULL
);

CREATE TABLE tbl_message (
    id SERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES tbl_issue(id),
    content TEXT NOT NULL CHECK(char_length(content) BETWEEN 2 AND 2048)
);

CREATE TABLE tbl_mark (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE CHECK(char_length(name) BETWEEN 2 AND 32) NOT NULL
);

CREATE TABLE tbl_issue_mark (
    id SERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES tbl_issue(id),
    mark_id BIGINT NOT NULL REFERENCES tbl_mark(id)
);

ALTER TABLE tbl_issue DROP CONSTRAINT IF EXISTS tbl_issue_creator_id_fkey,
ADD CONSTRAINT tbl_issue_creator_id_fkey
FOREIGN KEY (creator_id) REFERENCES tbl_creator(id)
ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS tbl_issue_mark (
    issue_id BIGINT NOT NULL,
    mark_id BIGINT NOT NULL,
    PRIMARY KEY (issue_id, mark_id),
    FOREIGN KEY (issue_id) REFERENCES tbl_issue(id) ON DELETE CASCADE,
    FOREIGN KEY (mark_id) REFERENCES tbl_mark(id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION delete_orphan_marks()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM tbl_mark WHERE id = OLD.mark_id AND NOT EXISTS (
        SELECT 1 FROM tbl_issue_mark
        WHERE mark_id = OLD.mark_id
    );

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER after_delete_issue_mark AFTER DELETE ON tbl_issue_mark
    FOR EACH ROW EXECUTE FUNCTION delete_orphan_marks();