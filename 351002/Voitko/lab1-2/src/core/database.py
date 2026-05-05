import re

from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from urllib.parse import quote_plus

from src.core.settings import settings
from src.domain.models.models import Base

engine = create_engine(
    settings.sqlalchemy_url,
    pool_pre_ping=True,
    echo=False,
    connect_args={"options": f"-csearch_path={settings.DB_SCHEMA}"},
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def ensure_database_exists() -> None:
    """
    Если БД из DB_NAME ещё нет — создаёт её (подключение к служебной БД postgres).
    Не вызывается при явном DATABASE_URL (как в pytest).
    """
    if settings.DATABASE_URL:
        return
    if not re.fullmatch(r"[a-zA-Z_][a-zA-Z0-9_]*", settings.DB_NAME):
        return

    safe = quote_plus(settings.DB_PASSWORD)
    maint = settings.DB_MAINTENANCE_DB
    url = (
        f"postgresql+psycopg2://{settings.DB_USER}:{safe}"
        f"@{settings.DB_HOST}:{settings.DB_PORT}/{maint}"
    )
    eng = create_engine(url, isolation_level="AUTOCOMMIT", pool_pre_ping=True)
    with eng.connect() as conn:
        exists = conn.execute(
            text("SELECT 1 FROM pg_database WHERE datname = :n"),
            {"n": settings.DB_NAME},
        ).fetchone()
        if exists is None:
            conn.execute(text(f'CREATE DATABASE "{settings.DB_NAME}"'))
        # Без этого JDBC вида jdbc:postgresql://localhost/distcomp с запросом
        # SELECT * FROM tbl_writer не находит таблицы (они в схеме distcomp, не в public).
        conn.execute(
            text(
                f'ALTER DATABASE "{settings.DB_NAME}" '
                f'SET search_path TO "{settings.DB_SCHEMA}", public'
            )
        )


def ensure_schema_and_tables() -> None:
    ensure_database_exists()
    with engine.connect() as conn:
        conn.execute(text(f'CREATE SCHEMA IF NOT EXISTS "{settings.DB_SCHEMA}"'))
        conn.commit()
    Base.metadata.create_all(bind=engine)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
