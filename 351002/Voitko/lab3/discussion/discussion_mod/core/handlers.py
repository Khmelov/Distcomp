from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from discussion_mod.core.errors import AppError


async def app_error_handler(_: Request, exc: AppError) -> JSONResponse:
    return JSONResponse(
        status_code=exc.status_code,
        content={"errorMessage": exc.message, "errorCode": exc.error_code},
    )


async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    msg = "; ".join([f"{e['loc'][-1]}: {e['msg']}" for e in exc.errors()])
    return JSONResponse(
        status_code=400,
        content={
            "errorMessage": f"Validation error: {msg}",
            "errorCode": 40001,
        },
    )


def register_error_handlers(app: FastAPI) -> None:
    app.add_exception_handler(AppError, app_error_handler)
    app.add_exception_handler(RequestValidationError, validation_exception_handler)
