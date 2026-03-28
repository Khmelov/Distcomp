from contextlib import asynccontextmanager

from fastapi import FastAPI

from src.core.database import engine
from src.core.kafka import init_kafka_producer, close_kafka_producer
from src.domain.models import Base
from src.api.v1 import router_v1
from src.core.errors import register_error_handlers


@asynccontextmanager
async def lifespan(_: FastAPI):
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)
        await conn.run_sync(Base.metadata.create_all)

    await init_kafka_producer()
    yield
    await close_kafka_producer()
    await engine.dispose()

app = FastAPI(title="DistComp", version="1.0", lifespan=lifespan)

# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=[
#         "*",
#     ],
#     allow_credentials=True,
#     allow_methods=["*"],
#     allow_headers=["*"],
# )

register_error_handlers(app)

app.include_router(router_v1, prefix="/api")