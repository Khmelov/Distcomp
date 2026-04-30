from cassandra.cluster import Cluster
from cassandra.query import dict_factory

session = None


def init_cassandra():
    global session
    cluster = Cluster(['localhost'], port=9042)
    session = cluster.connect()
    session.row_factory = dict_factory

    session.execute("""
        CREATE KEYSPACE IF NOT EXISTS distcomp 
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
    """)
    session.set_keyspace('distcomp')

    session.execute("DROP TABLE IF EXISTS tbl_post")

    session.execute("""
        CREATE TABLE tbl_post (
            id bigint PRIMARY KEY,
            article_id bigint,
            content text,
            country text,
            state text
        )
    """)

    session.execute("CREATE INDEX ON tbl_post (article_id)")


def get_session():
    return session