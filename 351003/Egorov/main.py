from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.config.cassandra_config import cassandra_config
from app.controllers.creator_controller import router as creator_router
from app.controllers.marker_controller import router as marker_router
from app.controllers.notice_controller import router as notice_router
from app.controllers.story_controller import router as story_router
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

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    return JSONResponse(
        status_code=status.HTTP_400_BAD_REQUEST,
        content={"errorMessage": "Validation error", "errorCode": 40001, "details": exc.errors()},
    )

@app.on_event("startup")
async def startup():
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


def get_app() -> FastAPI:
    return app


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="127.0.0.1", port=24110, reload=True)

