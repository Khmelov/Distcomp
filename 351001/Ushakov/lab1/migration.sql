CREATE TABLE writers (
    id BIGSERIAL PRIMARY KEY,
    login TEXT NOT NULL UNIQUE CHECK (char_length(login) BETWEEN 2 AND 64),
    password TEXT NOT NULL CHECK (char_length(password) BETWEEN 8 AND 124),
    firstname TEXT NOT NULL CHECK (char_length(firstname) BETWEEN 2 AND 64),
    lastname TEXT NOT NULL CHECK (char_length(lastname) BETWEEN 2 AND 64)
);

CREATE TABLE issues (
    id SERIAL PRIMARY KEY,
    writer_id BIGINT NOT NULL REFERENCES writers(id),
    title TEXT NOT NULL CHECK (char_length(title) BETWEEN 2 AND 64),
    content TEXT NOT NULL CHECK (char_length(content) BETWEEN 4 AND 2048),
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified TIMESTAMPTZ DEFAULT NULL
);

CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issues(id),
    content TEXT NOT NULL CHECK(char_length(content) BETWEEN 2 AND 2048)
);

CREATE TABLE labels (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE CHECK(char_length(name) BETWEEN 2 AND 32) NOT NULL
);