import re

from cassandra.cluster import Cluster

from config.settings import Settings

settings = Settings()

cluster = Cluster([settings.cassandra_host], port=settings.cassandra_port)
_session = None


def get_session():
    global _session
    if _session is None:
        _session = cluster.connect()
        _init_schema(_session)
    return _session


def _init_schema(s):
    ks = settings.cassandra_keyspace
    if not re.match(r'^[a-zA-Z_][a-zA-Z0-9_]*$', ks):
        raise ValueError(f"Invalid keyspace name: {ks}")

    s.execute("""
        CREATE KEYSPACE IF NOT EXISTS %s
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
    """ % ks)

    s.set_keyspace(ks)

    s.execute("""
        CREATE TABLE IF NOT EXISTS tbl_comment (
            tweetId  bigint,
            id       bigint,
            country  text,
            content  text,
            state    text,
            PRIMARY KEY (tweetId, id)
        ) WITH CLUSTERING ORDER BY (id ASC)
    """)

    s.execute("""
        CREATE TABLE IF NOT EXISTS tbl_comment_by_id (
            id       bigint PRIMARY KEY,
            tweetId  bigint,
            country  text,
            content  text,
            state    text
        )
    """)

    try:
        s.execute("ALTER TABLE tbl_comment ADD state text")
    except Exception:
        pass  # Column already exists
    try:
        s.execute("ALTER TABLE tbl_comment_by_id ADD state text")
    except Exception:
        pass  # Column already exists


def shutdown():
    cluster.shutdown()
