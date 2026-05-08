from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from sqlalchemy.exc import IntegrityError

from app.controllers.author_controller import router as author_router
from app.controllers.mark_controller import router as mark_router
from app.controllers.message_controller import router as message_router
from app.controllers.news_controller import router as news_router

app = FastAPI(title="Publisher module (PostgreSQL)", version="1.0.0")

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    return JSONResponse(
        status_code=status.HTTP_400_BAD_REQUEST,
        content={"errorMessage": "Validation error", "errorCode": 40001, "details": exc.errors()},
    )


@app.exception_handler(IntegrityError)
async def integrity_error_handler(request: Request, exc: IntegrityError) -> JSONResponse:
    # Tests expect 403 for duplicates (login/title)
    return JSONResponse(
        status_code=status.HTTP_403_FORBIDDEN,
        content={"errorMessage": "Duplicate value", "errorCode": 40301},
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

