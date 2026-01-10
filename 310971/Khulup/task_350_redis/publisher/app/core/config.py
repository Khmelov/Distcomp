import os
from typing import Optional

DATABASE_URL: str = os.getenv(
    "DATABASE_URL",
    "postgresql://postgres:postgres@localhost:5432/distcomp"
)

APP_HOST: str = os.getenv("APP_HOST", "0.0.0.0")
APP_PORT: int = int(os.getenv("APP_PORT", "24110"))

DB_SCHEMA: str = "distcomp"

REDIS_URL: str = os.getenv(
    "REDIS_URL", 
    "redis://localhost:6379/0"
)
REDIS_CACHE_TTL: int = int(os.getenv("REDIS_CACHE_TTL", "3600"))
