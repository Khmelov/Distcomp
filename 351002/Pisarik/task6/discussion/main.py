from __future__ import annotations

import logging
import threading
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.controllers.message_controller import router as message_router

log = logging.getLogger(__name__)

_kafka_stop = threading.Event()
_kafka_thread: threading.Thread | None = None


def _kafka_worker_main() -> None:
    from app.kafka_worker import consume_loop

    consume_loop(_kafka_stop)


@asynccontextmanager
async def lifespan(_app: FastAPI):
    global _kafka_thread
    if _kafka_thread is None or not _kafka_thread.is_alive():
        _kafka_stop.clear()
        _kafka_thread = threading.Thread(target=_kafka_worker_main, name="kafka-in-consumer", daemon=True)
        _kafka_thread.start()
        log.info("Kafka InTopic consumer thread started")
    yield
    _kafka_stop.set()
    if _kafka_thread is not None:
        _kafka_thread.join(timeout=8)


# Cassandra connects lazily on first message request so the server binds immediately
# (avoids startup failure / empty TCP when Cassandra is still booting).

app = FastAPI(title="Discussion module (Cassandra)", version="1.0.0", lifespan=lifespan)


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    return JSONResponse(
        status_code=status.HTTP_400_BAD_REQUEST,
        content={"errorMessage": "Validation error", "errorCode": 40001, "details": exc.errors()},
    )


@app.get("/api/v1.0/health")
async def health_check() -> dict:
    return {"status": "ok"}


app.include_router(message_router)


def get_app() -> FastAPI:
    return app
