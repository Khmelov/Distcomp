from pydantic_settings import BaseSettings
from pydantic import BaseModel
from pathlib import Path


BASE_DIR = Path(__file__).parent

DB_PATH = BASE_DIR / "db.sqlite3"

class DBsettings(BaseModel):
        url: str = f"sqlite+aiosqlite:///{DB_PATH}"
        echo: bool = True

class Settings(BaseSettings):
        db: DBsettings = DBsettings()




settings = Settings()
