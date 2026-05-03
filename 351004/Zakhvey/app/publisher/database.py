from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base

# Подключаемся строго к БД distcomp
DATABASE_URL = "postgresql://postgres:postgres@localhost:5432/distcomp"

engine = create_engine(DATABASE_URL, echo=False)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()