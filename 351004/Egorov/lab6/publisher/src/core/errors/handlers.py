from http import HTTPStatus

from starlette import status
from starlette.responses import JSONResponse

from src.core.constants import ErrorStatus
from .errors import ResourceAlreadyExistsError, ResourceNotFoundError, UnauthorizedError, ForbiddenError


def not_found_handler(_, exc: ResourceNotFoundError) -> JSONResponse:
    return JSONResponse(status_code=HTTPStatus.NOT_FOUND, content={"errorMessage": str(exc), "errorCode": ErrorStatus.NOT_FOUND})

def already_exist_handler(_, exc: ResourceAlreadyExistsError):
    return JSONResponse(status_code=HTTPStatus.FORBIDDEN, content={"errorMessage": str(exc), "errorCode": ErrorStatus.ALREADY_EXIST})

def unauthorized_handler(_, exc: UnauthorizedError):
    return JSONResponse(status_code=status.HTTP_401_UNAUTHORIZED, content={"errorMessage": str(exc), "errorCode": ErrorStatus.UNAUTHORIZED})

def forbidden_handler(_, exc: ForbiddenError):
    return JSONResponse(status_code=status.HTTP_403_FORBIDDEN, content={"errorMessage": str(exc), "errorCode": ErrorStatus.FORBIDDEN})