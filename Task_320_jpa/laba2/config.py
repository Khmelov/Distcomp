from pydantic_settings import BaseSettings
from pydantic import BaseModel, Field
from urllib.parse import quote_plus
import os


class DBsettings(BaseModel):
    # Используем Field с default_factory для динамических значений
    url: str = Field(default_factory=lambda: os.getenv("DATABASE_URL", "postgresql+asyncpg://postgres:postgres@localhost:5432/distcomp"))
    echo: bool = True
    
    # Или создайте property для кодированного URL
    @property
    def encoded_url(self):
        if self.url and "postgres" in self.url:
            # Простая логика для кодирования пароля, если нужно
            return self.url
        return self.url


class Settings(BaseSettings):
    db: DBsettings = DBsettings()
    
    class Config:
        env_file = ".env"


settings = Settings()
