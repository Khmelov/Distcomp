CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE distcomp;

CREATE TABLE IF NOT EXISTS authors (
                                       id bigint PRIMARY KEY,
                                       login text,
                                       password text,
                                       firstname text,
                                       lastname text
);

CREATE TABLE IF NOT EXISTS news (
                                    id bigint PRIMARY KEY,
                                    author_id bigint,
                                    title text,
                                    content text,
                                    created timestamp,
                                    modified timestamp
);

CREATE TABLE IF NOT EXISTS tags (
                                    id bigint PRIMARY KEY,
                                    name text
);

CREATE TABLE IF NOT EXISTS comments (
                                        id bigint PRIMARY KEY,
                                        news_id bigint,
                                        content text
);

CREATE TABLE IF NOT EXISTS news_tags (
                                         news_id bigint,
                                         tag_id bigint,
                                         PRIMARY KEY (news_id, tag_id)
    );