--liquibase formatted sql
--changeset notebook:001-1
INSERT INTO distcomp.tbl_writer (id, login, password, firstname, lastname)
VALUES (69, 'writer69', 'password69', 'First69', 'Last69')
ON CONFLICT (id) DO NOTHING;

--changeset notebook:001-2
INSERT INTO distcomp.tbl_story (id, writer_id, title, content, created, modified)
VALUES (59, 69, 'Story 59', 'Content for story 59.', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

--changeset notebook:001-3
INSERT INTO distcomp.tbl_marker (id, name)
VALUES (1001, 'red69'), (1002, 'green69'), (1003, 'blue69')
ON CONFLICT (id) DO NOTHING;

--changeset notebook:001-4
INSERT INTO distcomp.tbl_story_marker (story_id, marker_id)
VALUES (59, 1001), (59, 1002), (59, 1003)
ON CONFLICT (story_id, marker_id) DO NOTHING;

--changeset notebook:001-5
SELECT setval(pg_get_serial_sequence('distcomp.tbl_writer', 'id'), (SELECT COALESCE(MAX(id), 69) FROM distcomp.tbl_writer));

--changeset notebook:001-6
SELECT setval(pg_get_serial_sequence('distcomp.tbl_story', 'id'), (SELECT COALESCE(MAX(id), 59) FROM distcomp.tbl_story));

--changeset notebook:001-7
SELECT setval(pg_get_serial_sequence('distcomp.tbl_marker', 'id'), (SELECT COALESCE(MAX(id), 1003) FROM distcomp.tbl_marker));
