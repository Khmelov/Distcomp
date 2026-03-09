from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    DB_HOST: str = Field(validation_alias="POSTGRES_HOST", default="localhost")
    DB_PORT: int = Field(validation_alias="POSTGRES_PORT", default=5432, ge=1, le=65535)
    DB_USER: str = Field(validation_alias="POSTGRES_USER", default=None)
    DB_PASSWORD: str = Field(validation_alias="POSTGRES_PASSWORD", default=None)
    DB_NAME: str = Field(validation_alias="POSTGRES_DB", default=None)
    NOTE_SERVICE_URL: str = Field(default=None)

    DEBUG: bool = Field(default=True)

    @property
    def get_database_url(self) -> str:
        return f"postgresql+asyncpg://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"

settings = Settings()