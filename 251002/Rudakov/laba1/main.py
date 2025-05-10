
from fastapi import FastAPI
from db_helper import db_helper
from contextlib import asynccontextmanager
from models import Base
from api_v1 import router as api_v1_router





@asynccontextmanager
async def lifespan(app: FastAPI):
    async with db_helper.engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield

app = FastAPI(
    lifespan=lifespan,
    title = "Laba 1 service", 
)



app.include_router(api_v1_router, prefix="/api")