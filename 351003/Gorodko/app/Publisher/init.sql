CREATE TABLE IF NOT EXISTS tbl_editor (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    firstname VARCHAR(64) NOT NULL,
    lastname VARCHAR(64) NOT NULL,
    CONSTRAINT ck_editor_login_length CHECK (length(login) BETWEEN 2 AND 64),
    CONSTRAINT ck_editor_password_length CHECK (length(password) BETWEEN 8 AND 128),
    CONSTRAINT ck_editor_firstname_length CHECK (length(firstname) BETWEEN 2 AND 64),
    CONSTRAINT ck_editor_lastname_length CHECK (length(lastname) BETWEEN 2 AND 64)
);

CREATE TABLE IF NOT EXISTS tbl_tweet (
    id BIGSERIAL PRIMARY KEY,
    editor_id BIGINT NOT NULL,
    title VARCHAR(64) NOT NULL,
    content VARCHAR(2048) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_tweet_editor FOREIGN KEY (editor_id) REFERENCES tbl_editor(id),
    CONSTRAINT ck_tweet_title_length CHECK (length(title) BETWEEN 2 AND 64),
    CONSTRAINT ck_tweet_content_length CHECK (length(content) BETWEEN 4 AND 2048)
);

CREATE TABLE IF NOT EXISTS tbl_sticker (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    CONSTRAINT ck_sticker_name_length CHECK (length(name) BETWEEN 2 AND 32)
);


CREATE TABLE IF NOT EXISTS tbl_tweet_sticker (
    tweet_id BIGINT NOT NULL,
    sticker_id BIGINT NOT NULL,
    CONSTRAINT pk_tweet_sticker PRIMARY KEY (tweet_id, sticker_id),
    CONSTRAINT fk_tweetsticker_tweet 
        FOREIGN KEY (tweet_id) 
        REFERENCES tbl_tweet(id) 
        ON DELETE CASCADE, 
    CONSTRAINT fk_tweetsticker_sticker 
        FOREIGN KEY (sticker_id) 
        REFERENCES tbl_sticker(id) 
        ON DELETE CASCADE  
);

CREATE TABLE IF NOT EXISTS tbl_reaction (
    id BIGSERIAL PRIMARY KEY,
    tweet_id BIGINT NOT NULL,
    content VARCHAR(2048) NOT NULL,
    CONSTRAINT fk_reaction_tweet 
        FOREIGN KEY (tweet_id) 
        REFERENCES tbl_tweet(id) 
        ON DELETE CASCADE, 
    CONSTRAINT ck_reaction_content_length CHECK (length(content) BETWEEN 2 AND 2048)
);

CREATE INDEX IF NOT EXISTS idx_tweet_editor ON tbl_tweet(editor_id);
CREATE INDEX IF NOT EXISTS idx_tweet_created ON tbl_tweet(created);
CREATE INDEX IF NOT EXISTS idx_reaction_tweet ON tbl_reaction(tweet_id);


DROP TRIGGER IF EXISTS trigger_cleanup_stickers ON tbl_tweet_sticker;
DROP FUNCTION IF EXISTS cleanup_unused_stickers();

CREATE FUNCTION cleanup_unused_stickers() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM tbl_sticker WHERE id = OLD.sticker_id
    AND NOT EXISTS (SELECT 1 FROM tbl_tweet_sticker WHERE sticker_id = OLD.sticker_id);
    RETURN OLD;
END;
$$;

CREATE TRIGGER trigger_cleanup_stickers
    AFTER DELETE ON tbl_tweet_sticker
    FOR EACH ROW
    EXECUTE FUNCTION cleanup_unused_stickers();


INSERT INTO tbl_editor (login, password, firstname, lastname)
VALUES ('xgorodko@gmail.com', 'password123', 'Ксения', 'Городко')
ON CONFLICT (login) DO NOTHING;