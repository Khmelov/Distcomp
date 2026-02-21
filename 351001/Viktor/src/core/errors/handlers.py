from http import HTTPStatus
from fastapi.exceptions import RequestValidationError
from starlette.responses import JSONResponse
from src.core.errors.errors import HttpNotFoundError
from src.core.constants import ErrorStatus


def not_found_handler(_, exc: HttpNotFoundError):
    return JSONResponse(status_code=HTTPStatus.NOT_FOUND, content={"errorMessage": str(exc), "errorCode": exc.error_code})

def validation_exception_handler(_, exc: RequestValidationError):
    return JSONResponse(
        status_code=HTTPStatus.BAD_REQUEST,
        content={"errorMessage": str(exc), "errorCode": ErrorStatus.BAD_REQUEST}
    )