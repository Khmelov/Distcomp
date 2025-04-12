CREATE OR REPLACE FUNCTION cleanup_unused_marks_trigger() RETURNS TRIGGER
    LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM tbl_mark
    WHERE id NOT IN (SELECT DISTINCT mark_id FROM m2m_tweet_mark);
    RETURN NULL;
END; $$;

CREATE OR REPLACE TRIGGER cleanup_unused_labels
    AFTER DELETE
    ON m2m_tweet_mark
    FOR EACH ROW EXECUTE FUNCTION cleanup_unused_marks_trigger();