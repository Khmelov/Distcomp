from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from .v1 import editors, markers, posts, tweets
from src.database.tables import run_mappers
from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException

run_mappers()

app = FastAPI(title="REST API Lab")


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
