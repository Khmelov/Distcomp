import os
from pathlib import Path

import pytest
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from testcontainers.postgres import PostgresContainer

_ROOT = Path(__file__).resolve().parent.parent
os.environ.setdefault("PYTHONPATH", str(_ROOT))


@pytest.fixture(scope="session")
def postgres_container():
    with PostgresContainer("postgres:16-alpine") as pg:
        yield pg


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
                "TRUNCATE distcomp.tbl_note, distcomp.tbl_news_label, distcomp.tbl_news, "
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
def client(db_engine):
    os.environ["DATABASE_URL"] = str(db_engine.url)
    os.environ["SKIP_DB_INIT"] = "1"

    from fastapi.testclient import TestClient
    from sqlalchemy.orm import sessionmaker

    from main import app
    from src.core.database import get_db

    Session = sessionmaker(autocommit=False, autoflush=False, bind=db_engine)

    def _override_db():
        db = Session()
        try:
            yield db
        finally:
            db.close()

    app.dependency_overrides[get_db] = _override_db
    with TestClient(app) as c:
        yield c
    app.dependency_overrides.clear()
    os.environ.pop("SKIP_DB_INIT", None)
