from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from starlette.exceptions import HTTPException as StarletteHTTPException
from app.exceptions.handlers import http_exception_handler, validation_exception_handler
from app.api.v1.api import api_router

app = FastAPI(title="REST API on Python", version="1.0.0")

app.add_exception_handler(StarletteHTTPException, http_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)

app.include_router(api_router, prefix="/api/v1.0")

if __name__ == "__main__":
    import uvicorn
    from app.core.config import APP_HOST, APP_PORT
    uvicorn.run("app.main:app", host=APP_HOST, port=APP_PORT, reload=False)