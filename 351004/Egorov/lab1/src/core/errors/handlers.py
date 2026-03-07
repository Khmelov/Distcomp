from http import HTTPStatus

from starlette.responses import JSONResponse

from lab1.src.core.errors.errors import HttpNotFoundError


def not_found_handler(_, exc: HttpNotFoundError):
    return JSONResponse(status_code=HTTPStatus.NOT_FOUND, content={"errorMessage": str(exc), "errorCode": exc.error_code})