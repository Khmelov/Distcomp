from cassandra.cluster import Cluster
from requests import session


class CassandraConfig:
    def __init__(self):
        self.cluster = None
        self.session = None

    def connect(self):
        self.cluster = Cluster(['localhost'], port=9042)
        self.session = self.cluster.connect()

        self.session.execute("""
            CREATE KEYSPACE IF NOT EXISTS distcomp 
            WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
        """)

        self.session.set_keyspace('distcomp')

        # Создание таблицы для Notice
        self.session.execute("""
            CREATE TABLE IF NOT EXISTS tbl_notice (
                country text,
                story_id bigint,
                id bigint,
                content text,
                PRIMARY KEY ((country), story_id, id)
            )
        """)

        return self.session

    def close(self):
        if self.cluster:
            self.cluster.shutdown()


# Синглтон для использования во всем приложении
cassandra_config = CassandraConfig()