from pydantic_settings import BaseSettings, SettingsConfigDict
from pathlib import Path


class Settings(BaseSettings):
    POSTGRES_USER: str = "postgres"
    POSTGRES_PASSWORD: str = "postgres"
    POSTGRES_HOST: str = "localhost"
    POSTGRES_PORT: str = "5432"
    POSTGRES_DB: str = "distcomp"

    APP_NAME: str = "Task350 REST API"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = False

    DISCUSSION_SERVICE_URL: str = "http://localhost:24130/api/v1.0"

    KAFKA_BOOTSTRAP_SERVERS: str = "localhost:9092"

    REDIS_HOST: str = "localhost"
    REDIS_PORT: int = 6379
    REDIS_DB: int = 0

    @property
    def DATABASE_URL(self) -> str:
        return (
            f"postgresql://{self.POSTGRES_USER}:{self.POSTGRES_PASSWORD}"
            f"@{self.POSTGRES_HOST}:{self.POSTGRES_PORT}/{self.POSTGRES_DB}"
        )

    model_config = SettingsConfigDict(
        env_file=Path(__file__).parent.parent.parent / '.env',
        env_file_encoding='utf-8',
        extra='ignore',
        case_sensitive=False
    )


settings = Settings()
