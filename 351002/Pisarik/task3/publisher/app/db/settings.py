import os

from pydantic import BaseModel


class Settings(BaseModel):
    # Per assignment: host=localhost port=5432 user=postgres password=postgres schema=distcomp
    # Also: mandatory table prefix `tbl_` is applied in ORM metadata.
    database_url: str = os.getenv(
        "DATABASE_URL",
        "postgresql+psycopg2://postgres:postgres@localhost:5432/distcomp",
    )


settings = Settings()

