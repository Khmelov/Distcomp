from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    PROJECT_NAME: str = "Discussion Service"
    API_V1_PREFIX: str = "/api/v1.0"

    # Cassandra settings
    CASSANDRA_HOSTS: list = ["localhost"]
    CASSANDRA_PORT: int = 9042
    CASSANDRA_KEYSPACE: str = "distcomp"

    class Config:
        env_file = ".env"


settings = Settings()