-- +goose Up
CREATE TABLE IF NOT EXISTS tbl_writer (
  id        bigserial     NOT NULL,
  login     varchar(64)   NOT NULL UNIQUE CHECK (LENGTH(login) >= 2),
  password  varchar(128)  NOT NULL CHECK (LENGTH(password) >= 8),
  firstname varchar(64)   NOT NULL CHECK (LENGTH(firstname) >= 2),
  lastname  varchar(64)   NOT NULL CHECK (LENGTH(lastname) >= 2),
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tbl_news (
  id        bigserial   NOT NULL,
  writer_id  bigint      NOT NULL, 
  title     varchar(64) NOT NULL UNIQUE CHECK (LENGTH(title) >= 2),
  content   text        NOT NULL CHECK (LENGTH(content) >= 4),
  created   timestamp   NOT NULL DEFAULT NOW(),
  modified  timestamp   NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id),
  FOREIGN KEY (writer_id) REFERENCES tbl_writer (id)
);

CREATE TABLE IF NOT EXISTS tbl_notice (
  id        bigserial   NOT NULL,
  news_id    bigint      NOT NULL,
  content   text        NOT NULL CHECK(LENGTH(content) >= 2),
  PRIMARY KEY (id),
  FOREIGN KEY (news_id) REFERENCES tbl_news (id)
);

CREATE TABLE IF NOT EXISTS tbl_label (
  id        bigserial   NOT NULL,
  name      varchar(32) NOT NULL UNIQUE CHECK(LENGTH(name) >= 2),
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS m2m_news_label (
  news_id  bigint NOT NULL,
  label_id bigint NOT NULL,
  PRIMARY KEY (news_id, label_id),
  FOREIGN KEY (news_id) REFERENCES tbl_news (id),
  FOREIGN KEY (label_id) REFERENCES tbl_label (id)
);

-- +goose Down
DROP TABLE IF EXISTS m2m_news_label;
DROP TABLE IF EXISTS tbl_label;
DROP TABLE IF EXISTS tbl_notice;
DROP TABLE IF EXISTS tbl_news;
DROP TABLE IF EXISTS tbl_writer;
