CREATE TABLE tbl_users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255),
  lastname VARCHAR(255)
);

CREATE TABLE tbl_news (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255),
  content TEXT,
  user_id INT
);

CREATE TABLE tbl_labels (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE tbl_news_labels (
  news_id INT,
  label_id INT,
  PRIMARY KEY(news_id, label_id)
);
