import asyncio
from fastapi import FastAPI
from db_helper import db_helper
from contextlib import asynccontextmanager
from models import Base
from api_v1 import router as api_v1_router
from kafkaAdapter import consume_and_produce


@asynccontextmanager
async def lifespan(app: FastAPI):
    # await consume_and_produce()
    task = asyncio.create_task(consume_and_produce())
    async with db_helper.engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield

       # Отменяем задачу при завершении
    task.cancel()
    try:
        await task
    except asyncio.CancelledError:
        pass

app = FastAPI(
    lifespan=lifespan,
    title = "Laba 2 service", 
)



app.include_router(api_v1_router, prefix="/api")


# asyncio.run(consume_and_produce())




