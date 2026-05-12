from pathlib import Path
from typing import Optional

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict

_ROOT = Path(__file__).resolve().parents[2]


class Settings(BaseSettings):
    """
    Как в Egorov/lab2: .env в корне проекта (рядом с main.py).
    """

    model_config = SettingsConfigDict(
        env_file=_ROOT / ".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    DB_HOST: str = Field(default="localhost")
    DB_PORT: int = Field(default=5432, ge=1, le=65535)
    DB_USER: str = Field(default="postgres", min_length=1)
    DB_PASSWORD: str = Field(default="postgres", min_length=1)
    DB_NAME: str = Field(default="distcomp", min_length=1)
    DB_SCHEMA: str = Field(default="distcomp", min_length=1)
    DB_MAINTENANCE_DB: str = Field(
        default="postgres",
        min_length=1,
        description="Служебная БД для авто-создания DB_NAME (обычно postgres)",
    )

    DATABASE_URL: Optional[str] = Field(default=None)

    @property
    def sqlalchemy_url(self) -> str:
        if self.DATABASE_URL:
            return self.DATABASE_URL
        from urllib.parse import quote_plus

        safe = quote_plus(self.DB_PASSWORD)
        return (
            f"postgresql+psycopg2://{self.DB_USER}:{safe}"
            f"@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"
        )


settings = Settings()
