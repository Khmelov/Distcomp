from http import HTTPStatus

from starlette.responses import JSONResponse

from src.core.constants import ErrorStatus
from src.core.errors.errors import ResourceAlreadyExistsError, ResourceNotFoundError


def not_found_handler(_, exc: ResourceNotFoundError) -> JSONResponse:
    return JSONResponse(status_code=HTTPStatus.NOT_FOUND, content={"errorMessage": str(exc), "errorCode": ErrorStatus.NOT_FOUND})

def already_exist_handler(_, exc: ResourceAlreadyExistsError):
    return JSONResponse(status_code=HTTPStatus.FORBIDDEN,content={"errorMessage": str(exc), "errorCode": ErrorStatus.ALREADY_EXIST})