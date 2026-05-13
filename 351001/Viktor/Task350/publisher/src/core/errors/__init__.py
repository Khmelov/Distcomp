from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from sqlalchemy.exc import IntegrityError
from Task350.publisher.src.core.errors.exceptions import HttpNotFoundError, HttpBadRequestError, HttpForbiddenError
from Task350.publisher.src.core.errors.handlers import (
    not_found_handler,
    validation_exception_handler,
    integrity_error_handler,
    forbidden_error_handler,
)

from enum import IntEnum
from http import HTTPStatus

__all__ = ["ErrorStatus"]

class ErrorStatus(IntEnum):
    NOT_FOUND = HTTPStatus.NOT_FOUND.value * 100 + 1
    BAD_REQUEST = HTTPStatus.BAD_REQUEST.value * 100 + 1   # 40001
    FORBIDDEN = HTTPStatus.FORBIDDEN.value * 100 + 1       # 40301

def register_error_handlers(app: FastAPI) -> None:
    app.add_exception_handler(HttpNotFoundError, not_found_handler)
    app.add_exception_handler(HttpBadRequestError, validation_exception_handler)  # пример
    app.add_exception_handler(HttpForbiddenError, forbidden_error_handler)
    app.add_exception_handler(RequestValidationError, validation_exception_handler)
    app.add_exception_handler(IntegrityError, integrity_error_handler)