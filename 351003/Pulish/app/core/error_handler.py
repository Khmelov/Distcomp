from fastapi.responses import JSONResponse
from fastapi import FastAPI
from app.core.exceptions import AppException


def register_exception_handlers(app: FastAPI):
    @app.exception_handler(AppException)
    def handle_app_exception(_, exc: AppException):
        return JSONResponse(
            status_code=exc.status,
            content={
                "errorMessage": exc.message,
                "errorCode": exc.code
            }
        )
