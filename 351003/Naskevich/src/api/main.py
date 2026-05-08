import uvicorn

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from src.api.posts_kafka import posts_kafka_router
from src.api.v1 import editors, markers, posts, tweets
from src.database.tables import run_mappers
from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException

run_mappers()

app = FastAPI(title="REST API Lab")
app.include_router(posts_kafka_router)


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


API_PREFIX = "/api/v1.0"

app.include_router(editors.router, prefix=API_PREFIX)
app.include_router(tweets.router, prefix=API_PREFIX)
app.include_router(markers.router, prefix=API_PREFIX)
app.include_router(posts.router, prefix=API_PREFIX)


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=24110, log_level="info")
