from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict

class PostgresSettings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="POSTGRES_")

    host: str = Field(default="localhost")
    port: int = Field(default=5432, ge=1, le=65535)
    user: str = Field(default=None)
    password: str = Field(default=None)
    name: str = Field(default=None)

    @property
    def get_database_url(self) -> str:
        return f"postgresql+asyncpg://{self.user}:{self.password}@{self.host}:{self.port}/{self.name}"

class Settings(BaseSettings):
    postgres: PostgresSettings = PostgresSettings()
    note_service_url: str = Field(default=None)

    DEBUG: bool = Field(default=True)

settings = Settings()