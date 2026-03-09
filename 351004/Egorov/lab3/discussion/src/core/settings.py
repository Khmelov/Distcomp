from pydantic import Field
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    DB_HOST: str = Field(validation_alias="MONGO_HOST", default="localhost")
    DB_PORT: int = Field(validation_alias="MONGO_PORT", default=5432, ge=1, le=65535)
    DB_USER: str = Field(validation_alias="MONGO_USER", default=None, min_length=1)
    DB_PASSWORD: str = Field(validation_alias="MONGO_PASSWORD", default=None, min_length=1)
    DB_NAME: str = Field(validation_alias="MONGO_DB", default=None, min_length=1)

    DEBUG: bool = Field(default=True)

    @property
    def get_database_url(self) -> str:
        return f"mongodb://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}"

settings = Settings()