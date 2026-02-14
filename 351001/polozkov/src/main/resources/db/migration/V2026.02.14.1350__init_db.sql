CREATE TABLE users (
      id BIGINT PRIMARY KEY,
      login VARCHAR(255) NOT NULL,
      password VARCHAR(255) NOT NULL,
      firstname VARCHAR(255),
      lastname VARCHAR(255)
);

CREATE TABLE issue (
       id BIGINT PRIMARY KEY,
       title VARCHAR(255) NOT NULL,
       content TEXT,
       created TIMESTAMP,
       modified TIMESTAMP,
       user_id BIGINT,
       FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE comment (
     id BIGINT PRIMARY KEY,
     content TEXT,
     issue_id BIGINT,
     FOREIGN KEY (issue_id) REFERENCES issue(id)
);

CREATE TABLE label (
   id BIGINT PRIMARY KEY,
   name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE label_issue (
     label_id BIGINT,
     issue_id BIGINT,
     PRIMARY KEY (label_id, issue_id),
     FOREIGN KEY (label_id) REFERENCES label(id),
     FOREIGN KEY (issue_id) REFERENCES issue(id)
);