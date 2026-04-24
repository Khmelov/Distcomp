from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI

from database import get_session, shutdown
from kafka_handler import kafka_handler
from routers.comment import router as comment_router


@asynccontextmanager
async def life_span(app: FastAPI):
    get_session()
    await kafka_handler.start()
    yield
    await kafka_handler.stop()
    shutdown()


app = FastAPI(lifespan=life_span)

app.include_router(router=comment_router)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24130)
