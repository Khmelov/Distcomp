CREATE TABLE tbl_user (
      id BIGSERIAL PRIMARY KEY,
      login VARCHAR(255) NOT NULL,
      password VARCHAR(255) NOT NULL,
      firstname VARCHAR(255),
      lastname VARCHAR(255)
);

CREATE TABLE tbl_issue (
       id BIGSERIAL PRIMARY KEY,
       title VARCHAR(255) NOT NULL,
       content TEXT,
       created TIMESTAMP,
       modified TIMESTAMP,
       user_id BIGINT,
       FOREIGN KEY (user_id) REFERENCES tbl_user(id)
);

CREATE TABLE tbl_comment (
     id BIGSERIAL PRIMARY KEY,
     content TEXT,
     issue_id BIGINT,
     FOREIGN KEY (issue_id) REFERENCES tbl_issue(id)
);

CREATE TABLE tbl_label (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tbl_label_issue (
     label_id BIGINT,
     issue_id BIGINT,
     PRIMARY KEY (label_id, issue_id),
     FOREIGN KEY (label_id) REFERENCES tbl_label(id),
     FOREIGN KEY (issue_id) REFERENCES tbl_issue(id)
);