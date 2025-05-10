from pydantic_settings import BaseSettings
from pydantic import BaseModel
import os


# BASE_DIR = Path(__file__).parent

# DB_PATH = BASE_DIR / "db.sqlite3"


class DBsettings(BaseModel):
    # url: str = f"sqlite+aiosqlite:///{DB_PATH}"
    # url: str = "postgresql+asyncpg://postgres:postgres@db:5432/postgres"

    # url: str = "postgresql+asyncpg://postgres:postgres@db:5432/distcomp"
    # url: str = "postgresql+asyncpg://postgres:postgres@127.0.0.1:5432/distcomp"
    url: str = os.getenv("DATABASE_URL")
    echo: bool = False


class Settings(BaseSettings):
    db: DBsettings = DBsettings()


settings = Settings()
