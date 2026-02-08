DROP TABLE IF EXISTS notes CASCADE;
DROP TABLE IF EXISTS topics_tags CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS topics CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR(64) NOT NULL UNIQUE,
                       first_name VARCHAR(64) NOT NULL,
                       last_name VARCHAR(64),
                       password VARCHAR(128) NOT NULL
);

CREATE TABLE topics (
                        id BIGSERIAL PRIMARY KEY,
                        title VARCHAR(64) NOT NULL,
                        content VARCHAR(2048) NOT NULL,
                        created_at TIMESTAMP,
                        updated_at TIMESTAMP,
                        user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tags (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE topics_tags (
                             topic_id BIGINT REFERENCES topics(id) ON DELETE CASCADE,
                             tag_id BIGINT REFERENCES tags(id) ON DELETE CASCADE,
                             PRIMARY KEY (topic_id, tag_id)
);

CREATE TABLE notes (
                       id BIGSERIAL PRIMARY KEY,
                       content VARCHAR(2048) NOT NULL,
                       topic_id BIGINT REFERENCES topics(id) ON DELETE CASCADE
);