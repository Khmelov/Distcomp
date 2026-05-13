import importlib.util
import os
import sys
from pathlib import Path

import pytest
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from testcontainers.cassandra import CassandraContainer
from testcontainers.postgres import PostgresContainer

_ROOT = Path(__file__).resolve().parent.parent
_LAB3 = _ROOT.parent
_DISC_ROOT = _LAB3 / "discussion"
os.environ.setdefault("PYTHONPATH", str(_ROOT))


def _load_discussion_app():
    dr = str(_DISC_ROOT)
    inserted = False
    if dr not in sys.path:
        sys.path.insert(0, dr)
        inserted = True
    path = _DISC_ROOT / "main.py"
    spec = importlib.util.spec_from_file_location("distcomp_discussion_main", path)
    mod = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    try:
        spec.loader.exec_module(mod)
        return mod.app
    finally:
        if inserted:
            try:
                sys.path.remove(dr)
            except ValueError:
                pass


@pytest.fixture(scope="session")
def postgres_container():
    with PostgresContainer("postgres:16-alpine") as pg:
        yield pg


@pytest.fixture(scope="session")
def cassandra_container():
    with CassandraContainer("cassandra:4.1") as c:
        yield c


@pytest.fixture(scope="session")
def db_engine(postgres_container):
    raw = postgres_container.get_connection_url()
    url = raw.replace("postgresql://", "postgresql+psycopg2://")
    eng = create_engine(url, pool_pre_ping=True, connect_args={"options": "-csearch_path=distcomp"})
    with eng.connect() as conn:
        conn.execute(text("CREATE SCHEMA IF NOT EXISTS distcomp"))
        conn.commit()

    from src.domain.models.models import Base

    Base.metadata.create_all(eng)
    yield eng
    Base.metadata.drop_all(eng)


@pytest.fixture(autouse=True)
def _truncate_tables(db_engine):
    with db_engine.connect() as conn:
        conn.execute(
            text(
                "TRUNCATE distcomp.tbl_news_label, distcomp.tbl_news, "
                "distcomp.tbl_label, distcomp.tbl_writer RESTART IDENTITY CASCADE"
            )
        )
        conn.commit()
    yield


@pytest.fixture
def db_session(db_engine):
    Session = sessionmaker(autocommit=False, autoflush=False, bind=db_engine)
    s = Session()
    try:
        yield s
    finally:
        s.close()


@pytest.fixture
def client(db_engine, cassandra_container, monkeypatch):
    host = cassandra_container.get_container_host_ip()
    port = int(cassandra_container.get_exposed_port(9042))
    monkeypatch.setenv("CASSANDRA_HOSTS", host)
    monkeypatch.setenv("CASSANDRA_PORT", str(port))
    monkeypatch.setenv("DATABASE_URL", str(db_engine.url))
    monkeypatch.setenv("SKIP_DB_INIT", "1")

    from cassandra.cluster import Cluster
    from src.core import discussion_http
    from src.core.database import get_db

    cl = Cluster([host], port=port, protocol_version=4)
    sess = cl.connect()
    sess.execute("DROP KEYSPACE IF EXISTS distcomp")
    sess.shutdown()
    cl.shutdown()

    discussion_app = _load_discussion_app()

    from fastapi.testclient import TestClient
    from starlette.testclient import TestClient as DiscussionTestClient

    from main import app

    Session = sessionmaker(autocommit=False, autoflush=False, bind=db_engine)

    def _override_db():
        db = Session()
        try:
            yield db
        finally:
            db.close()

    app.dependency_overrides[get_db] = _override_db
    try:
        with DiscussionTestClient(discussion_app) as disc_http:
            discussion_http.set_discussion_client(disc_http)
            with TestClient(app) as c:
                yield c
    finally:
        app.dependency_overrides.clear()
        discussion_http.set_discussion_client(None)
        monkeypatch.delenv("SKIP_DB_INIT", raising=False)
