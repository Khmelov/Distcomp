import os
import uvicorn

from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError

from discussion.app.core.exceptions import AppException, app_exception_handler, validation_exception_handler
from discussion.app.api.v1.router import api_router
from discussion.app.core.cassandra import cassandra_init, cassandra_shutdown
from discussion.app.messaging.kafka_worker import start_kafka_worker, stop_kafka_worker


app = FastAPI(title="Discussion module", redirect_slashes=False)

app.add_exception_handler(AppException, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)

app.include_router(api_router, prefix="/api/v1.0")


@app.on_event("startup")
async def startup():
    cassandra_init()
    if os.getenv("DISCUSSION_ENABLE_KAFKA", "true").lower() == "true":
        await start_kafka_worker()


@app.on_event("shutdown")
async def shutdown():
    await stop_kafka_worker()
    cassandra_shutdown()


if __name__ == "__main__":
    reload_enabled = os.getenv("UVICORN_RELOAD", "false").lower() == "true"
    uvicorn.run("discussion.main:app", host="0.0.0.0", port=24130, reload=reload_enabled, http="h11")

