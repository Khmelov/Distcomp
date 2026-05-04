import sys
import os
import socket
import multiprocessing
import uvicorn

from sqlalchemy import text
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError

from app.api.v1.router import api_router
from app.api.v2.router import api_router as api_router_v2

from app.core.exceptions import AppException, app_exception_handler, validation_exception_handler
from app.core.redis import init_redis, close_redis
from app.core.database import engine, Base 
from app.core.redis import redis_client

@asynccontextmanager
async def lifespan(app: FastAPI):
    await init_redis()
    
    if os.getenv("DISTCOMP_START_DISCUSSION", "true").lower() == "true":
        if not _port_open("127.0.0.1", 24130):
            p = multiprocessing.Process(target=_run_discussion, daemon=True)
            p.start()

    async with engine.begin() as conn:
        await conn.execute(text("CREATE SCHEMA IF NOT EXISTS distcomp"))
        await conn.execute(text("SET search_path TO distcomp"))
        await conn.run_sync(Base.metadata.create_all)
        
        try:
            await conn.execute(text("ALTER TABLE distcomp.tbl_author ADD COLUMN IF NOT EXISTS role VARCHAR(32) DEFAULT 'CUSTOMER'"))
        except Exception: pass

        if os.getenv("DISTCOMP_RESET_DB", "true").lower() == "true":
            for table in ["tbl_tweet_label", "tbl_notice", "tbl_tweet", "tbl_label", "tbl_author"]:
                await conn.execute(text(f"TRUNCATE TABLE distcomp.{table} RESTART IDENTITY CASCADE"))
    
    if redis_client:
        try:
            await redis_client.flushdb()
        except Exception:
            pass

    yield
    
    await close_redis()

app = FastAPI(
    title="Distibuted Computing Labs by Vlada Kolbeko, 351003", 
    redirect_slashes=False,
    lifespan=lifespan
)

app.add_exception_handler(AppException, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)

app.include_router(api_router, prefix="/api/v1.0")
app.include_router(api_router_v2, prefix="/api/v2.0")

def _port_open(host: str, port: int) -> bool:
    try:
        with socket.create_connection((host, port), timeout=0.2):
            return True
    except OSError:
        return False

def _run_discussion() -> None:
    sys.path.append(os.getcwd())
    
    os.environ.setdefault("DISCUSSION_ENABLE_KAFKA", os.getenv("DISCUSSION_ENABLE_KAFKA", "false"))
    
    print("--- Starting Discussion Module on port 24130 ---")
    try:
        uvicorn.run("discussion.main:app", host="0.0.0.0", port=24130, reload=False, http="h11")
    except Exception as e:
        print(f"!!! Discussion Module failed to start: {e}")

if __name__ == "__main__":
    reload_enabled = os.getenv("UVICORN_RELOAD", "false").lower() == "true"
    uvicorn.run("main:app", host="0.0.0.0", port=24110, reload=reload_enabled, http="h11")