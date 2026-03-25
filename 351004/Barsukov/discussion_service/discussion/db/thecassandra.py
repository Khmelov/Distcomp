from cassandra.cluster import Cluster
from cassandra.query import dict_factory
from discussion.core.config import settings


class CassandraClient:
    def __init__(self):
        self.cluster = None
        self.session = None

    def connect(self):
        """Подключение к Cassandra"""
        print(f"Connecting to Cassandra at {settings.CASSANDRA_HOSTS}:{settings.CASSANDRA_PORT}")
        self.cluster = Cluster(
            contact_points=settings.CASSANDRA_HOSTS,
            port=settings.CASSANDRA_PORT
        )
        self.session = self.cluster.connect(settings.CASSANDRA_KEYSPACE)
        self.session.row_factory = dict_factory
        self._init_tables()
        print("Connected to Cassandra successfully")

    def _init_tables(self):
        """Создание таблицы если не существует"""
        # ВНИМАНИЕ: Правильная структура с issue_id как partition key!
        self.session.execute("""
                             CREATE TABLE IF NOT EXISTS tbl_note
                             (
                                 issue_id
                                 int,
                                 id
                                 int,
                                 content
                                 text,
                                 PRIMARY
                                 KEY (
                             (
                                 issue_id
                             ), id)
                                 ) WITH CLUSTERING ORDER BY (id ASC)
                             """)
        print("Table tbl_note initialized")

    def close(self):
        """Закрытие соединения"""
        if self.cluster:
            self.cluster.shutdown()
            print("Disconnected from Cassandra")


# Создаем глобальный экземпляр
cassandra_client = CassandraClient()


def get_db():
    """Dependency для получения сессии"""
    return cassandra_client.session