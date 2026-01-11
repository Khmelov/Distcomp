# Подключимся к Cassandra
docker exec -it cassandra cqlsh -u cassandra -p cassandra

# Используем наш keyspace
USE discussion_keyspace;

# Создадим таблицу вручную
CREATE TABLE IF NOT EXISTS tbl_comment (
    story_id bigint,
    id bigint,
    content text,
    PRIMARY KEY (story_id, id)
) WITH CLUSTERING ORDER BY (id ASC);

# Проверим что таблица создана
DESCRIBE TABLE tbl_comment;