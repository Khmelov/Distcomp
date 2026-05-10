from __future__ import annotations

from pathlib import Path
from threading import Lock
from typing import Optional

from cassandra.cluster import Cluster, Session
from cassandra.io.asyncioreactor import AsyncioConnection

KEYSPACE = "distcomp"
CONTACT_POINTS = ["127.0.0.1"]
PORT = 9042

SCHEMA_FILE = Path(__file__).with_name("schema.cql")

_cluster: Optional[Cluster] = None
_session: Optional[Session] = None
_lock = Lock()


def init_cassandra() -> Session:
    global _cluster, _session

    if _session is not None:
        return _session

    with _lock:
        if _session is not None:
            return _session

        _cluster = Cluster(["127.0.0.1"], port=9042)
        _session = _cluster.connect()

        _session.execute(
            f"""
            CREATE KEYSPACE IF NOT EXISTS {KEYSPACE}
            WITH replication = {{
                'class': 'SimpleStrategy',
                'replication_factor': 1
            }}
            """
        )
        _session.set_keyspace(KEYSPACE)

        _run_schema(_session)
        return _session


def get_cassandra_session() -> Session:
    return init_cassandra()


def _run_schema(session: Session) -> None:
    if not SCHEMA_FILE.exists():
        raise FileNotFoundError(f"Schema file not found: {SCHEMA_FILE}")

    schema_text = SCHEMA_FILE.read_text(encoding="utf-8")
    statements = [stmt.strip() for stmt in schema_text.split(";") if stmt.strip()]

    for statement in statements:
        session.execute(statement)


def shutdown_cassandra() -> None:
    global _cluster, _session

    if _session is not None:
        _session.shutdown()
        _session = None

    if _cluster is not None:
        _cluster.shutdown()
        _cluster = None