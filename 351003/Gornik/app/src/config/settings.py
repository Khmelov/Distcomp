from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    # Эти поля остаются обязательными (они есть в docker-compose)
    postgres_user: str
    postgres_password: str
    postgres_db: str
    postgres_host: str
    postgres_port: int

    model_config = SettingsConfigDict(
        env_prefix="",  # без префикса
        case_sensitive=False  # MONGODB_ROOT_USER == mongodb_root_user
    )

@lru_cache
def get_settings() -> Settings:
    return Settings()