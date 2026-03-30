# alembic/env.py
import asyncio
from logging.config import fileConfig
import os

from sqlalchemy import pool
from sqlalchemy.engine import Connection

from alembic import context

# this is the Alembic Config object, which provides
# access to the values within the .ini file in use.
config = context.config

# Interpret the config file for Python logging.
fileConfig(config.config_file_name)

# Import your models' MetaData
# ensure package import path is correct
from app.infrastructure.db.models import Base  # Base.metadata contains schema=distcomp
target_metadata = Base.metadata

# Use env var if present; otherwise fallback to alembic.ini sqlalchemy.url
# We prefer a synchronous URL for autogenerate (psycopg2)
DB_URL = os.getenv("ALEMBIC_DATABASE_URL") or os.getenv("DATABASE_URL") or config.get_main_option("sqlalchemy.url")

# When using schema other than public, ensure version table is in that schema
version_table_schema = os.getenv("ALEMBIC_VERSION_SCHEMA", "distcomp")


def run_migrations_offline():
    """Run migrations in 'offline' mode (generate SQL)."""
    url = DB_URL
    context.configure(
        url=url,
        target_metadata=target_metadata,
        literal_binds=True,
        dialect_opts={"paramstyle": "named"},
        include_schemas=True,
        version_table_schema=version_table_schema,
    )

    with context.begin_transaction():
        context.run_migrations()


def do_run_migrations(connection: Connection):
    context.configure(
        connection=connection,
        target_metadata=target_metadata,
        include_schemas=True,
        version_table_schema=version_table_schema,
        compare_type=True,  # detect type changes
        compare_server_default=True,
    )

    with context.begin_transaction():
        context.run_migrations()


async def run_migrations_online():
    """Run migrations in 'online' mode using async engine."""
    # create async engine (via SQLAlchemy 1.4 async)
    # For online migrations we can use asyncpg driver URL (postgresql+asyncpg://...)
    async_url = os.getenv("DATABASE_URL_ASYNC") or os.getenv("DATABASE_URL")  # prefer async url in env
    if not async_url:
        raise RuntimeError("DATABASE_URL or DATABASE_URL_ASYNC must be set in environment")

    # create async engine
    from sqlalchemy.ext.asyncio import create_async_engine

    connectable = create_async_engine(async_url, poolclass=pool.NullPool)

    async with connectable.connect() as connection:
        # optionally set search_path for this connection
        # await connection.execute(text("SET search_path TO distcomp,public"))
        await connection.run_sync(do_run_migrations)
    await connectable.dispose()


if context.is_offline_mode():
    run_migrations_offline()
else:
    # run async migrations
    asyncio.run(run_migrations_online())