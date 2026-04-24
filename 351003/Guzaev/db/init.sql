
CREATE TABLE IF NOT EXISTS tbl_writer (
    id SERIAL PRIMARY KEY,
    login VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64),
    lastname VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS tbl_marker (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_tweet (
    id SERIAL PRIMARY KEY,
    writer_id INTEGER NOT NULL,
    title VARCHAR(64) NOT NULL,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tweet_writer FOREIGN KEY (writer_id) REFERENCES tbl_writer(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tbl_comment (
    id SERIAL PRIMARY KEY,
    tweet_id INTEGER NOT NULL,
    content VARCHAR(2048) NOT NULL,
    CONSTRAINT fk_comment_tweet FOREIGN KEY (tweet_id) REFERENCES tbl_tweet(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tbl_tweet_marker (
    tweet_id INTEGER NOT NULL,
    marker_id INTEGER NOT NULL,
    PRIMARY KEY (tweet_id, marker_id),
    CONSTRAINT fk_tm_tweet FOREIGN KEY (tweet_id) REFERENCES tbl_tweet(id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_marker FOREIGN KEY (marker_id) REFERENCES tbl_marker(id) ON DELETE CASCADE
);
