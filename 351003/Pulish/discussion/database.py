from cassandra.cluster import Cluster
from cassandra.query import dict_factory
from config import settings


class CassandraDB:
    def __init__(self):
        self.cluster = Cluster([settings.CASSANDRA_HOST],
                               port=settings.CASSANDRA_PORT)
        self.session = self.cluster.connect()
        self.session.row_factory = dict_factory
        self.init_db()

    def init_db(self):
        self.session.execute("""
            CREATE KEYSPACE IF NOT EXISTS distcomp 
            WITH replication = {'class': 'SimpleStrategy',
                              'replication_factor': 1};
        """)
        self.session.set_keyspace('distcomp')

        self.session.execute("""
            CREATE TABLE IF NOT EXISTS tbl_comment (
                id int PRIMARY KEY,
                topic_id int,
                content text
            );
        """)
        self.session.execute(
            "CREATE INDEX IF NOT EXISTS ON tbl_comment (topic_id);")


db = CassandraDB()


def get_cassandra_session():
    return db.session
