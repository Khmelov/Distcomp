from fastapi import HTTPException, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException


def _code(http_status: int, suffix: int = 1) -> str:
    return f"{http_status:03d}{suffix:02d}"


def v2_error_payload(http_status: int, message: str, suffix: int = 1) -> dict:
    return {
        "errorMessage": message,
        "errorCode": _code(http_status, suffix),
    }


class V2HTTPException(Exception):
    def __init__(self, status_code: int, message: str, *, code_suffix: int = 1):
        self.status_code = status_code
        self.message = message
        self.code_suffix = code_suffix


def is_v2_path(request: Request) -> bool:
    return request.url.path.startswith("/api/v2.0")


def install_v2_exception_handlers(app) -> None:
    @app.exception_handler(V2HTTPException)
    async def v2_exc_handler(request: Request, exc: V2HTTPException) -> JSONResponse:
        return JSONResponse(
            status_code=exc.status_code,
            content=v2_error_payload(exc.status_code, exc.message, exc.code_suffix),
        )

    @app.exception_handler(HTTPException)
    async def http_v2_handler(request: Request, exc: HTTPException) -> JSONResponse:
        if not is_v2_path(request):
            return JSONResponse(
                status_code=exc.status_code,
                content={"message": str(exc.detail)},
            )
        return JSONResponse(
            status_code=exc.status_code,
            content=v2_error_payload(exc.status_code, str(exc.detail)),
        )

    @app.exception_handler(RequestValidationError)
    async def validation_v2(request: Request, exc: RequestValidationError) -> JSONResponse:
        if not is_v2_path(request):
            return JSONResponse(status_code=422, content={"detail": exc.errors()})
        msg = exc.errors()[0].get("msg", "Validation error") if exc.errors() else "Validation error"
        return JSONResponse(
            status_code=422,
            content=v2_error_payload(422, str(msg), 1),
        )

    @app.exception_handler(EntityNotFoundException)
    async def entity_not_found_v2(request: Request, exc: EntityNotFoundException) -> JSONResponse:
        if not is_v2_path(request):
            return JSONResponse(status_code=404, content={"message": str(exc)})
        return JSONResponse(
            status_code=404,
            content=v2_error_payload(404, str(exc), 1),
        )

    @app.exception_handler(EntityAlreadyExistsException)
    async def entity_exists_v2(request: Request, exc: EntityAlreadyExistsException) -> JSONResponse:
        if not is_v2_path(request):
            return JSONResponse(status_code=403, content={"message": str(exc)})
        return JSONResponse(
            status_code=403,
            content=v2_error_payload(403, str(exc), 1),
        )
