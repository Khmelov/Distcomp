-- Run once if tbl_comment / tbl_comment_by_id already existed without `state`:
USE distcomp;
ALTER TABLE tbl_comment ADD state text;
ALTER TABLE tbl_comment_by_id ADD state text;
