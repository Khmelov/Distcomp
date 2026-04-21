from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict

class AuthSettings(BaseSettings):
    token_lifetime: int = Field(default=1800)
    algorithm: str = Field(default="HS256")
    secret_key: str = Field(default="super-secret-key")


class KafkaSettings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="kafka_")

    bootstrap_servers: str = Field(default=None)
    topic_in: str = Field(default=None)
    topic_out: str = Field(default=None)

class RedisSettings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="redis_")

    host: str = Field(default="")
    port: int = Field(default="6379")
    db: str = Field(default="")

    @property
    def get_url(self) -> str:
        return f"redis://{self.host}:{self.port}/{self.db}"


class PostgresSettings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="postgres_")

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
    redis: RedisSettings = RedisSettings()
    auth: AuthSettings = AuthSettings()
    note_service_url: str = Field(default=None)

    DEBUG: bool = Field(default=True)

settings = Settings()