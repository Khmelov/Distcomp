import os
import time

from cassandra.cluster import Cluster

KEYSPACE = "distcomp"
TABLE = "tbl_message"

_session = None
_cluster = None


def _hosts() -> list[str]:
    raw = os.getenv("CASSANDRA_HOSTS", "localhost").strip()
    return [h.strip() for h in raw.split(",") if h.strip()]


def _port() -> int:
    return int(os.getenv("CASSANDRA_PORT", "9042"))


def get_session():
    global _session, _cluster
    if _session is not None:
        return _session
    last_err = None
    for _ in range(90):
        try:
            _cluster = Cluster(_hosts(), port=_port(), connect_timeout=10)
            s = _cluster.connect()
            s.execute(
                f"""
                CREATE KEYSPACE IF NOT EXISTS {KEYSPACE}
                WITH replication = {{'class': 'SimpleStrategy', 'replication_factor': 1}}
                """
            )
            s.set_keyspace(KEYSPACE)
            s.execute(
                f"""
                CREATE TABLE IF NOT EXISTS {TABLE} (
                    country text,
                    news_id bigint,
                    id bigint,
                    content text,
                    PRIMARY KEY ((country), news_id, id)
                )
                """
            )
            _session = s
            return s
        except Exception as e:
            last_err = e
            time.sleep(3)
    raise RuntimeError(f"Cassandra unavailable: {last_err}") from last_err
