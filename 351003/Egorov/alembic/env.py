from logging.config import fileConfig

from sqlalchemy import create_engine, text
from alembic import context

from publisher.config import get_settings
from publisher.database import metadata


config = context.config

if config.config_file_name is not None:
    fileConfig(config.config_file_name)

target_metadata = metadata


def run_migrations_offline() -> None:
    settings = get_settings()
    url = settings.database_url.replace("+asyncpg", "")
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
    url = settings.database_url.replace("+asyncpg", "")
    engine = create_engine(url)

    with engine.connect() as connection:
        # ensure schema is in search_path (SQLAlchemy 2.x needs text() or exec_driver_sql)
        connection.execute(text(f"SET search_path TO {settings.db_schema}"))
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
