using System;
using FluentMigrator;

namespace Infrastructure.Migrations;

[Migration(20250216101011, TransactionBehavior.None)]
public class InitSchema : Migration
{
    public override void Up()
    {
        const string init_script = 
"""
CREATE TABLE IF NOT EXISTS tbl_user (
  id        bigserial     NOT NULL,
  login     varchar(64)   NOT NULL UNIQUE CHECK (LENGTH(login) >= 2),
  password  varchar(128)  NOT NULL CHECK (LENGTH(password) >= 8),
  firstname varchar(64)   NOT NULL CHECK (LENGTH(firstname) >= 2),
  lastname  varchar(64)   NOT NULL CHECK (LENGTH(lastname) >= 2),
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tbl_tweet (
  id        bigserial   NOT NULL,
  user_id  bigint      NOT NULL, 
  title     varchar(64) NOT NULL UNIQUE CHECK (LENGTH(title) >= 2),
  content   text        NOT NULL CHECK (LENGTH(content) >= 4),
  created   timestamp   NOT NULL DEFAULT NOW(),
  modified  timestamp   NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES tbl_user (id)
);

CREATE TABLE IF NOT EXISTS tbl_reaction (
  id        bigserial   NOT NULL,
  tweet_id    bigint      NOT NULL,
  content   text        NOT NULL CHECK(LENGTH(content) >= 2),
  PRIMARY KEY (id),
  FOREIGN KEY (tweet_id) REFERENCES tbl_tweet (id)
);

CREATE TABLE IF NOT EXISTS tbl_marker (
  id        bigserial   NOT NULL,
  name      varchar(32) NOT NULL UNIQUE CHECK(LENGTH(name) >= 2),
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS m2m_tweet_marker (
  tweet_id  bigint NOT NULL,
  marker_id bigint NOT NULL,
  PRIMARY KEY (tweet_id, marker_id),
  FOREIGN KEY (tweet_id) REFERENCES tbl_tweet (id),
  FOREIGN KEY (marker_id) REFERENCES tbl_marker (id)
);
""";
        Execute.Sql(init_script);
    }

    public override void Down()
    {
        const string init_down_script = 
"""
DROP TABLE IF EXISTS m2m_tweet_marker;
DROP TABLE IF EXISTS tbl_marker;
DROP TABLE IF EXISTS tbl_reaction;
DROP TABLE IF EXISTS tbl_tweets;
DROP TABLE IF EXISTS tbl_user;
""";
        Execute.Sql(init_down_script);
    }
}
