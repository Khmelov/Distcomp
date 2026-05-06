import os
from pathlib import Path
from urllib.parse import quote

from pydantic import SecretStr, Field
from pydantic_settings import BaseSettings as _BaseSettings
from pydantic_settings import SettingsConfigDict
from sqlalchemy import URL


def get_env_file_path() -> Path:
    if env_file := os.getenv("ENV_FILE"):
        return Path(env_file)

    return Path(__file__).parent.parent / ".env"


class BaseSettings(_BaseSettings):
    model_config = SettingsConfigDict(extra="ignore",
                                      env_file=get_env_file_path(),
                                      env_file_encoding="utf-8")


class PostgresConfig(BaseSettings, env_prefix="POSTGRES_"):
    host: str
    db: str
    password: SecretStr
    port: int
    user: str

    def url(self) -> URL:
        return URL.create(
            drivername="postgresql+asyncpg",
            username=self.user,
            password=self.password.get_secret_value(),
            host=self.host,
            port=self.port,
            database=self.db
        )


class CassandraConfig(BaseSettings, env_prefix="CASSANDRA_"):
    host: str = "127.0.0.1"
    port: int = 9042
    keyspace: str = "distcomp_discussion"


class PublisherConfig(BaseSettings, env_prefix="PUBLISHER_"):
    base_url: str = "http://127.0.0.1:24110/api/v1.0"


class DiscussionConfig(BaseSettings, env_prefix="DISCUSSION_"):
    base_url: str = "http://127.0.0.1:24130/api/v1.0"


class KafkaConfig(BaseSettings, env_prefix="KAFKA_"):
    bootstrap_servers: str = "localhost:9092"
    post_rpc_timeout_seconds: float = 1.0


class JwtConfig(BaseSettings, env_prefix="JWT_"):
    secret: SecretStr = SecretStr("change-me-in-production-use-long-random-secret")
    algorithm: str = "HS256"
    access_token_expire_seconds: int = 3600


class RedisConfig(BaseSettings, env_prefix="REDIS_"):
    host: str = "127.0.0.1"
    port: int = 6379
    db: int = 0
    password: SecretStr | None = None
    default_ttl_seconds: int = 300

    def url(self) -> str:
        if self.password is not None:
            pwd = self.password.get_secret_value()
            return f"redis://:{pwd}@{self.host}:{self.port}/{self.db}"
        return f"redis://{self.host}:{self.port}/{self.db}"
