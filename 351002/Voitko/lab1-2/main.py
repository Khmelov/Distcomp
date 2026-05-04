import os
from contextlib import asynccontextmanager

from fastapi import FastAPI

from src.api.v1 import router_v1
from src.core.database import ensure_schema_and_tables
from src.core.errors import register_error_handlers


@asynccontextmanager
async def lifespan(_: FastAPI):
    if os.environ.get("SKIP_DB_INIT") != "1":
        ensure_schema_and_tables()
    yield


app = FastAPI(title="DistComp", version="1.0", lifespan=lifespan)

register_error_handlers(app)

app.include_router(router_v1, prefix="/api")


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="0.0.0.0", port=24110, reload=True)
