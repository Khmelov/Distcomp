from fastapi import FastAPI, Request, status
from fastapi.exceptions import HTTPException as FastAPIHTTPException
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.config.cassandra_config import cassandra_config
from app.controllers.creator_controller import router as creator_router
from app.controllers.marker_controller import router as marker_router
from app.controllers.notice_controller import router as notice_router
from app.controllers.story_controller import router as story_router
from app.routers.v2.auth_router import router as v2_auth_router
from app.routers.v2.creator_router import router as v2_creator_router
from app.routers.v2.marker_router import router as v2_marker_router
from app.routers.v2.notice_router import router as v2_notice_router
from app.routers.v2.story_router import router as v2_story_router
from app.models.creator import Creator
from app.models.marker import Marker
from app.models.notice import Notice
from app.models.story import Story
from app.repositories.in_memory_repository import InMemoryRepository
from app.repositories.notice_repository import NoticeRepository
from app.services.creator_service import CreatorService
from app.services.marker_service import MarkerService
from app.services.notice_service import NoticeService
from app.services.story_service import StoryService


app = FastAPI(title="Story Management API", version="1.0.0")


creator_repository = InMemoryRepository[Creator]()
marker_repository = InMemoryRepository[Marker]()
story_repository = InMemoryRepository[Story]()
notice_repository = InMemoryRepository[Notice]()

creator_service = CreatorService(creator_repository)
marker_service = MarkerService(marker_repository)
story_service = StoryService(story_repository, creator_repository, marker_repository)
notice_service = None


def _error_body(status_code: int, message: str) -> dict:
    return {"errorMessage": message, "errorCode": status_code * 100}


def _extract_message(detail: object, fallback: str) -> str:
    if isinstance(detail, str) and detail:
        return detail
    if isinstance(detail, dict):
        value = detail.get("errorMessage")
        if isinstance(value, str) and value:
            return value
    return fallback

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    return JSONResponse(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, content=_error_body(422, "Validation error"))


@app.exception_handler(FastAPIHTTPException)
async def http_exception_handler(request: Request, exc: FastAPIHTTPException) -> JSONResponse:
    msg = _extract_message(exc.detail, "Request failed")
    return JSONResponse(status_code=exc.status_code, content=_error_body(exc.status_code, msg))


@app.exception_handler(Exception)
async def unhandled_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content=_error_body(500, "Internal server error"),
    )

@app.on_event("startup")
async def startup():
    global notice_service
    cassandra_config.connect()
    notice_service = NoticeService(NoticeRepository())

@app.on_event("shutdown")
async def shutdown():
    cassandra_config.close()

@app.get("/api/v1.0/health")
async def health_check() -> dict:
    return {"status": "ok"}


app.include_router(creator_router)
app.include_router(marker_router)
app.include_router(story_router)
app.include_router(notice_router)
app.include_router(v2_auth_router)
app.include_router(v2_creator_router)
app.include_router(v2_marker_router)
app.include_router(v2_story_router)
app.include_router(v2_notice_router)


def get_app() -> FastAPI:
    return app


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="127.0.0.1", port=24110, reload=True)

