from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.controllers.author_controller import router as author_router
from app.controllers.mark_controller import router as mark_router
from app.controllers.message_controller import router as message_router
from app.controllers.news_controller import router as news_router
from app.models.author import Author
from app.models.mark import Mark
from app.models.message import Message
from app.models.news import News
from app.repositories.in_memory_repository import InMemoryRepository
from app.services.author_service import AuthorService
from app.services.mark_service import MarkService
from app.services.message_service import MessageService
from app.services.news_service import NewsService

app = FastAPI(title="News Management API", version="1.0.0")

author_repository = InMemoryRepository[Author]()
mark_repository = InMemoryRepository[Mark]()
news_repository = InMemoryRepository[News]()
message_repository = InMemoryRepository[Message]()

author_service = AuthorService(author_repository)
mark_service = MarkService(mark_repository)
news_service = NewsService(news_repository, author_repository, mark_repository)
message_service = MessageService(message_repository, news_repository)

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    return JSONResponse(
        status_code=status.HTTP_400_BAD_REQUEST,
        content={"errorMessage": "Validation error", "errorCode": 40001, "details": exc.errors()},
    )

@app.get("/api/v1.0/health")
async def health_check() -> dict:
    return {"status": "ok"}

app.include_router(author_router)
app.include_router(mark_router)
app.include_router(news_router)
app.include_router(message_router)

def get_app() -> FastAPI:
    return app

