from fastapi.exceptions import RequestValidationError
from Task350.publisher.src.core.errors.exceptions import HttpNotFoundError
from Task350.publisher.src.core.constants import ErrorStatus
from Task350.publisher.src.core.errors.exceptions import HttpForbiddenError

def forbidden_error_handler(_, exc: HttpForbiddenError):
    return JSONResponse(
        status_code=HTTPStatus.FORBIDDEN,
        content={"errorMessage": str(exc), "errorCode": exc.error_code}
    )

def not_found_handler(_, exc: HttpNotFoundError):
    return JSONResponse(status_code=HTTPStatus.NOT_FOUND, content={"errorMessage": str(exc), "errorCode": exc.error_code})

def validation_exception_handler(_, exc: RequestValidationError):
    return JSONResponse(
        status_code=HTTPStatus.BAD_REQUEST,
        content={"errorMessage": str(exc), "errorCode": ErrorStatus.BAD_REQUEST}
    )

from sqlalchemy.exc import IntegrityError
from starlette.responses import JSONResponse
from http import HTTPStatus

async def integrity_error_handler(_, exc: IntegrityError):
    # Проверяем SQLSTATE код ошибки (23505 = unique violation)
    if hasattr(exc.orig, 'pgcode') and exc.orig.pgcode == '23505':
        return JSONResponse(
            status_code=HTTPStatus.FORBIDDEN,
            content={"errorMessage": "Login already exists", "errorCode": 40301}
        )
    else:
        return JSONResponse(
            status_code=HTTPStatus.BAD_REQUEST,
            content={"errorMessage": "Data integrity violation", "errorCode": 40002}
        )