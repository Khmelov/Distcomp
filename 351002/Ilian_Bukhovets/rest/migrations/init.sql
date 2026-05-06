CREATE TABLE tbl_author (
    id bigserial PRIMARY KEY,
    login TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    firstname TEXT NOT NULL,
    lastname TEXT NOT NULL
);

INSERT INTO tbl_author (login, password, firstname, lastname) VALUES
    ('curcur334@gmail.com', 'admin', 'Илиан', 'Буховец');

CREATE TABLE tbl_issue (
   id bigserial PRIMARY KEY,
   author_id bigint NOT NULL,
   title TEXT NOT NULL UNIQUE,
   content TEXT NOT NULL,
   created TIMESTAMP NOT NULL,
   modified TIMESTAMP NOT NULL,
   FOREIGN KEY (author_id) REFERENCES tbl_author(id) ON DELETE CASCADE
);

CREATE TABLE tbl_comment (
     id bigserial PRIMARY KEY,
     issue_id bigint NOT NULL,
     content TEXT NOT NULL,
     FOREIGN KEY (issue_id) REFERENCES tbl_issue(id) ON DELETE CASCADE
);

CREATE TABLE tbl_marker (
    id bigserial PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE tbl_issue_marker (
    issue_id bigint NOT NULL,
    marker_id bigint NOT NULL,
    FOREIGN KEY (issue_id) REFERENCES tbl_issue(id) ON DELETE CASCADE,
    FOREIGN KEY (marker_id) REFERENCES tbl_marker(id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION delete_orphan_markers()
    RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM tbl_marker
    WHERE id NOT IN (SELECT DISTINCT marker_id FROM tbl_issue_marker);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_delete_orphan_markers
    AFTER DELETE ON tbl_issue_marker
    FOR EACH STATEMENT
EXECUTE FUNCTION delete_orphan_markers();