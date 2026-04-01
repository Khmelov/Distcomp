from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )

    DB_HOST: str = Field(default="localhost", validation_alias="db_host")
    DB_PORT: int = Field(default=5432, ge=1, le=65535)
    DB_USER: str = Field(min_length=1)
    DB_PASSWORD: str = Field(min_length=1)
    DB_NAME: str = Field(min_length=1)

    DEBUG: bool = Field(default=True)

    @property
    def get_database_url(self) -> str:
        return f"postgresql+asyncpg://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"

settings = Settings()