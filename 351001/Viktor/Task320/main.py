from fastapi import FastAPI
from sqlalchemy import text

from Task320.src.api.v1 import router_v1
from Task320.src.core.errors import register_error_handlers
from Task320.src.infrastructure.database import engine, Base

app = FastAPI(title="DistComp", version="1.0")

@app.on_event("startup")
async def init_db():
    async with engine.begin() as conn:
        await conn.execute(text("CREATE SCHEMA IF NOT EXISTS distcomp"))
        await conn.execute(text("ALTER USER postgres SET search_path TO distcomp, public"))
        # ПОЛНОСТЬЮ УДАЛЯЕМ И ПЕРЕСОЗДАЁМ ТАБЛИЦЫ
        await conn.run_sync(Base.metadata.drop_all)
        await conn.run_sync(Base.metadata.create_all)
        # Никакой вставки данных!

register_error_handlers(app)
app.include_router(router_v1, prefix="/api")

if __name__ == "__main__":
    import hypercorn.asyncio
    import asyncio
    from hypercorn.config import Config

    config = Config()
    config.bind = ["127.0.0.1:24110"]
    config.use_reloader = True
    asyncio.run(hypercorn.asyncio.serve(app, config))