from logging.config import fileConfig

from sqlalchemy import engine_from_config, pool
from sqlalchemy import create_engine
from sqlalchemy.engine import Connection
from alembic import context

from publisher.database import metadata
from publisher.config import get_settings


config = context.config

if config.config_file_name is not None:
    fileConfig(config.config_file_name)

target_metadata = metadata


def run_migrations_offline() -> None:
    url = get_settings().database_url
    context.configure(
        url=url,
        target_metadata=target_metadata,
        literal_binds=True,
        compare_type=True,
    )

    with context.begin_transaction():
        context.run_migrations()


def run_migrations_online() -> None:
    settings = get_settings()

    connectable = create_engine(settings.database_url.replace("+asyncpg", ""))

    with connectable.connect() as connection:
        connection.execute(f"SET search_path TO {settings.db_schema}")
        context.configure(
            connection=connection,
            target_metadata=target_metadata,
            compare_type=True,
        )

        with context.begin_transaction():
            context.run_migrations()


if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()

