from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker, declarative_base

SQLALCHEMY_DATABASE_URL = "postgresql://postgres:postgres@localhost:5432/distcomp"

engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def init_db():
    Base.metadata.create_all(bind=engine)

    with engine.begin() as conn:
        conn.execute(
            text(
                "TRUNCATE TABLE "
                "tbl_tweet_marker, tbl_comment, tbl_tweet, tbl_marker, tbl_writer "
                "RESTART IDENTITY CASCADE;"
            )
        )
