from functools import lru_cache
from pydantic import Field
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    app_name: str = "Story Management API"
    app_version: str = "1.0.0"

    api_prefix: str = "/api/v1.0"
    database_url: str = Field(
        default="postgresql+asyncpg://postgres:postgres@localhost:5432/postgres",
        description="Async SQLAlchemy URL; search_path is set to distcomp in database.py",
    )
    db_schema: str = "distcomp"

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"


@lru_cache
def get_settings() -> Settings:
    return Settings()

