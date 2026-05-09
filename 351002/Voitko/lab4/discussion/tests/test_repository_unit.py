"""Юнит-тест репозитория на синхронной сессии Cassandra (testcontainers)."""

import os
from pathlib import Path

import pytest
from cassandra.cluster import Cluster
from testcontainers.cassandra import CassandraContainer

_ROOT = Path(__file__).resolve().parent.parent
os.environ.setdefault("PYTHONPATH", str(_ROOT))


@pytest.fixture(scope="module")
def cass_session():
    with CassandraContainer("cassandra:4.1") as c:
        host = c.get_container_host_ip()
        port = int(c.get_exposed_port(9042))
        cl = Cluster([host], port=port, protocol_version=4)
        sess = cl.connect()
        sess.execute("DROP KEYSPACE IF EXISTS distcomp")
        sess.execute(
            "CREATE KEYSPACE distcomp WITH replication = "
            "{'class': 'SimpleStrategy', 'replication_factor': 1}"
        )
        sess.set_keyspace("distcomp")
        sess.execute(
            """
            CREATE TABLE tbl_notes_by_news (
                news_id bigint,
                id bigint,
                content text,
                PRIMARY KEY ((news_id), id)
            )
            """
        )
        sess.execute(
            """
            CREATE TABLE tbl_note_by_id (
                id_bucket int,
                id bigint,
                news_id bigint,
                content text,
                PRIMARY KEY ((id_bucket), id)
            )
            """
        )
        yield sess
        sess.shutdown()
        cl.shutdown()


def test_repository_crud(cass_session, monkeypatch):
    monkeypatch.setenv("NOTE_ID_BUCKETS", "64")
    from importlib import reload

    import discussion_mod.core.settings as st
    reload(st)

    from discussion_mod.domain.repositories.note_repository import NoteRepository

    repo = NoteRepository(cass_session)
    repo.insert(1, 100, "a")
    assert repo.get_by_id(100) == (100, 1, "a")
    repo.update(100, 2, "b")
    assert repo.list_by_news(1) == []
    assert repo.list_by_news(2) == [(100, 2, "b")]
    repo.delete(100)
    assert repo.get_by_id(100) is None
