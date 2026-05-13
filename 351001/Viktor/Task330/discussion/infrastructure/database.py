from cassandra.cluster import Cluster
from Task330.discussion.config import settings

class CassandraDB:
    def __init__(self):
        self.cluster = Cluster([settings.CASSANDRA_HOST], port=settings.CASSANDRA_PORT)
        self.session = self.cluster.connect()

    def init_keyspace(self):
        self.session.execute(f"""
            CREATE KEYSPACE IF NOT EXISTS {settings.CASSANDRA_KEYSPACE}
            WITH REPLICATION = {{ 'class': 'SimpleStrategy', 'replication_factor': 1 }}
        """)
        self.session.set_keyspace(settings.CASSANDRA_KEYSPACE)
        self.session.execute("""
            CREATE TABLE IF NOT EXISTS tbl_post (
                tweet_id bigint,
                id bigint,
                content text,
                PRIMARY KEY (tweet_id, id)
            )
        """)
        # Создаём вторичный индекс для поиска по id (для глобального GET /posts/{id})
        self.session.execute("CREATE INDEX IF NOT EXISTS idx_post_id ON tbl_post (id)")

    def get_session(self):
        return self.session

    def close(self):
        self.cluster.shutdown()

cassandra_db = CassandraDB()
cassandra_db.init_keyspace()

# Функция для получения сессии (для Dependency Injection)
def get_db():
    return cassandra_db.get_session()