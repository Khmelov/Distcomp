from fastapi import FastAPI, Request
from src.api.v1.reactions import router as reactions_router
from src.db.cassandra import init_db
import logging
from contextlib import asynccontextmanager
from src.utils.kafka_client import start_kafka_consumer


@asynccontextmanager
async def lifespan(app: FastAPI):
    init_db()
    await start_kafka_consumer()
    yield

app = FastAPI(title="Discussion Service", version="1.0.0", lifespan=lifespan)

app.include_router(reactions_router)

logging.basicConfig(level=logging.INFO)

@app.middleware("http")
async def log_requests(request: Request, call_next):
    body_bytes = await request.body()
    body_str = body_bytes.decode("utf-8")
    logging.info(f"Incoming request {request.method} {request.url.path} with body: {body_str}")

    # async def receive() -> dict:
    #     return {"type": "http.request"}
    #
    # request._receive = receive

    response = await call_next(request)
    return response