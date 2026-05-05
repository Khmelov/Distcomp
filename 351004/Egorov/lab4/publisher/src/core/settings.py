from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict

class KafkaSettings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="KAFKA_")

    bootstrap_servers: str = Field(default=None)
    topic_in: str = Field(default=None)
    topic_out: str = Field(default=None)

class PostgresSettings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="POSTGRES_")

    host: str = Field(default="localhost")
    port: int = Field(default=5432, ge=1, le=65535)
    user: str = Field(default=None)
    password: str = Field(default=None)
    db: str = Field(default=None)

    @property
    def get_database_url(self) -> str:
        return f"postgresql+asyncpg://{self.user}:{self.password}@{self.host}:{self.port}/{self.db}"

class Settings(BaseSettings):
    postgres: PostgresSettings = PostgresSettings()
    kafka: KafkaSettings = KafkaSettings()
    note_service_url: str = Field(default=None)

    DEBUG: bool = Field(default=True)

settings = Settings()