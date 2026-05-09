from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict

class MongoSettings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="MONGO_")

    host: str = Field(default="localhost")
    port: int = Field(default=5432, ge=1, le=65535)
    user: str = Field(default=None, min_length=1)
    password: str = Field(default=None, min_length=1)
    name: str = Field(default=None, min_length=1)

    @property
    def get_database_url(self) -> str:
        return f"mongodb://{self.user}:{self.password}@{self.host}:{self.port}"


class Settings(BaseSettings):
    mongo: MongoSettings = MongoSettings()
    DEBUG: bool = Field(default=True)

settings = Settings()