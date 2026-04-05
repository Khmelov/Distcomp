from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    CASSANDRA_HOST: str = "localhost"
    CASSANDRA_PORT: int = 9042
    KAFKA_BOOTSTRAP_SERVERS: str = "localhost:9092"

    model_config = SettingsConfigDict(
        env_file='.env',
        env_file_encoding='utf-8',
        extra='ignore'
    )


settings = Settings()