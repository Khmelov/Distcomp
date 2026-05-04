import os
from pathlib import Path

import pytest
from testcontainers.cassandra import CassandraContainer

_ROOT = Path(__file__).resolve().parent.parent
os.environ.setdefault("PYTHONPATH", str(_ROOT))


@pytest.fixture(scope="session")
def cassandra_container():
    with CassandraContainer("cassandra:4.1") as c:
        yield c


@pytest.fixture
def discussion_client(cassandra_container, monkeypatch):
    host = cassandra_container.get_container_host_ip()
    port = int(cassandra_container.get_exposed_port(9042))
    monkeypatch.setenv("CASSANDRA_HOSTS", host)
    monkeypatch.setenv("CASSANDRA_PORT", str(port))
    monkeypatch.setenv("KAFKA_ENABLED", "0")

    from cassandra.cluster import Cluster
    from discussion_mod.core import database as dbmod

    dbmod.close_db()
    cl = Cluster([host], port=port, protocol_version=4)
    sess = cl.connect()
    sess.execute("DROP KEYSPACE IF EXISTS distcomp")
    sess.shutdown()
    cl.shutdown()

    from fastapi.testclient import TestClient

    from main import app

    with TestClient(app) as client:
        yield client

    dbmod.close_db()
    monkeypatch.delenv("KAFKA_ENABLED", raising=False)
