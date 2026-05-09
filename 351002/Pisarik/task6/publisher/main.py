from fastapi import FastAPI, HTTPException, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from sqlalchemy.exc import IntegrityError
from starlette.responses import Response

from app.api.v2_all import router as v2_router
from app.controllers.author_controller import router as author_router
from app.controllers.mark_controller import router as mark_router
from app.controllers.message_controller import router as message_router
from app.controllers.news_controller import router as news_router

app = FastAPI(title="Publisher (v1.0 open + v2.0 JWT)", version="2.0.0")


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    return JSONResponse(
        status_code=status.HTTP_400_BAD_REQUEST,
        content={"errorMessage": "Validation error", "errorCode": 40001, "details": exc.errors()},
    )


@app.exception_handler(IntegrityError)
async def integrity_error_handler(request: Request, exc: IntegrityError) -> JSONResponse:
    return JSONResponse(
        status_code=status.HTTP_403_FORBIDDEN,
        content={"errorMessage": "Duplicate value", "errorCode": 40301},
    )


@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException) -> Response | JSONResponse:
    if exc.status_code == status.HTTP_204_NO_CONTENT or exc.status_code == status.HTTP_304_NOT_MODIFIED:
        return Response(status_code=exc.status_code)
    d = exc.detail
    if isinstance(d, dict) and "errorMessage" in d and "errorCode" in d:
        return JSONResponse(status_code=exc.status_code, content=d)
    if isinstance(d, dict):
        return JSONResponse(status_code=exc.status_code, content=d)
    return JSONResponse(
        status_code=exc.status_code,
        content={"errorMessage": str(d), "errorCode": int(exc.status_code) * 100 + 1},
    )


@app.get("/api/v1.0/health")
async def health_check() -> dict:
    return {"status": "ok"}


app.include_router(author_router)
app.include_router(mark_router)
app.include_router(news_router)
app.include_router(message_router)
app.include_router(v2_router)


def get_app() -> FastAPI:
    return app
