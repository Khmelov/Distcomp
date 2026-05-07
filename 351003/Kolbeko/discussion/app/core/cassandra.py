import os
from typing import Optional

from cassandra.cluster import Cluster, Session


_cluster: Optional[Cluster] = None
_session: Optional[Session] = None


def cassandra_init() -> None:
    global _cluster, _session
    if _session:
        return

    host = os.getenv("CASSANDRA_HOST", "localhost")
    port = int(os.getenv("CASSANDRA_PORT", "9042"))
    keyspace = os.getenv("CASSANDRA_KEYSPACE", "distcomp")

    _cluster = Cluster([host], port=port)
    _session = _cluster.connect()

    _session.execute(
        f"""
        CREATE KEYSPACE IF NOT EXISTS {keyspace}
        WITH replication = {{'class': 'SimpleStrategy', 'replication_factor': 1}};
        """
    )
    _session.set_keyspace(keyspace)

    _session.execute(
        """
        CREATE TABLE IF NOT EXISTS tbl_notice_by_id (
            bucket int,
            id bigint,
            tweet_id bigint,
            content text,
            state text,
            PRIMARY KEY ((bucket), id)
        );
        """
    )
    _session.execute(
        """
        CREATE TABLE IF NOT EXISTS tbl_notice_by_tweet (
            tweet_id bigint,
            id bigint,
            content text,
            state text,
            PRIMARY KEY ((tweet_id), id)
        ) WITH CLUSTERING ORDER BY (id DESC);
        """
    )


def cassandra_shutdown() -> None:
    global _cluster, _session
    if _session:
        _session.shutdown()
    if _cluster:
        _cluster.shutdown()
    _session = None
    _cluster = None


def cassandra_session() -> Session:
    if not _session:
        raise RuntimeError("Cassandra not initialized")
    return _session


def notice_bucket(notice_id: int, buckets: int = 16) -> int:
    return int(notice_id % buckets)

