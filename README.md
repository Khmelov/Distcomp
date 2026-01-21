# 350

задача 350 Черняк

Task350. Кеширование данных (на примере Redis)
Дано:
• Разрабатываемая система обрабатывает сущности Author, Tweet, Tag и Reaction, которые логически связаны отношениями (см. предыдущие задачи)
o один-ко-многим (Author и Tweet, Tweet и Reaction)
o многие-ко-многим (Tweet, Tag).
• В Kafka настроена передача между модулями publisher и discussion сущности Reaction.
• Author(s), Tweet(s), Tag(s) хранятся в Postgres.
• Reaction(s) хранятся в Cassandra.
