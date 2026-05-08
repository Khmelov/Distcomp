import os
import socket
import multiprocessing
import uvicorn

from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError

from app.core.exceptions import AppException, app_exception_handler, validation_exception_handler
from app.api.v1.router import api_router

from app.core.database import engine, Base 
import sqlalchemy

app = FastAPI(title="Distibuted Computing Labs by Vlada Kolbeko, 351003", redirect_slashes=False)

app.add_exception_handler(AppException, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)

app.include_router(api_router, prefix="/api/v1.0")

@app.on_event("startup")
async def startup():
    if os.getenv("DISTCOMP_START_DISCUSSION", "true").lower() == "true":
        if not _port_open("127.0.0.1", 24130):
            p = multiprocessing.Process(target=_run_discussion, daemon=True)
            p.start()

    async with engine.begin() as conn:
        await conn.execute(sqlalchemy.text("CREATE SCHEMA IF NOT EXISTS distcomp"))
        await conn.execute(sqlalchemy.text("SET search_path TO distcomp"))
        await conn.run_sync(Base.metadata.create_all)

        if os.getenv("DISTCOMP_RESET_DB", "true").lower() == "true":
            await conn.execute(sqlalchemy.text("TRUNCATE TABLE distcomp.tbl_tweet_label RESTART IDENTITY CASCADE"))
            await conn.execute(sqlalchemy.text("TRUNCATE TABLE distcomp.tbl_notice RESTART IDENTITY CASCADE"))
            await conn.execute(sqlalchemy.text("TRUNCATE TABLE distcomp.tbl_tweet RESTART IDENTITY CASCADE"))
            await conn.execute(sqlalchemy.text("TRUNCATE TABLE distcomp.tbl_label RESTART IDENTITY CASCADE"))
            await conn.execute(sqlalchemy.text("TRUNCATE TABLE distcomp.tbl_author RESTART IDENTITY CASCADE"))


def _port_open(host: str, port: int) -> bool:
    try:
        with socket.create_connection((host, port), timeout=0.2):
            return True
    except OSError:
        return False


def _run_discussion() -> None:
    os.environ.setdefault("DISCUSSION_ENABLE_KAFKA", os.getenv("DISCUSSION_ENABLE_KAFKA", "false"))
    uvicorn.run("discussion.main:app", host="0.0.0.0", port=24130, reload=False, http="h11")


if __name__ == "__main__":
    reload_enabled = os.getenv("UVICORN_RELOAD", "false").lower() == "true"
    uvicorn.run("main:app", host="0.0.0.0", port=24110, reload=reload_enabled, http="h11")