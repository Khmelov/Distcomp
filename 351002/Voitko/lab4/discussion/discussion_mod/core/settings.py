from pathlib import Path

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict

_ROOT = Path(__file__).resolve().parents[2]


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=_ROOT / ".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    CASSANDRA_HOSTS: str = Field(default="localhost", description="Comma-separated hosts")
    CASSANDRA_PORT: int = Field(default=9042, ge=1, le=65535)
    CASSANDRA_KEYSPACE: str = Field(default="distcomp", min_length=1)
    CASSANDRA_PROTOCOL_VERSION: int = Field(default=4, ge=4, le=5)

    NOTE_ID_BUCKETS: int = Field(
        default=64,
        ge=4,
        le=256,
        description="id_bucket = id % buckets — равномерное распределение lookup по id",
    )
    KAFKA_ENABLED: bool = Field(default=True)
    KAFKA_BOOTSTRAP_SERVERS: str = Field(default="localhost:9092")
    KAFKA_NOTES_REQUEST_TOPIC: str = Field(default="notes.request")
    KAFKA_NOTES_REPLY_TOPIC: str = Field(default="notes.reply")
    KAFKA_NOTES_DISCUSSION_GROUP_ID: str = Field(default="discussion-notes-group")

    @property
    def cassandra_hosts_list(self) -> list[str]:
        return [h.strip() for h in self.CASSANDRA_HOSTS.split(",") if h.strip()]


settings = Settings()
