import uvicorn
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from src.discussion.api.posts import router as posts_router
from src.discussion.posts_kafka import discussion_kafka_router
from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException

app = FastAPI(title="Discussion API (Cassandra + Kafka)")
app.include_router(discussion_kafka_router)

API_PREFIX = "/api/v1.0"
app.include_router(posts_router, prefix=API_PREFIX)


@app.exception_handler(EntityNotFoundException)
async def not_found_handler(request: Request, exc: EntityNotFoundException) -> JSONResponse:
    return JSONResponse(
        status_code=404,
        content={"message": str(exc)},
    )


@app.exception_handler(EntityAlreadyExistsException)
async def already_exists_handler(request: Request, exc: EntityAlreadyExistsException) -> JSONResponse:
    return JSONResponse(
        status_code=403,
        content={"message": str(exc)},
    )


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24130, log_level="info")
