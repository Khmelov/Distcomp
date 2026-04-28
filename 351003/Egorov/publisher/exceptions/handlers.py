from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from publisher.exceptions.app_exceptions import AppError


def register_exception_handlers(app: FastAPI) -> None:
    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(  # type: ignore[unused-variable]
        request: Request,
        exc: RequestValidationError,
    ) -> JSONResponse:
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "errorMessage": "Validation error",
                "errorCode": 40001,
                "details": exc.errors(),
            },
        )

    @app.exception_handler(AppError)
    async def app_error_handler(  # type: ignore[unused-variable]
        request: Request,
        exc: AppError,
    ) -> JSONResponse:
        # exc.detail уже в правильном формате
        return JSONResponse(status_code=exc.status_code, content=exc.detail)

