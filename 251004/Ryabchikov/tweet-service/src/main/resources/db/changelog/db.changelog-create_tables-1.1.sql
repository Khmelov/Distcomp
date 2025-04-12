--liquibase formatted sql

--changeset ryabchikov:1
CREATE TABLE m2m_tweet_mark
(
    mark_id  BIGINT NOT NULL,
    tweet_id BIGINT NOT NULL,
    CONSTRAINT fk_mark FOREIGN KEY (mark_id) REFERENCES tbl_mark (id) ON DELETE CASCADE,
    CONSTRAINT fk_tweet FOREIGN KEY (tweet_id) REFERENCES tbl_tweet (id) ON DELETE CASCADE
);