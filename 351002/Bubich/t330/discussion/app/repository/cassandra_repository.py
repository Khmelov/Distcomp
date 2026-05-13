from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from cassandra.query import SimpleStatement
from app.config import Config
import uuid


class CassandraRepository:
    def __init__(self):
        self.cluster = None
        self.session = None
        self.connect()

    def connect(self):
        """Подключение к Cassandra"""
        try:
            self.cluster = Cluster(
                [Config.CASSANDRA_HOST],
                port=Config.CASSANDRA_PORT
            )
            self.session = self.cluster.connect()
            print(f"Connected to Cassandra at {Config.CASSANDRA_HOST}:{Config.CASSANDRA_PORT}")
        except Exception as e:
            print(f"Warning: Could not connect to Cassandra: {e}")
            print("Using in-memory storage as fallback")
            self.session = None
            self._in_memory_storage = {}
            self._next_id = 1

    def init_schema(self):
        """Инициализация схемы и таблиц"""
        if not self.session:
            return

        try:
            # Создаем keyspace
            self.session.execute("""
                CREATE KEYSPACE IF NOT EXISTS distcomp
                WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
            """)

            self.session.set_keyspace('distcomp')

            # Создаем таблицу комментариев
            # country - partition key (НЕ story_id, чтобы избежать data skew!)
            # story_id и id - clustering keys для сортировки
            self.session.execute("""
                CREATE TABLE IF NOT EXISTS tbl_comment (
                    country text,
                    story_id bigint,
                    id bigint,
                    content text,
                    PRIMARY KEY (country, story_id, id)
                )
            """)

            # Индекс для поиска по story_id
            self.session.execute("""
                CREATE INDEX IF NOT EXISTS idx_comment_story_id 
                ON tbl_comment (story_id)
            """)

            print("Cassandra schema initialized")
        except Exception as e:
            print(f"Error initializing schema: {e}")

    def save(self, comment):
        """Сохранение комментария"""
        if self.session:
            # Распределяем по странам для равномерного партиционирования
            country = self._get_partition_key(comment.get('story_id', 0))

            self.session.execute(
                """
                INSERT INTO tbl_comment (country, story_id, id, content)
                VALUES (%s, %s, %s, %s)
                """,
                (
                    country,
                    comment.get('story_id'),
                    comment.get('id'),
                    comment.get('content')
                )
            )
            return comment
        else:
            # In-memory fallback
            comment['id'] = self._next_id
            self._in_memory_storage[self._next_id] = comment
            self._next_id += 1
            return comment

    def find_by_id(self, id: int):
        """Поиск комментария по ID (требует перебора партиций)"""
        if self.session:
            # Поиск по всем партициям
            countries = self._get_all_partition_keys()
            for country in countries:
                rows = self.session.execute(
                    "SELECT * FROM tbl_comment WHERE country=%s AND id=%s",
                    (country, id)
                )
                for row in rows:
                    return {
                        'id': row.id,
                        'story_id': row.story_id,
                        'content': row.content
                    }
            return None
        else:
            return self._in_memory_storage.get(id)

    def find_by_story_id(self, story_id: int):
        """Поиск комментариев по story_id"""
        if self.session:
            # Используем ALLOW FILTERING для поиска по индексу
            country = self._get_partition_key(story_id)
            rows = self.session.execute(
                "SELECT * FROM tbl_comment WHERE story_id=%s ALLOW FILTERING",
                (story_id,)
            )
            comments = []
            for row in rows:
                comments.append({
                    'id': row.id,
                    'story_id': row.story_id,
                    'content': row.content
                })
            return comments
        else:
            return [
                c for c in self._in_memory_storage.values()
                if c.get('story_id') == story_id
            ]

    def find_all(self):
        """Получение всех комментариев"""
        if self.session:
            rows = self.session.execute("SELECT * FROM tbl_comment")
            comments = []
            for row in rows:
                comments.append({
                    'id': row.id,
                    'story_id': row.story_id,
                    'content': row.content
                })
            return comments
        else:
            return list(self._in_memory_storage.values())

    def update(self, id: int, comment_data: dict):
        """Обновление комментария"""
        if self.session:
            # Кассандра - upsert (вставка с обновлением)
            country = self._get_partition_key(comment_data.get('story_id', 0))
            self.session.execute(
                """
                INSERT INTO tbl_comment (country, story_id, id, content)
                VALUES (%s, %s, %s, %s)
                """,
                (
                    country,
                    comment_data.get('story_id'),
                    id,
                    comment_data.get('content')
                )
            )
            comment_data['id'] = id
            return comment_data
        else:
            if id in self._in_memory_storage:
                self._in_memory_storage[id].update(comment_data)
                return self._in_memory_storage[id]
            return None

    def delete_by_id(self, id: int, story_id: int = None):
        """Удаление комментария"""
        if self.session:
            if story_id:
                country = self._get_partition_key(story_id)
                self.session.execute(
                    "DELETE FROM tbl_comment WHERE country=%s AND story_id=%s AND id=%s",
                    (country, story_id, id)
                )
            else:
                # Поиск по всем партициям для удаления
                countries = self._get_all_partition_keys()
                for country in countries:
                    self.session.execute(
                        "DELETE FROM tbl_comment WHERE country=%s AND id=%s",
                        (country, id)
                    )
            return True
        else:
            if id in self._in_memory_storage:
                del self._in_memory_storage[id]
                return True
            return False

    def _get_partition_key(self, story_id: int) -> str:
        """
        Равномерное распределение по партициям.
        Используем хеш для равномерного распределения данных.
        """
        # 10 стран для равномерного распределения
        countries = ['us', 'uk', 'de', 'fr', 'jp', 'br', 'in', 'au', 'ca', 'ru']
        return countries[story_id % len(countries)]

    def _get_all_partition_keys(self):
        """Получение всех возможных ключей партиционирования"""
        return ['us', 'uk', 'de', 'fr', 'jp', 'br', 'in', 'au', 'ca', 'ru']

    def close(self):
        if self.cluster:
            self.cluster.shutdown()